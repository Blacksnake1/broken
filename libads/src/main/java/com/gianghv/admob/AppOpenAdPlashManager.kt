package com.gianghv.admob

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import com.gianghv.utils.ConstantsAds
import com.gianghv.utils.Utils
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.Date


class AppOpenAdPlashManager constructor(
    private val context: Context,
    private val idOpenAds: String,
) {
    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false

    private var handler: Handler? = null
    private var runable: Runnable? = null
    private var isPassed = false

    companion object {
        var isShowingAd = false
    }

    private var loadTime: Long = 0


    @RequiresApi(Build.VERSION_CODES.M)
    fun requestAds(onAdLoader: (() -> Unit)?, onAdLoadFail: (() -> Unit)?) {
        if (isLoadingAd || isAdAvailable() || !Utils.isNetworkConnected(context)) {
            onAdLoadFail?.invoke()
            return
        }

        handler = Handler(Looper.getMainLooper())
        runable = Runnable {
            if (handler != null) {
                runable?.let { handler?.removeCallbacks(it) }
                handler = null
                if (!isPassed)
                    isPassed = true
                onAdLoadFail?.invoke()
            }
        }
        handler?.postDelayed(runable!!, ConstantsAds.TIME_OUT)

        isLoadingAd = true
        loadAdsPrepare(idOpenAds, onAdLoader = { onAdLoader?.invoke() }, onAdLoadFail = {
            runable?.let { handler?.removeCallbacks(it) }
            handler = null
            if (!isPassed) {
                isPassed = true
                onAdLoadFail?.invoke()
            }
        })
    }

    private fun loadAdsPrepare(
        idAds: String, onAdLoader: (() -> Unit)? = null, onAdLoadFail: (() -> Unit)? = null,
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
                    onAdLoader?.invoke()
                    runable?.let { handler?.removeCallbacks(it) }
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
        activity: Activity,
        onAdClose: (() -> Unit),
        onAdShowing: (() -> Unit)
    ) {
        runable?.let { handler?.removeCallbacks(it) }
        handler = null
        showAdIfAvailable(activity, object : OnShowAdCompleteListener {
            override fun onShowAdComplete() {
                isShowingAd = false
                onAdClose.invoke()
                // Empty because the user will go back to the activity that shows the ad.
            }

            override fun onShowingAd() {
                onAdShowing.invoke()
            }
        })
    }

    private fun showAdIfAvailable(
        activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener,
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
                onShowAdCompleteListener.onShowingAd()
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
        fun onShowingAd()
    }
}
