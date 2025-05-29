package com.gianghv.max

import android.app.Activity
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.gianghv.utils.ConstantsAds
import com.gianghv.utils.Utils

class MaxInterstitialSingleReqAdManager constructor(
    private val activity: Activity,
    private val mIdAdsFullMax: String
) {
    var isShowingAds = false
    var interstitialAd: MaxInterstitialAd? = null
    var handler: Handler? = null
    var runable: Runnable? = null

    @RequiresApi(Build.VERSION_CODES.M)
    fun requestAds(
        onLoadAdSuccess: (() -> Unit)? = null,
        onAdLoadFail: (() -> Unit)? = null,
    ) {
        if (!Utils.isNetworkConnected(activity)) {
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
        handler?.postDelayed(runable!!, ConstantsAds.TIME_OUT)

        loadAds(onAdLoader = {
            onLoadAdSuccess?.invoke()
        }, onAdLoadFail)
    }

    private fun loadAds(
        onAdLoader: (() -> Unit)? = null,
        onAdLoadFail: (() -> Unit)? = null
    ) {
        requestAdsPrepare(mIdAdsFullMax, onAdLoader, onAdLoadFail = {
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
        interstitialAd = MaxInterstitialAd(idAds, activity)
        interstitialAd?.setListener(object : MaxAdListener {
            override fun onAdLoaded(ad: MaxAd) {
                runable?.let { handler?.removeCallbacks(it) }
                if (handler == null) {
                    return
                }
                onLoadAdSuccess?.invoke()
                onAdLoader?.invoke()
                handler = null
            }

            override fun onAdDisplayed(ad: MaxAd) {
                interstitialAd = null
            }

            override fun onAdHidden(ad: MaxAd) {
                isShowingAds = false
            }

            override fun onAdClicked(ad: MaxAd) {}
            override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                interstitialAd = null
                onAdLoadFail?.invoke()
            }

            override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
                onAdLoadFail?.invoke()
            }
        })
        interstitialAd?.loadAd()
    }

    fun show(
        onShowAdsFinish: (() -> Unit)? = null,
        onShowingAds: (() -> Unit)? = null,
        onAdsClicked: (() -> Unit)? = null,
    ) {
        if (interstitialAd != null) {
            isShowingAds = true
            runable?.let { handler?.removeCallbacks(it) }
            handler = null
            interstitialAd?.setListener(object : MaxAdListener {
                override fun onAdLoaded(ad: MaxAd) {}
                override fun onAdDisplayed(ad: MaxAd) {
                    onShowingAds?.invoke()
                }

                override fun onAdHidden(ad: MaxAd) {
                    isShowingAds = false
                    onShowAdsFinish?.invoke()
                }

                override fun onAdClicked(ad: MaxAd) {
                    onAdsClicked?.invoke()
                }
                override fun onAdLoadFailed(adUnitId: String, error: MaxError) {}
                override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
                    onShowAdsFinish?.invoke()
                }
            })
            interstitialAd?.setRevenueListener {
                Utils.postRevenueMaxToAdjust(it, it.adUnitId)
            }
            interstitialAd?.showAd()
        } else {
            onShowAdsFinish?.invoke()
        }
    }
}