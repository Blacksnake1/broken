package com.nekosoft.brokenglass.extention

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.brokenscreen.prankapp.crack.screen.BuildConfig
import com.gianghv.admob.BaseNativeAdView
import com.gianghv.admob.NativeAdsManager
import com.gianghv.max.BaseMaxNativeAdView
import com.gianghv.max.MaxNativeAdsManager
import com.gianghv.utils.AdsConfigUtils
import com.google.android.gms.ads.nativead.NativeAd
import com.nekosoft.brokenglass.App
import com.nekosoft.brokenglass.extention.ContextExt.getAdsNetworking
import com.nekosoft.brokenglass.extention.ContextExt.isOnline

class LoadNativeAds(
    val activity: Activity,
    var statusConfigName: String?,
    var idAds: String?,
    var nativeAdsView: BaseNativeAdView?,
    var nativeAdsViewMax: BaseMaxNativeAdView?,
    val onLoadSuccess: (() -> Unit)? = null,
    val onLoadFail: (() -> Unit)? = null,
) {

    private var isAdsCalling = false
    private var isStopLoad = false

    var nativeAdData: NativeAd? = null

    private var handler: Handler? = null
    private var runable: Runnable? = null


    fun showAds(key: String?, reload: Boolean = false) {
        Log.e("TagAds", "funshowAds $key")
        if (activity.isOnline()) {
            when (statusConfigName?.let { activity.getAdsNetworking(it) }) {
                AdsConfigUtils.ADMOB -> {
                    nativeAdsView?.isVisible = true
                    Log.e("TagAds", "showAds_43:$key $statusConfigName ${nativeAdsView} ")
                    idAds?.let {
                        loadNativeAdmob(
                            nativeAdsView,
                            it,
                            reload,
                            key,
                            onLoadSuccess = { onLoadSuccess?.invoke() },
                            onLoadFail = { onLoadFail?.invoke() })
                    }
                }

                AdsConfigUtils.MAX -> {
                    Log.e("TagAds", "showAds_48: ")
                    nativeAdsViewMax?.isVisible = true
                    loadNativeMax(nativeAdsViewMax,
                        onLoadSuccess = { onLoadSuccess?.invoke() },
                        onLoadFail = { onLoadFail?.invoke() }
                    )
                }

                else -> {
                    Log.e("TagAds", "showAds_54: $key")
                    nativeAdsView?.goneExt()
                    nativeAdsViewMax?.goneExt()
                    onLoadSuccess?.invoke()
                }
            }
        } else {
            Log.e("TagAds", "showAds_61: $key")
            onLoadFail?.invoke()
        }
    }


    private fun crateHandler(key: String?) {
//        if (isStopLoad) return
        Log.e("TagAds", "crateHandler: $key")
        removeHandler(key)
        handler = Handler(Looper.getMainLooper())
        runable = Runnable {
            if (handler != null) {
                Log.d("TagAds", "crateHandler_93: " + App.getInstance().currentActivity)
                Log.e("TagAds", "crateHandler_handle $key")
                showAds(key, true)
            }
        }
        handler?.postDelayed(runable!!, 5000)
    }


    private fun loadNativeAdmob(
        nativeAdsView: BaseNativeAdView?,
        idAds: String,
        reload: Boolean,
        key: String?,
        onLoadSuccess: (() -> Unit)?,
        onLoadFail: (() -> Unit)?
    ) {

        isAdsCalling = true

        val nativeAdsManager = NativeAdsManager(activity, idAds)
        if (!reload) {
            nativeAdsView?.showShimmer(true)
        }
        Log.d("TagAds", "loadNativeAdmob_115: $nativeAdsView $idAds $key")
        nativeAdsManager.loadAds(onLoadSuccess = {
            nativeAdData = it
            Log.e("TagAds", "loadNativeAdmob_117: $key " + activity.isFinishing)
            Log.d("TagAds", "loadNativeAdmob_118: $nativeAdsView $idAds $key")
            if (!activity.isFinishing) {
                nativeAdsView?.showShimmer(false)
                nativeAdsView?.setNativeAd(it)
                nativeAdData = null
                Log.e("TagAds", "loadNativeAdmob_132: $key" + reload)
                if (!reload) {
                    Log.e("TagAds", "loadNativeAdmob_call_onLoadSuccess")
                    onLoadSuccess?.invoke()
                }
                Log.e("TagAds", "loadNativeAdmob_onLoadSuccess_call_crateHandler")

                isAdsCalling = false
                crateHandler(key)
            }
        }, onLoadFail = {
            if (!activity.isFinishing) {
                //nativeAdsView?.hideAdsAndShimmer()
                if (!reload) {
                    onLoadFail?.invoke()
                }
                nativeAdsView?.isGone = true
                Log.e("TagAds", "loadNativeAdmob_onLoadFail_call_crateHandler")
                isAdsCalling = false
                crateHandler(key)
            }
        })
    }

    private fun removeHandler(key: String?) {
        try {
            Log.d("TagAds", "removeHandler_76: " + key)
            runable?.let { handler?.removeCallbacks(it) }
            handler = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun resumeLoadAds(key: String?) {
        isStopLoad = false
        if (handler == null && !isAdsCalling) {
            Log.e("TagAds", "resumeLoadAds_call_crateHandler" + key)
            crateHandler(key)
        }
    }

    fun stopLoadAds(key: String?) {
        isStopLoad = true
        Log.e("TagAds", "stopLoadAds $key")
        statusConfigName = null
        idAds = null
        nativeAdsView = null
        nativeAdsViewMax = null
        removeHandler(key)
    }

    private fun loadNativeMax(
        nativeAdsView: BaseMaxNativeAdView?,
        onLoadSuccess: (() -> Unit)?,
        onLoadFail: (() -> Unit)?,
    ) {

        isAdsCalling = true
        val nativeAdsManager = MaxNativeAdsManager(activity, true, BuildConfig.max_native)
        nativeAdsView?.showShimmer(true)
        nativeAdsManager.loadAds(onLoadSuccess = {
            if (!activity.isFinishing) {
                nativeAdsView?.showShimmer(false)
                nativeAdsView?.setNativeAdInflate(it)
                onLoadSuccess?.invoke()
                isAdsCalling = false
                crateHandler(null)
            }
        }, onLoadFail = {
            if (!activity.isFinishing) {
                nativeAdsView?.hideAdsAndShimmer()
                onLoadFail?.invoke()
                nativeAdsView?.isGone = true
                isAdsCalling = false
                crateHandler(null)
            }
        })
    }


}