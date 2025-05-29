package com.gianghv.max

import android.app.Activity
import android.os.Build
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.sdk.AppLovinSdkUtils
import com.gianghv.utils.Utils

class MaxAdaptiveBannerManager(
    private val activity: Activity,
    private val mIdBannerMax: String
) {
    private val TAG: String = "MaxAdaptiveBanner"
    var adViewMax: MaxAdView? = null
    var isBannerLoaded = false

    @RequiresApi(Build.VERSION_CODES.M)
    fun loadBanner(
        parent: ViewGroup?,
        onAdLoader: ((MaxAdView?) -> Unit)? = null,
        onAdLoadFail: (() -> Unit)? = null
    ) {
        if (!Utils.isNetworkConnected(activity)) {
            onAdLoadFail?.invoke()
            return
        }
        requestBannerAdsPrepare(mIdBannerMax, parent, onAdLoader, onAdLoadFail = {
            onAdLoadFail?.invoke()
        })
    }

    private fun requestBannerAdsPrepare(
        idBanner: String,
        parent: ViewGroup?,
        onAdLoader: ((MaxAdView?) -> Unit)? = null,
        onAdLoadFail: (() -> Unit)? = null
    ) {
        adViewMax = MaxAdView(idBanner, activity)
        val listener: MaxAdViewAdListener = object : MaxAdViewAdListener {
            override fun onAdExpanded(ad: MaxAd) {}
            override fun onAdCollapsed(ad: MaxAd) {}
            override fun onAdDisplayed(ad: MaxAd) {}
            override fun onAdHidden(ad: MaxAd) {}
            override fun onAdClicked(ad: MaxAd) {}
            override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
                onAdLoadFail?.invoke()
            }
            override fun onAdLoaded(ad: MaxAd) {
                isBannerLoaded = true
                parent?.removeAllViews()
                parent?.addView(adViewMax)
                onAdLoader?.invoke(adViewMax)
            }

            override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                onAdLoadFail?.invoke()
            }
        }
        adViewMax?.setListener(listener)
        if (adViewMax?.parent != null) {
            (adViewMax?.parent as ViewGroup).removeAllViews()
        }
        parent?.addView(adViewMax)

        val isTablet = AppLovinSdkUtils.isTablet(activity)
        val heightPx = AppLovinSdkUtils.dpToPx(activity, if (isTablet) 90 else 50)
        adViewMax?.layoutParams =
            FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightPx)
        adViewMax?.setRevenueListener {
            Utils.postRevenueMaxToAdjust(it, it.adUnitId)
        }
        adViewMax?.loadAd()
    }

    private fun loadAdViewToParent(parent: ViewGroup?) {
        if (adViewMax?.parent != null) {
            (adViewMax?.parent as ViewGroup).removeAllViews()
        }
        parent?.removeAllViews()
        parent?.addView(adViewMax)
    }
}