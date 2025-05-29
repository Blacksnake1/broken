package com.gianghv.admob

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import com.gianghv.utils.ConstantsAds
import com.gianghv.utils.Utils
import com.google.ads.mediation.facebook.FacebookMediationAdapter
import com.google.ads.mediation.mintegral.MintegralMediationAdapter
import com.google.ads.mediation.pangle.PangleMediationAdapter
import com.google.ads.mediation.vungle.VungleMediationAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.nativead.NativeAd

class NativeAdsManager constructor(
    private val context: Context,
    private val idNativeAds: String,
    private var timeOut: Long = ConstantsAds.TIME_OUT
) {
    private var nativeAd: NativeAd? = null
    private var mAdLoader: AdLoader? = null
    val ONE_HOUR = 1 * 60 * 1000 * 60
    var timeLoaded: Long = 0

    var isLoadingNativeAds = false

    var handler: Handler? = null
    var runable: Runnable? = null

    @RequiresApi(Build.VERSION_CODES.M)
    fun loadAds(
        onLoadSuccess: ((NativeAd) -> Unit)? = null,
        onLoadFail: ((failed: Boolean) -> Unit)? = null,
        onAdsClicked: (() -> Unit)? = null,
    ) {
        Log.d("nativeAds", "loadAds_45: ")
        val requestConfiguration = RequestConfiguration.Builder().build()
        MobileAds.setRequestConfiguration(requestConfiguration)
        Log.d("nativeAds", "loadAds_48: ")
        if (!Utils.isNetworkConnected(context)) {
            onLoadFail?.invoke(true)
            return
        }
        Log.d("nativeAds", "loadAds_53: ")
        if (isLoadingNativeAds) return
        isLoadingNativeAds = true

        Log.d("nativeAds", "loadAds_55: ")
        handler = Handler(Looper.getMainLooper())
        runable = Runnable {
            if (handler != null) {
                isLoadingNativeAds= false
                onLoadFail?.invoke(true)
            }
        }

        handler?.postDelayed(runable!!, timeOut)
        requestAds(idNativeAds,
            onLoadSuccess = {
                Log.d("nativeAds", "loadAds_70:")
                onLoadSuccess?.invoke(it)
                runable?.let { handler?.removeCallbacks(it) }},
            onLoadFail = {
                Log.d("nativeAds", "loadAds_72: ")
                runable?.let { handler?.removeCallbacks(it) }
                onLoadFail?.invoke(true)
            }, onAdsClicked = {
                onAdsClicked?.invoke()
            })
    }

    fun requestAds(
        idNativeAds: String,
        onLoadSuccess: ((NativeAd) -> Unit)? = null,
        onLoadFail: (() -> Unit)? = null,
        onAdsClicked: (() -> Unit)? = null,
    ) {
        mAdLoader = AdLoader.Builder(context, idNativeAds).forNativeAd {
            nativeAd?.destroy()
            nativeAd = it
            nativeAd?.setOnPaidEventListener {
                Utils.postRevenueAdjust(it, idNativeAds)
            }
            Log.d("nativeAds", "requestAds_92: ")
            isLoadingNativeAds= false
            onLoadSuccess?.invoke(it)
        }.withAdListener(object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d("nativeAds", "onAdLoaded_93: ")
                timeLoaded = System.currentTimeMillis()
                runable?.let { handler?.removeCallbacks(it) }
                handler = null
                isLoadingNativeAds= false
            }

            override fun onAdClosed() {
                super.onAdClosed()
                Log.d("nativeAds", "onAdClosed_101: ")
                val request = AdRequest.Builder().build()
                mAdLoader?.loadAd(request)
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                Log.d("nativeAds", "onAdFailedToLoad_108: ")
                isLoadingNativeAds = false
                onLoadFail?.invoke()
            }

            override fun onAdClicked() {
                super.onAdClicked()
                onAdsClicked?.invoke()

            }
        }).build()
        val extras = Bundle()
        val request =
            AdRequest.Builder().addNetworkExtrasBundle(FacebookMediationAdapter::class.java, extras)
                .addNetworkExtrasBundle(PangleMediationAdapter::class.java, extras)
                .addNetworkExtrasBundle(
                    com.google.ads.mediation.applovin.AppLovinMediationAdapter::class.java, extras
                ).addNetworkExtrasBundle(MintegralMediationAdapter::class.java, extras)
                .addNetworkExtrasBundle(VungleMediationAdapter::class.java, extras).build()
        mAdLoader?.loadAd(request)
    }

    fun isAdAvailable(): Boolean {
        return nativeAd != null && (System.currentTimeMillis() - timeLoaded) <= ONE_HOUR
    }

}