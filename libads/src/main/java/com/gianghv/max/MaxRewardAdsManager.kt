package com.gianghv.max

import android.app.Activity
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.ads.MaxRewardedAd
import com.gianghv.utils.ConstantsAds
import com.gianghv.utils.Utils


class MaxRewardAdsManager constructor(
    private val activity: Activity,
    private val idNativeAdsMax: String
) {
    var rewardedAd: MaxRewardedAd? = null
    var rewardItem = false
    var handler: Handler? = null
    var runable: Runnable? = null
    val FOUR_HOUR = 4 * 60 * 1000 * 60
    var timeLoaded: Long = 0

    companion object {
        var isShowingAd = false

    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun requestAds(
        onAdLoader: (() -> Unit)? = null, onAdLoadFail: (() -> Unit)? = null
    ) {
        if (!Utils.isNetworkConnected(activity)) {
            onAdLoadFail?.invoke()
            return
        }

        handler = Handler(Looper.getMainLooper())
        runable = Runnable {
            if (handler != null) {
                onAdLoadFail?.invoke()
                handler = null
            }
        }
        handler?.postDelayed(runable!!, ConstantsAds.TIME_OUT)
        requestAdsPrepare(idNativeAdsMax, onAdLoader, onAdLoadFail = {
            runable?.let { handler?.removeCallbacks(it) }
            handler = null
            onAdLoadFail?.invoke()
        })
    }

    private fun requestAdsPrepare(
        idAds: String,
        onLoadAdSuccess: (() -> Unit)? = null,
        onAdLoader: (() -> Unit)? = null,
        onAdLoadFail: (() -> Unit)? = null,
        onAdClicked:(() -> Unit)? = null
    ) {
        if (handler == null) {
            return
        }
        rewardedAd = MaxRewardedAd.getInstance(idAds, activity)
        /* rewardedAd?.setListener(object : MaxRewardedAdListener {
             override fun onAdLoaded(ad: MaxAd) {
                 runable?.let { handler?.removeCallbacks(it) }
                 if (handler == null) {
                     return
                 }
                 onLoadAdSuccess?.invoke()
                 onAdLoader?.invoke()
                 handler = null
             }

             override fun onAdDisplayed(ad: MaxAd) {
                 rewardedAd = null
             }

             override fun onAdHidden(ad: MaxAd) {
                 isShowingAd = false
             }

             override fun onAdClicked(ad: MaxAd) {
                 onAdClicked?.invoke()
             }
             override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                 rewardedAd = null
                 onAdLoadFail?.invoke()
             }

             override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
                 onAdLoadFail?.invoke()
             }

             override fun onUserRewarded(p0: MaxAd, p1: MaxReward) {
                 rewardItem = true
             }

             override fun onRewardedVideoStarted(p0: MaxAd) {
             }

             override fun onRewardedVideoCompleted(p0: MaxAd) {
             }
         })*/
        rewardedAd?.loadAd()
    }

    fun show(activity: Activity, listener: OnShowRewardAdListener) {
        if (rewardedAd != null) {
            isShowingAd = true
            runable?.let { handler?.removeCallbacks(it) }
            handler = null
            rewardedAd?.setListener(object : MaxRewardedAdListener {
                override fun onAdLoaded(ad: MaxAd) {
                }

                override fun onAdDisplayed(ad: MaxAd) {
                    isShowingAd = true

                }

                override fun onAdHidden(ad: MaxAd) {
                    isShowingAd = false
                    if (rewardItem) {
                        listener.onCloseRewardCompletedDone()
                    } else {
                        listener.onShowAdsFailed()
                    }
                }

                override fun onAdClicked(ad: MaxAd) {
                    listener.onAdsClicked()
                }

                override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                    isShowingAd = false
                    rewardedAd = null
                    listener.onShowAdsFailed()
                }

                override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
                    isShowingAd = false
                }

                override fun onUserRewarded(p0: MaxAd, p1: MaxReward) {
                    rewardItem = true
                }
            })
            rewardedAd?.setRevenueListener {
                Utils.postRevenueMaxToAdjust(it, it.adUnitId)
            }
            rewardedAd?.showAd()
        } else {
            listener.onShowAdsFailed()
        }
    }

    fun isMaxRewardAdAvailable(): Boolean {
        return rewardedAd != null && (System.currentTimeMillis() - timeLoaded) <= FOUR_HOUR
    }

    interface OnShowRewardAdListener {
        fun onShowRewardSuccess()
        fun onShowAdsFailed()
        fun onShowRewardCompleteDone()
        fun onCloseRewardCompletedDone()
        fun onAdsClicked()
    }
}