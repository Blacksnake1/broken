package com.nekosoft.brokenglass

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.gianghv.admob.InterstitialSingleReqAdManager
import com.gianghv.utils.ConstantsAds.isShowAdsFull
import com.gianghv.utils.NekoAdsLoader
import com.google.android.gms.ads.AdActivity
import com.nekosoft.brokenglass.ui.SplashActivity
import com.nekosoft.brokenglass.utils.Networking
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
open class App : Application(), LifecycleObserver {
    lateinit var nekoAdsLoader: NekoAdsLoader
    var backToFireActivity = 0
    var enableShowOpenAds = false
    var newTurnOpenWallpaper = true
    var newTurnOpenFireScreen = true
    var newTurnOpenHome = true // lần vào màn home đầu tiên của lượt mở mới
    var currentActivity: Activity? = null
    var turnOffOverlayService = false

    var bannerIntro012 = MutableLiveData<Any?>()
    var nativeIntro3 = MutableLiveData<Any?>()
    var nativeIntro4 = MutableLiveData<Any?>()

    var postOpenAds = MutableLiveData<Boolean>(false)
    var appOpenAdManager: InterstitialSingleReqAdManager? = null
    var SET_WALLPAPER_SUCCESSFUL = false


    override fun onCreate() {
        super.onCreate()
        instance = this
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        registerActivityLifecycleCallbacks(AppLifecycleCallbacks())
        nekoAdsLoader = NekoAdsLoader(this)
        nekoAdsLoader.initialize()

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected fun onMoveToForeground() {
        Log.d(
            "extensionAds",
            "onMoveToForeground_59: isShowAdsFull= $isShowAdsFull / allowReturnSplash= $enableShowOpenAds / currentActivity = $currentActivity "
        )
        if (currentActivity !is SplashActivity && !isShowAdsFull && enableShowOpenAds && currentActivity !is AdActivity) {
            if (Networking.isNetworkConnected(applicationContext)) {
                postOpenAds.postValue(true)
            }
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: App? = null

        @JvmStatic
        fun getInstance(): App = instance ?: synchronized(this) {
            instance ?: App().also {
                instance = it
            }
        }
    }

    inner class AppLifecycleCallbacks : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, bundle: Bundle?) {}

        override fun onActivityStarted(activity: Activity) {
            currentActivity = activity
        }

        override fun onActivityResumed(activity: Activity) {}

        override fun onActivityPaused(activity: Activity) {}

        override fun onActivityStopped(activity: Activity) {}

        override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}

        override fun onActivityDestroyed(activity: Activity) {}
    }


}