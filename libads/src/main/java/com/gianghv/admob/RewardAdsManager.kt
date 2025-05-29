package com.gianghv.admob

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import com.gianghv.utils.ConstantsAds
import com.gianghv.utils.Utils
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class RewardAdsManager(
    private val context: Context,
    private val mIDReward: String,
) {
    private var rewardedAd: RewardedAd? = null
    var handler: Handler? = null
    var runable: Runnable? = null
    var isShowReward = false
    val FOUR_HOUR = 4 * 60 * 1000 * 60
    var timeLoaded: Long = 0
    var isRewardLoading = false

    @RequiresApi(Build.VERSION_CODES.M)
    fun requestAds(
        onLoadAdSuccess: (() -> Unit),
        onAdLoadFail: (() -> Unit),
        onAdLoading: (() -> Unit)
    ) {
        val requestConfiguration = RequestConfiguration.Builder().build()
        MobileAds.setRequestConfiguration(requestConfiguration)
        if (!Utils.isNetworkConnected(context)) {
            onAdLoadFail.invoke()
            return
        }
        if (isRewardLoading) {
            onAdLoading.invoke()
            return
        }
        handler = Handler(Looper.getMainLooper())
        runable = Runnable {
            if (handler != null) {
                onAdLoadFail.invoke()
                handler = null
            }
        }
        handler?.postDelayed(runable!!, ConstantsAds.TIME_OUT)
        requestAdsPrepare(mIDReward,
            onLoadAdSuccess = {
                onLoadAdSuccess.invoke()
            }, onAdLoadFail = {
                onAdLoadFail.invoke()
            })
    }

//    fun loadAds(
//        onAdLoader: (() -> Unit)? = null, onAdLoadFail: (() -> Unit)? = null
//    ) {
//        requestAdsPrepare(mIDReward, onAdLoader, onAdLoadFail = {
//            runable?.let { handler?.removeCallbacks(it) }
//            handler = null
//            onAdLoadFail?.invoke()
//        })
//    }

    private fun requestAdsPrepare(
        idAds: String,
        onLoadAdSuccess: (() -> Unit)? = null,
        onAdLoadFail: (() -> Unit)? = null
    ) {
        if (handler == null && isRewardLoading) {
            return
        }
        isRewardLoading = true
        val configuration =
            RequestConfiguration.Builder().setTestDeviceIds(ConstantsAds.testDevices()).build()
        MobileAds.setRequestConfiguration(configuration)
        RewardedAd.load(context,
            idAds,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(p0: RewardedAd) {
                    isRewardLoading = false
                    runable?.let { handler?.removeCallbacks(it) }
                    if (handler == null) {
                        return
                    }
                    rewardedAd = p0
                    onLoadAdSuccess?.invoke()
                    handler = null
                    timeLoaded = System.currentTimeMillis()
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    isRewardLoading = false
                    runable?.let { handler?.removeCallbacks(it) }
                    handler = null
                    onAdLoadFail?.invoke()
                }
            })
    }

    fun showRewardAds(activity: Activity, onShowRewardAdListener: OnShowRewardAdListener) {
        if (rewardedAd != null) {
            val fullScreenContentCallback: FullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdShowedFullScreenContent() {
                        onShowRewardAdListener.onShow()
                        runable?.let { handler?.removeCallbacks(it) }
                        handler = null
                    }

                    override fun onAdDismissedFullScreenContent() {
                        rewardedAd = null
                        onShowRewardAdListener.onShowRewardSuccess()
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        super.onAdFailedToShowFullScreenContent(p0)
                        onShowRewardAdListener.onShowRewardFailed(p0)
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        onShowRewardAdListener.onClicked()
                    }
                }
            rewardedAd?.fullScreenContentCallback = fullScreenContentCallback
            rewardedAd?.setOnPaidEventListener {
                Utils.postRevenueAdjust(it, rewardedAd?.adUnitId)
            }
            rewardedAd?.show(
                activity
            ) { onShowRewardAdListener.onUserEarnReward() }
        } else {
            onShowRewardAdListener.onShowRewardFailed(null)
        }
    }

    fun isRewardAdAvailable(): Boolean {
        return rewardedAd != null && (System.currentTimeMillis() - timeLoaded) <= FOUR_HOUR
    }

    interface OnShowRewardAdListener {
        fun onShow()
        fun onShowRewardSuccess()
        fun onUserEarnReward()
        fun onShowRewardFailed(string: AdError?)
        fun onClicked()
    }
}