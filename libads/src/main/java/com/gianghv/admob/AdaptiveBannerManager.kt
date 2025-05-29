package com.gianghv.admob

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.gianghv.utils.Utils
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdView

class AdaptiveBannerManager(
    private val context: Activity,
    private var isCollapse: Boolean,
    private val mIdBanner: String,

    ) {
    var adView: AdManagerAdView? = null
    var isBannerLoaded = false

    private val adSize: AdSize
        get() {
            val display = context.windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = context.resources.displayMetrics.widthPixels.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
        }

    @RequiresApi(Build.VERSION_CODES.M)
    fun loadBanner(
        parent: ViewGroup?,
        onAdLoader: ((AdManagerAdView?) -> Unit)? = null,
        onAdLoadFail: (() -> Unit)? = null,
        actionClicked: (() -> Unit)? = null
    ) {
        if (!Utils.isNetworkConnected(context)) {
            onAdLoadFail?.invoke()
            return
        }
        requestBannerAdsPrepare(mIdBanner, parent, onAdLoader,
            onAdLoadFail = {
                onAdLoadFail?.invoke()
            }, actionClicked = {
                actionClicked?.invoke()
            })
    }

    private fun requestBannerAdsPrepare(
        idBanner: String,
        parent: ViewGroup?,
        onAdLoader: ((AdManagerAdView?) -> Unit)? = null,
        onAdLoadFail: (() -> Unit)? = null,
        actionClicked: (() -> Unit)? = null
    ) {
        adView = AdManagerAdView(context)
        adView?.adUnitId = idBanner
        adView?.setAdSizes(adSize)
        adView?.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                isBannerLoaded = true
                parent?.removeAllViews()
                parent?.addView(adView)
                onAdLoader?.invoke(adView)
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                onAdLoadFail?.invoke()
            }

            override fun onAdClicked() {
                super.onAdClicked()
                actionClicked?.invoke()
            }
        }
        adView?.setOnPaidEventListener {
            Utils.postRevenueAdjust(it, adView?.adUnitId)
        }
        val adRequest = if (isCollapse) {
            AdRequest
                .Builder()
                .addNetworkExtrasBundle(AdMobAdapter::class.java, Bundle().apply {
                    putString("collapsible", "bottom")
                })
                .build()
        } else {
            AdRequest.Builder().build()
        }
        adView?.loadAd(adRequest)
    }

    fun loadAdViewToParent(parent: ViewGroup?) {
        if (adView?.parent != null) {
            (adView?.parent as ViewGroup).removeAllViews()
        }
        parent?.removeAllViews()
        parent?.addView(adView)
        parent?.isVisible = true
    }

    fun stopView() {
        adView?.pause()
    }

    fun resumeView() {
        adView?.resume()
    }
}