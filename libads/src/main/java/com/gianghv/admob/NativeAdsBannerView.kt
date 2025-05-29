package com.gianghv.admob

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.gianghv.libads.databinding.NativeBannerViewBinding
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class NativeAdsBannerView(context: Context, attrs: AttributeSet?) :
    BaseNativeAdView(context, attrs) {

    private val mViewBinding =
        NativeBannerViewBinding.inflate(LayoutInflater.from(context), this, true)

    override fun setNativeAd(nativeAd: NativeAd) {
        super.setNativeAd(nativeAd)
        val price: String? = nativeAd.price
        val starRating: Double? = nativeAd.starRating
//        getSubTitleView().setVisible(!((starRating != null && starRating > 0f) || price != null))
    }

    override fun getTitleView(): AppCompatTextView = mViewBinding.adHeadline
    override fun getSubTitleView(): AppCompatTextView = mViewBinding.adBody
    override fun getRatingView(): AppCompatRatingBar = mViewBinding.adStars

    override fun getIconView(): AppCompatImageView = mViewBinding.adAppIcon

    override fun getPriceView(): AppCompatTextView? = null
    override fun getCallActionButtonView(): AppCompatTextView = mViewBinding.adCallToActionBanner

    override fun getAdView(): NativeAdView = mViewBinding.adView
    override fun getViewContainerRate_Price(): View? = null
    override fun getShimerView(): ShimmerFrameLayout = mViewBinding.shimmer.shimmerView
    override fun getRootAds(): ConstraintLayout = mViewBinding.rootNativeAd
    override fun getLabelAds(): TextView? = null
    override fun getTextCountDown(): TextView? = null
    override fun getStore(): TextView?= null


}