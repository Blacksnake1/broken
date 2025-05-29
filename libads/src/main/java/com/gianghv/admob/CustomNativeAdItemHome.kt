package com.gianghv.admob

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.gianghv.libads.databinding.CustomNativeAdsIntro2Binding
import com.gianghv.libads.databinding.CustomNativeAdsItemHomeBinding
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAdView

class CustomNativeAdItemHome(context: Context, attrs: AttributeSet?) :
    BaseNativeAdView(context, attrs) {

    private var mViewBinding: CustomNativeAdsItemHomeBinding =
        CustomNativeAdsItemHomeBinding.inflate(LayoutInflater.from(context), this, true)

    override fun getTitleView(): AppCompatTextView = mViewBinding.adHeadline
    override fun getSubTitleView(): AppCompatTextView = mViewBinding.adBody
    override fun getRatingView(): AppCompatRatingBar? = null

    override fun getIconView(): ImageView? = null

    override fun getPriceView(): AppCompatTextView? = null
    override fun getCallActionButtonView(): AppCompatTextView = mViewBinding.adCallToAction
    override fun getMediaView(): MediaView? = null

    override fun getAdView(): NativeAdView = mViewBinding.adView
    override fun getViewContainerRate_Price(): View? = null

    override fun getShimerView(): ShimmerFrameLayout = mViewBinding.shimmer.shimmerViewGift
    override fun getRootAds(): ConstraintLayout = mViewBinding.rootNativeAd
    override fun getLabelAds(): TextView? = mViewBinding.labelAds
    override fun getTextCountDown(): TextView? = null
    override fun getStore(): TextView? = null
}