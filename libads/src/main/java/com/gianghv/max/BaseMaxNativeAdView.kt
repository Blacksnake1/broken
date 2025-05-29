package com.gianghv.max

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.facebook.shimmer.ShimmerFrameLayout


abstract class BaseMaxNativeAdView(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {

    fun showShimmer(isShow: Boolean) {
        if (isShow) {
            getShimerView().startShimmer()
            getShimerView().isVisible = true
            getRootAds().isVisible = false
        } else {
            getShimerView().stopShimmer()
            getShimerView().isVisible = false
            getRootAds().isVisible = true
        }
    }

    fun hideAdsAndShimmer() {
        getShimerView().stopShimmer()
        getShimerView().isVisible = false
        getRootAds().isVisible = false
    }

    fun setNativeAdInflate(nativeAdView: MaxNativeAdView?) {
        getRootAds().removeAllViews()
        getRootAds().addView(nativeAdView)
    }

    fun errorShimmer() {
        getShimerView().stopShimmer()
    }

    abstract fun getShimerView(): ShimmerFrameLayout
    abstract fun getRootAds(): FrameLayout

}