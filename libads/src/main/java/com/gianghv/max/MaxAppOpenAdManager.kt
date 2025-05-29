package com.gianghv.max

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAppOpenAd
import com.applovin.sdk.AppLovinSdk
import com.gianghv.utils.Utils


class MaxAppOpenAdManager constructor(
    private val context: Context,
    private val idOpenAds01: String,
) {
    private var appOpenAd: MaxAppOpenAd? = null

    private var isLoadingAd = false

    var handler: Handler? = null
    var runable: Runnable? = null

    companion object {
        var isShowingAd = false
    }

    private var loadTime: Long = 0

    fun loadAd(onAdLoader: (() -> Unit)?, onAdLoadFail: (() -> Unit)?) {
        if (isLoadingAd) {
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

        loadAdsPrepare(idOpenAds01, onAdLoader = { onAdLoader?.invoke() }, onAdLoadFail = {
            runable?.let { handler?.removeCallbacks(it) }
            handler = null
            onAdLoadFail?.invoke()
        })
    }

    private fun loadAdsPrepare(
        idAds: String, onAdLoader: (() -> Unit)? = null, onAdLoadFail: (() -> Unit)? = null
    ) {
        appOpenAd = MaxAppOpenAd(idAds, context)
        appOpenAd?.setListener(object : MaxAdListener {
            override fun onAdLoaded(p0: MaxAd) {
                runable?.let { handler?.removeCallbacks(it) }
                if (handler == null) {
                    return
                }
                onAdLoader?.invoke()
                handler = null
            }

            override fun onAdDisplayed(p0: MaxAd) {
            }

            override fun onAdHidden(p0: MaxAd) {
            }

            override fun onAdClicked(p0: MaxAd) {

            }

            override fun onAdLoadFailed(p0: String, p1: MaxError) {
                isLoadingAd = false
                onAdLoadFail?.invoke()
            }

            override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                isLoadingAd = false
            }
        })
        appOpenAd?.loadAd()
    }


    fun showAdIfAvailable(
        onShowAdCompleteListener: OnShowAdCompleteListener
    ) {
        if (isShowingAd) {
            return
        }

        if (appOpenAd == null || !AppLovinSdk.getInstance(context).isInitialized) return

        if (appOpenAd?.isReady == true) {
            appOpenAd?.setListener(object : MaxAdListener {
                override fun onAdLoaded(p0: MaxAd) {

                }

                override fun onAdDisplayed(p0: MaxAd) {
                }

                override fun onAdHidden(p0: MaxAd) {
                    isShowingAd = false
                    onShowAdCompleteListener.onShowAdComplete()
                }

                override fun onAdClicked(p0: MaxAd) {
                }

                override fun onAdLoadFailed(p0: String, p1: MaxError) {
                    isShowingAd = false
                }

                override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                    isShowingAd = false
                    onShowAdCompleteListener.onShowAdComplete()
                }
            })
            appOpenAd?.setRevenueListener {
                Utils.postRevenueMaxToAdjust(it, it.adUnitId)
            }
            appOpenAd?.showAd()
        }
    }

    interface OnShowAdCompleteListener {
        fun onShowAdComplete()
    }
}
