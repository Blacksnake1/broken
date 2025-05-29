package com.gianghv.admob

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import com.facebook.shimmer.ShimmerFrameLayout
import com.gianghv.libads.databinding.NativeFullScreenBinding
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAdView

class NativeFullScreenAdmobNonCount(context: Context, attrs: AttributeSet?) :
    BaseNativeAdView(context, attrs) {

    var mViewBinding: NativeFullScreenBinding =
        NativeFullScreenBinding.inflate(LayoutInflater.from(context), this, true)

//    fun hideCountDown() {
//        mViewBinding.tvCountDown.isInvisible = true
//        mViewBinding.shimmer.tvCountDown.isInvisible = true
//    }

    override fun getTitleView(): AppCompatTextView = mViewBinding.adHeadline
    override fun getSubTitleView(): AppCompatTextView = mViewBinding.adBody
    override fun getRatingView(): AppCompatRatingBar? = mViewBinding.adRatingBar
    override fun getIconView(): AppCompatImageView = mViewBinding.adAppIcon
    override fun getPriceView(): AppCompatTextView? = null
    override fun getCallActionButtonView(): AppCompatTextView = mViewBinding.adCallToAction
    override fun getMediaView(): MediaView = mViewBinding.adMedia
    override fun getAdView(): NativeAdView = mViewBinding.adView
    override fun getViewContainerRate_Price(): View? = null
    override fun getShimerView(): ShimmerFrameLayout = mViewBinding.shimmer.shimmerView
    override fun getRootAds(): ConstraintLayout = mViewBinding.rootNativeAd
    override fun getLabelAds(): TextView = mViewBinding.label
    override fun getTextCountDown(): TextView? = null
    override fun getStore(): TextView? = mViewBinding.adStore

}