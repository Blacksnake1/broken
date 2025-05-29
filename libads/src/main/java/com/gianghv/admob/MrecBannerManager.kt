package com.gianghv.admob

import android.app.Activity
import android.os.Build
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.gianghv.utils.Utils
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdView

class MrecBannerManager(
    private val context: Activity,
    private val mIdBanner01: String,
) {
    var adView: AdManagerAdView? = null
    var isBannerLoaded = false

    val adSize: AdSize = AdSize.MEDIUM_RECTANGLE


    @RequiresApi(Build.VERSION_CODES.M)
    fun loadBanner(
        parent: ViewGroup?,
        onAdLoader: ((AdManagerAdView?) -> Unit)? = null,
        onAdLoadFail: (() -> Unit)? = null
    ) {
        if (!Utils.isNetworkConnected(context)) {
            onAdLoadFail?.invoke()
            return
        }


        requestBannerAdsPrepare(
            mIdBanner01,
            parent,
            onAdLoader = { onAdLoader?.invoke(adView) },
            onAdLoadFail = {

                onAdLoadFail?.invoke()

            })

    }


    private fun requestBannerAdsPrepare(
        idBanner: String,
        parent: ViewGroup?,
        onAdLoader: ((AdManagerAdView?) -> Unit)? = null,
        onAdLoadFail: (() -> Unit)? = null
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
        }
        adView?.setOnPaidEventListener {
        }
        val adRequest = AdRequest.Builder().build()

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