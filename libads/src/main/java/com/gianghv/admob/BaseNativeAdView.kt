package com.gianghv.admob

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView


abstract class BaseNativeAdView(context: Context, attrs: AttributeSet?) :
    FrameLayout(context, attrs) {

    open fun setNativeAd(nativeAd: NativeAd) {
        val icon = nativeAd.icon
        val starRating = nativeAd.starRating
        val title = nativeAd.headline
        val callAction = nativeAd.callToAction
        val price = nativeAd.price
        val subTitle = nativeAd.body
        val store = nativeAd.store


        if (icon != null) {
            getIconView()?.setImageDrawable(icon.drawable)
            getIconView()?.visibility = View.VISIBLE
        } else {
            getIconView()?.visibility = View.INVISIBLE
        }

        if (starRating != null && starRating > 0f) {
            getRatingView()?.rating = starRating.toFloat()
            getRatingView()?.isVisible = true
        } else {
            getRatingView()?.isInvisible = true
        }

        if (store != null) {
            getStore()?.text = store
            getStore()?.visibility = View.VISIBLE
        } else {
            getStore()?.visibility = View.INVISIBLE
        }

        getViewContainerRate_Price()?.isVisible =
            !((starRating == null || starRating <= 0f) && price == null)

        if (subTitle != null) {
            getSubTitleView()?.text = subTitle
            getSubTitleView()?.visibility = View.VISIBLE
        } else {
            getSubTitleView()?.visibility = View.INVISIBLE
        }

        if (title != null) {
            getTitleView().text = title
            getTitleView().isSelected = true
            getTitleView().visibility = View.VISIBLE
        } else {
            getTitleView().visibility = View.INVISIBLE
        }

        if (callAction != null) {
            getCallActionButtonView().text = callAction
            getCallActionButtonView().visibility = View.VISIBLE
        } else {
            getCallActionButtonView().visibility = View.INVISIBLE
        }


        if (price != null) {
            getPriceView()?.text = price
            getPriceView()?.visibility = View.VISIBLE
        } else {
            getPriceView()?.visibility = View.INVISIBLE
        }

        if (getMediaView() != null) {
            getAdView().mediaView = getMediaView()
        }

        getLabelAds()?.setOnClickListener {
            getCallActionButtonView().performClick()
        }
        getTextCountDown()?.setOnClickListener {
            getCallActionButtonView().performClick()
        }
        getRootAds().setOnClickListener {
            getCallActionButtonView().performClick()
        }

        getAdView().callToActionView = getCallActionButtonView()
        getAdView().headlineView = getTitleView()
        getAdView().bodyView = getSubTitleView()
        getAdView().storeView = getStore()
        getAdView().setNativeAd(nativeAd)
    }

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
        getShimerView().isVisible = true
        getRootAds().isVisible = false
    }

    fun errorShimmer() {
        getShimerView().stopShimmer()
    }


    abstract fun getTitleView(): AppCompatTextView
    abstract fun getSubTitleView(): AppCompatTextView?
    abstract fun getRatingView(): AppCompatRatingBar?
    abstract fun getIconView(): ImageView?
    abstract fun getPriceView(): AppCompatTextView?
    abstract fun getCallActionButtonView(): AppCompatTextView
    abstract fun getAdView(): NativeAdView
    abstract fun getShimerView(): ShimmerFrameLayout
    abstract fun getRootAds(): ConstraintLayout
    abstract fun getLabelAds(): TextView?
    abstract fun getTextCountDown(): TextView?
    abstract fun getStore(): TextView?

    open fun getMediaView(): MediaView? = null
    open fun getViewContainerRate_Price(): View? = null

}