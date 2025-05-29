package com.gianghv.admob

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.gianghv.libads.databinding.CustomNativeAdsCrankEffectBinding
import com.gianghv.libads.databinding.CustomNativeAdsItemCrankEffectBinding
import com.gianghv.libads.databinding.NativeFullScreenBinding
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAdView

class CustomNativeAdsCrankEffect(context: Context, attrs: AttributeSet?) :
    BaseNativeAdView(context, attrs) {

    var mViewBinding: CustomNativeAdsCrankEffectBinding =
        CustomNativeAdsCrankEffectBinding.inflate(LayoutInflater.from(context), this, true)


    override fun getTitleView(): AppCompatTextView = mViewBinding.adHeadline
    override fun getSubTitleView(): AppCompatTextView = mViewBinding.adBody
    override fun getRatingView(): AppCompatRatingBar? = null
    override fun getIconView(): AppCompatImageView = mViewBinding.imgAvt
    override fun getPriceView(): AppCompatTextView? = null
    override fun getCallActionButtonView(): AppCompatTextView = mViewBinding.adCallToAction
    override fun getMediaView(): MediaView? = null
    override fun getAdView(): NativeAdView = mViewBinding.adView
    override fun getViewContainerRate_Price(): View? = null
    override fun getShimerView(): ShimmerFrameLayout = mViewBinding.shimmer.shimmerViewGift
    override fun getRootAds(): ConstraintLayout = mViewBinding.rootNativeAd
    override fun getLabelAds(): TextView?= mViewBinding.labelAds
    override fun getTextCountDown(): TextView?= null
    override fun getStore(): TextView = mViewBinding.adStore

}