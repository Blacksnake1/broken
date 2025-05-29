package com.gianghv.admob

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import com.gianghv.utils.ConstantsAds.TIME_OUT
import com.gianghv.utils.Utils
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


class InterstitialSingleReqAdManager constructor(
    private val context: Context,
    private val mIdAdsFull: String
) {
    companion object {
        var isShowingAds = false
    }

    private var mInterstitialAd: InterstitialAd? = null
    var handler: Handler? = null
    var runable: Runnable? = null

    @RequiresApi(Build.VERSION_CODES.M)
    fun requestAds(
        onLoadAdSuccess: (() -> Unit)? = null,
        onAdLoadFail: (() -> Unit)? = null,
    ) {

        val requestConfiguration = RequestConfiguration.Builder().build()
        MobileAds.setRequestConfiguration(requestConfiguration)
        if (!Utils.isNetworkConnected(context)) {
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
        handler?.postDelayed(runable!!, TIME_OUT)

        loadAds(onAdLoader = {
            onLoadAdSuccess?.invoke()
        }, onAdLoadFail)
    }

    private fun loadAds(
        onAdLoader: (() -> Unit)? = null,
        onAdLoadFail: (() -> Unit)? = null
    ) {
        requestAdsPrepare(mIdAdsFull, onAdLoader, onAdLoadFail = {
            runable?.let { handler?.removeCallbacks(it) }
            handler = null
            onAdLoadFail?.invoke()
        })
    }

    private fun requestAdsPrepare(
        idAds: String,
        onLoadAdSuccess: (() -> Unit)? = null,
        onAdLoader: (() -> Unit)? = null,
        onAdLoadFail: (() -> Unit)? = null
    ) {
        if (handler == null) {
            return
        }
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, idAds, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
                onAdLoadFail?.invoke()
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                runable?.let { handler?.removeCallbacks(it) }
                mInterstitialAd = interstitialAd
                if (handler == null) {
                    return
                }
                onLoadAdSuccess?.invoke()
                onAdLoader?.invoke()
                handler = null
            }
        })
    }

    fun showAds(
        activity: Activity,
        onAdClose: (() -> Unit)? = null,
        onAdShowing: (() -> Unit)? = null,
        onAdClicked: (() -> Unit)? = null,

        ) {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    onAdClose?.invoke()
                    isShowingAds = false
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    isShowingAds = false
                    mInterstitialAd = null
                    onAdClose?.invoke()
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    onAdClicked?.invoke()
                }


                override fun onAdShowedFullScreenContent() {
                    onAdShowing?.invoke()
                    handler = null
                    mInterstitialAd = null
                    isShowingAds = true
                }
            }
            mInterstitialAd?.setOnPaidEventListener {
                Utils.postRevenueAdjust(it, mInterstitialAd?.adUnitId)
            }
            mInterstitialAd?.show(activity) ?: onAdClose?.invoke()
        } else {
            isShowingAds = false
            onAdClose?.invoke()
        }
    }
}