package com.gianghv.admob

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import com.gianghv.utils.ConstantsAds
import com.gianghv.utils.ConstantsAds.isLoadingInterAds
import com.gianghv.utils.Utils
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialPreloadAdManager constructor(
    private val context: Context,
    val mIdAdsFull02: String

) {
    var isShowingAds = false
    val FOUR_HOUR = 4 * 60 * 1000 * 60

    private var mInterstitialAd: InterstitialAd? = null
    var handler: Handler? = null
    var runable: Runnable? = null
    var timeLoaded: Long = 0

    @RequiresApi(Build.VERSION_CODES.M)
    fun loadAds(
        onAdLoader: (() -> Unit)? = null,
        onAdLoadFail: (() -> Unit)? = null,
        onAdLoading: (() -> Unit)? = null
    ) {

        if (!Utils.isNetworkConnected(context)) {
            onAdLoadFail?.invoke()
            return
        }
        if (isLoadingInterAds) {
            onAdLoading?.invoke()
            return
        }
        handler = Handler(Looper.getMainLooper())
        runable = Runnable {
            if (handler != null) {
                onAdLoadFail?.invoke()
            }
        }
        handler?.postDelayed(runable!!, ConstantsAds.TIME_OUT)

        requestAdsPrepare(mIdAdsFull02, onAdLoader = onAdLoader, onAdLoadFail = {
            runable?.let { handler?.removeCallbacks(it) }
            handler = null
            onAdLoadFail?.invoke()
        })

    }

    private fun requestAdsPrepare(
        idAds: String, onAdLoader: (() -> Unit)? = null, onAdLoadFail: (() -> Unit)? = null
    ) {
        if (handler == null && isLoadingInterAds) {
            return
        }
        isLoadingInterAds = true
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, idAds, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
                isLoadingInterAds = false
                onAdLoadFail?.invoke()
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                isLoadingInterAds = false
                timeLoaded = System.currentTimeMillis()
                if (handler == null) {
                    return
                }
                runable?.let { handler?.removeCallbacks(it) }
                handler = null
                mInterstitialAd = interstitialAd
                onAdLoader?.invoke()
            }
        })
    }

    fun showAds(
        activity: Activity,
        onAdClose: (() -> Unit)? = null,
        onAdError: ((p0: AdError?) -> Unit)? = null,
        onAdShowing: (() -> Unit)? = null,
        onAdClicked: (() -> Unit)? = null,

        ) {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    onAdClose?.invoke()
                    isShowingAds = false
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    isShowingAds = false
                    mInterstitialAd = null
                    onAdError?.invoke(p0)
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    onAdClicked?.invoke()
                }


                override fun onAdShowedFullScreenContent() {
                    onAdShowing?.invoke()
                    handler = null
                    mInterstitialAd = null
                    isShowingAds = true
                }
            }
            mInterstitialAd?.setOnPaidEventListener {
                Utils.postRevenueAdjust(it, mInterstitialAd?.adUnitId)
            }
            mInterstitialAd?.show(activity) ?: onAdError?.invoke(null)
        } else {
            isShowingAds = false
            onAdError?.invoke(null)
        }
    }

    fun isAdAvailable(): Boolean {
        return mInterstitialAd != null && (System.currentTimeMillis() - timeLoaded) <= FOUR_HOUR && !isShowingAds
    }

}