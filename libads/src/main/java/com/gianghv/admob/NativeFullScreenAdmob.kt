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
import com.gianghv.libads.databinding.NativeFullScreenBinding
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAdView

class NativeFullScreenAdmob(context: Context, attrs: AttributeSet?) :
    BaseNativeAdView(context, attrs) {

    var mViewBinding: NativeFullScreenBinding =
        NativeFullScreenBinding.inflate(LayoutInflater.from(context), this, true)

    fun initCollapseEvent(onCloseAdsAction: (() -> Unit)) {
//        mViewBinding.tvCountDown.visibility = View.VISIBLE
        mViewBinding.btnHideNativeFullScreen.visibility = View.VISIBLE
//
//        val countDownTimer = object : CountDownTimer(3000, 1000) {
//            @SuppressLint("SetTextI18n")
//            override fun onTick(lastTime: Long) {
//                if (((lastTime / 1000) + 1).toInt() == 4) {
//                    mViewBinding.tvCountDown.text = 3.toString()
//                } else {
//                    mViewBinding.tvCountDown.text = ((lastTime / 1000) + 1).toString()
//                }
//            }
//
//            override fun onFinish() {
//                mViewBinding.tvCountDown.text = "0"
//                Handler(Looper.getMainLooper()).postDelayed({
//                    mViewBinding.tvCountDown.visibility = View.GONE
//                    mViewBinding.btnHideNativeFullScreen.visibility = View.VISIBLE
//                }, 500)
//            }
//        }
//        countDownTimer.start()

        mViewBinding.btnHideNativeFullScreen.setOnClickListener {
            onCloseAdsAction.invoke()
        }

    }


    override fun getTitleView(): AppCompatTextView = mViewBinding.adHeadline
    override fun getSubTitleView(): AppCompatTextView = mViewBinding.adBody
    override fun getRatingView(): AppCompatRatingBar = mViewBinding.adRatingBar
    override fun getIconView(): AppCompatImageView = mViewBinding.adAppIcon
    override fun getPriceView(): AppCompatTextView? = null
    override fun getCallActionButtonView(): AppCompatTextView = mViewBinding.adCallToAction
    override fun getMediaView(): MediaView = mViewBinding.adMedia
    override fun getAdView(): NativeAdView = mViewBinding.adView
    override fun getViewContainerRate_Price(): View? = null
    override fun getShimerView(): ShimmerFrameLayout = mViewBinding.shimmer.shimmerView
    override fun getRootAds(): ConstraintLayout = mViewBinding.rootNativeAd
    override fun getLabelAds(): TextView = mViewBinding.label
    override fun getTextCountDown(): TextView ? = null
    override fun getStore(): TextView? = mViewBinding.adStore

}