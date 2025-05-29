package com.gianghv.max

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.gianghv.libads.databinding.MaxNativeLargeViewBinding

class MaxNativeAdLargeView(context: Context, attrs: AttributeSet?) :
    BaseMaxNativeAdView(context, attrs) {

    var mViewBinding: MaxNativeLargeViewBinding =
        MaxNativeLargeViewBinding.inflate(LayoutInflater.from(context), this, true)

    override fun getShimerView(): ShimmerFrameLayout = mViewBinding.shimmer.shimmerView
    override fun getRootAds(): FrameLayout = mViewBinding.frameAdInflate
}
