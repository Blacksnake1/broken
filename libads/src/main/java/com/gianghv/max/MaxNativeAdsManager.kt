package com.gianghv.max

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder
import com.gianghv.libads.R
import com.gianghv.utils.ConstantsAds
import com.gianghv.utils.Utils

class MaxNativeAdsManager constructor(
    private val context: Context,
    private val isBigView: Boolean,
    private val idNativeAdsMax: String
) {
    private var nativeAdLoader: MaxNativeAdLoader? = null
    private var mNativeAdView: MaxNativeAdView? = null
    private var nativeAdMax: MaxAd? = null
    var handler: Handler? = null
    var runable: Runnable? = null
    var isLoadingAdsFailed = false

    fun loadAds(
        onLoadSuccess: ((MaxNativeAdView?) -> Unit)? = null,
        onLoadFail: ((failed: Boolean) -> Unit)? = null
    ) {
        if (!Utils.isNetworkConnected(context)) {
            onLoadFail?.invoke(true)
            isLoadingAdsFailed = true
            return
        }

        handler = Handler(Looper.getMainLooper())
        runable = Runnable {
            if (handler != null) {
                onLoadFail?.invoke(true)
                isLoadingAdsFailed = true
            }
        }
        handler?.postDelayed(runable!!, ConstantsAds.TIME_OUT)

        requestAds(idNativeAdsMax, onLoadSuccess, onLoadFail = {
            runable?.let { handler?.removeCallbacks(it) }
            onLoadFail?.invoke(true)
            isLoadingAdsFailed = true
        })
    }

    fun requestAds(
        idNativeAds: String,
        onLoadSuccess: ((MaxNativeAdView?) -> Unit)? = null,
        onLoadFail: (() -> Unit)? = null
    ) {

        val binder =
            if (isBigView) {
                MaxNativeAdViewBinder.Builder(R.layout.max_big_native_layout_inflate)
                    .setTitleTextViewId(R.id.ad_headline)
                    .setBodyTextViewId(R.id.ad_body)
                    .setAdvertiserTextViewId(R.id.ad_headline)
                    .setStarRatingContentViewGroupId(R.id.ad_stars)
                    .setIconImageViewId(R.id.ad_app_icon)
                    .setMediaContentViewGroupId(R.id.ad_media)
                    .setOptionsContentViewGroupId(R.id.ad_options_view)
                    .setCallToActionButtonId(R.id.ad_call_to_action_max)
                    .build()
            } else {
                MaxNativeAdViewBinder.Builder(R.layout.max_native_banner_view_inflate)
                    .setTitleTextViewId(R.id.ad_headline)
                    .setBodyTextViewId(R.id.ad_body)
                    .setAdvertiserTextViewId(R.id.ad_headline)
                    .setStarRatingContentViewGroupId(R.id.ad_stars)
                    .setIconImageViewId(R.id.ad_app_icon)
                    .setOptionsContentViewGroupId(R.id.ad_options_view)
                    .setCallToActionButtonId(R.id.ad_call_to_action_max)
                    .build()
            }
        mNativeAdView = MaxNativeAdView(binder, context)
        nativeAdLoader = MaxNativeAdLoader(idNativeAds, context)
        nativeAdLoader?.setRevenueListener {
            Utils.postRevenueMaxToAdjust(it, it.adUnitId)
        }

        nativeAdLoader?.setNativeAdListener(object : MaxNativeAdListener() {
            override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView?, ad: MaxAd) {
                // Cleanup any pre-existing native ad to prevent memory leaks.
                runable?.let { handler?.removeCallbacks(it) }
                handler = null
                if (nativeAdMax != null) {
                    nativeAdLoader?.destroy(nativeAdMax)
                }
                nativeAdMax = ad
                mNativeAdView = nativeAdView
                onLoadSuccess?.invoke(mNativeAdView)
            }

            override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError) {
                onLoadFail?.invoke()
            }

            override fun onNativeAdClicked(ad: MaxAd) {}
        })
        nativeAdLoader?.loadAd(mNativeAdView)
    }
}