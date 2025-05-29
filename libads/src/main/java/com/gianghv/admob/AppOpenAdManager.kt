package com.gianghv.admob

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.gianghv.utils.Utils
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.*



class AppOpenAdManager constructor(
    private val context: Context,
    private val idOpenAds: String,
) {
    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false

    var handler: Handler? = null
    var runable: Runnable? = null

    companion object{
        var isShowingAd = false
    }
    private var loadTime: Long = 0

    fun loadAd(onAdLoader: (() -> Unit)?, onAdLoadFail: (() -> Unit)?) {
        val requestConfiguration = RequestConfiguration.Builder().build()
        MobileAds.setRequestConfiguration(requestConfiguration)
        if (isLoadingAd || isAdAvailable()) {
            onAdLoadFail?.invoke()
            return
        }

        handler = Handler(Looper.getMainLooper())
        runable = Runnable {
            if (handler != null) {
                onAdLoadFail?.invoke()
                handler = null
            }
        }
        handler?.postDelayed(runable!!, 10000)

        isLoadingAd = true

        loadAdsPrepare(idOpenAds, onAdLoader = { onAdLoader?.invoke() }, onAdLoadFail = {
            runable?.let { handler?.removeCallbacks(it) }
            handler = null
            onAdLoadFail?.invoke()
        })


    }

    private fun loadAdsPrepare(
        idAds: String, onAdLoader: (() -> Unit)? = null, onAdLoadFail: (() -> Unit)? = null
    ) {
        val request = AdRequest.Builder().build()
        AppOpenAd.load(context,
            idAds,
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                    runable?.let { handler?.removeCallbacks(it) }
                    if (handler == null) {
                        return
                    }
                    onAdLoader?.invoke()
                    handler = null
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoadingAd = false
                    onAdLoadFail?.invoke()
                }
            })
    }

    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    fun showAdIfAvailable(
        activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener
    ) {
        if (isShowingAd) {
            return
        }

        if (!isAdAvailable()) {
            onShowAdCompleteListener.onShowAdComplete()
            return
        }

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                isShowingAd = false
                onShowAdCompleteListener.onShowAdComplete()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
                isShowingAd = false
                onShowAdCompleteListener.onShowAdComplete()
            }

            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
            }
        }
        appOpenAd?.setOnPaidEventListener {
            Utils.postRevenueAdjust(it, "OpenAds")
        }
        appOpenAd?.show(activity)
    }

    interface OnShowAdCompleteListener {
        fun onShowAdComplete()
    }
}
