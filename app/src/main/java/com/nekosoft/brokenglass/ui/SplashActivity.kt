package com.nekosoft.brokenglass.ui


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.brokenscreen.prankapp.crack.screen.BuildConfig
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.ActivitySplashBinding
import com.gianghv.admob.AppOpenAdPlashManager
import com.gianghv.admob.InterstitialPreloadAdManager
import com.gianghv.max.MaxAppOpenAdManager
import com.gianghv.utils.AdsConfigUtils
import com.gianghv.utils.AdsConfigUtils.Companion.DEFAULT_STATUS_ADS
import com.gianghv.utils.AdsConfigUtils.Companion.DEFAULT_TIME_BANNER
import com.gianghv.utils.AdsConfigUtils.Companion.SHOW_INTER_ADS
import com.gianghv.utils.AdsConfigUtils.Companion.banner_home_status
import com.gianghv.utils.AdsConfigUtils.Companion.banner_intro_status
import com.gianghv.utils.AdsConfigUtils.Companion.banner_splash_mrec_status
import com.gianghv.utils.AdsConfigUtils.Companion.banner_splash_status
import com.gianghv.utils.AdsConfigUtils.Companion.inter_crankeffect_back_status
import com.gianghv.utils.AdsConfigUtils.Companion.inter_done_status
import com.gianghv.utils.AdsConfigUtils.Companion.inter_getstart_status
import com.gianghv.utils.AdsConfigUtils.Companion.inter_history_back_status
import com.gianghv.utils.AdsConfigUtils.Companion.inter_resume_status
import com.gianghv.utils.AdsConfigUtils.Companion.inter_splash_status
import com.gianghv.utils.AdsConfigUtils.Companion.inter_unlock_status
import com.gianghv.utils.AdsConfigUtils.Companion.inter_wallpaper_status
import com.gianghv.utils.AdsConfigUtils.Companion.inter_weapon_status
import com.gianghv.utils.AdsConfigUtils.Companion.native_crank_done_status
import com.gianghv.utils.AdsConfigUtils.Companion.native_crank_status
import com.gianghv.utils.AdsConfigUtils.Companion.native_detail_wallpaper_status
import com.gianghv.utils.AdsConfigUtils.Companion.native_downloading_status
import com.gianghv.utils.AdsConfigUtils.Companion.native_firescreen_status
import com.gianghv.utils.AdsConfigUtils.Companion.native_full_status
import com.gianghv.utils.AdsConfigUtils.Companion.native_history_status
import com.gianghv.utils.AdsConfigUtils.Companion.native_intro_status
import com.gianghv.utils.AdsConfigUtils.Companion.native_language_status
import com.gianghv.utils.AdsConfigUtils.Companion.native_select_weapon_status
import com.gianghv.utils.AdsConfigUtils.Companion.native_setting_status
import com.gianghv.utils.AdsConfigUtils.Companion.native_wallpaper_status
import com.gianghv.utils.Utils
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.nekosoft.brokenglass.App
import com.nekosoft.brokenglass.base.BaseActivity
import com.nekosoft.brokenglass.extention.ContextExt.changeStatusBarColor
import com.nekosoft.brokenglass.extention.ContextExt.isOnline
import com.nekosoft.brokenglass.extention.invisibleExt
import com.nekosoft.brokenglass.extention.launchWhenResumed
import com.nekosoft.brokenglass.extention.requestAndShowBanner
import com.nekosoft.brokenglass.extention.requestAndShowBannerMrect
import com.nekosoft.brokenglass.extention.requestInterFunction
import com.nekosoft.brokenglass.extention.visibleExt
import com.nekosoft.brokenglass.service.OverlayService
import com.nekosoft.brokenglass.ui.intro.IntroActivity
import com.nekosoft.brokenglass.ui.language.LanguageActivity
import com.nekosoft.brokenglass.utils.ConstantsApp
import com.nekosoft.brokenglass.utils.isServiceRunning
import com.nekosoft.brokenglass.utils.logEventFirebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private var handlerInter: Handler? = null
    private var runableInter: Runnable? = null
    private var splashOpenAmob: AppOpenAdPlashManager? = null
    private var splashOpenMax: MaxAppOpenAdManager? = null
    private var isResume = false
    private var turnOffOverlayService = false

    override fun setScreen() {
        fullScreencall()
        changeStatusBarColor(R.color.black)
    }

    override fun setupAds() {

    }

    override fun setupData() {
        Log.d("tagSplash", "setupData_79: ")
        ConstantsApp.IS_NEW_TURN_FOR_SHOW_DIALOG_RATE = true
        turnOffOverlayService = intent.getBooleanExtra(OverlayService.TURN_OFF_BROKEN, false)
        handleNotShowinter()

        if (isServiceRunning(this, OverlayService::class.java)) {
            OverlayService.turnOffService(this)
            turnOffOverlayService = true
        }

        App.getInstance().turnOffOverlayService = turnOffOverlayService
        logEventFirebase(this@SplashActivity, "plash_start")
        if (Utils.isNetworkConnected(this@SplashActivity)) {
            App.getInstance().nekoAdsLoader.showConsentForm(
                this@SplashActivity,
                onInitSuccess = {
                    feetchRemoteConfig()
                }, onDialogCMPShowing = {}
            )
        } else {
            checkFinishPermission()
        }
    }

    /**
     * dùng để xử lý cho việc đã vào màn splash nhưng không chuyển sang màn tiếp theo
     * thì sau 15s sẽ tự động chuyển màn
     */
    private fun handleNotShowinter() {
        handlerInter = Handler(Looper.getMainLooper())
        runableInter = Runnable {
            if (handlerInter != null) {
                runableInter?.let { handlerInter?.removeCallbacks(it) }
                handlerInter = null
                Log.d("tagSplash", "handleNotShowinter: ")
                followOfNewTurn()
            }
        }
        handlerInter?.postDelayed(runableInter!!, 15000)
    }

    override fun setupViews() {

    }

    override fun setupEvent() {

    }

    override fun getViewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    private fun feetchRemoteConfig() {
        val configKeys = arrayOf(
            banner_splash_status,
            banner_splash_mrec_status,
            banner_intro_status,
            banner_home_status,
            native_language_status,
            native_intro_status,
            native_full_status,
            native_crank_status,
            native_downloading_status,
            native_crank_done_status,
            native_history_status,
            native_wallpaper_status,
            native_detail_wallpaper_status,
            native_firescreen_status,
            native_select_weapon_status,
            native_setting_status,
            inter_splash_status,
            inter_resume_status,
            inter_getstart_status,
            inter_crankeffect_back_status,
            inter_done_status,
            inter_history_back_status,
            inter_wallpaper_status,
            inter_unlock_status,
            inter_weapon_status
        )

        val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        mFirebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener(this@SplashActivity) { task ->

                val timeShowInter =
                    FirebaseRemoteConfig.getInstance().getLong(SHOW_INTER_ADS)

                val configValues = Array(configKeys.size) {
                    FirebaseRemoteConfig.getInstance().getLong(configKeys[it])
                }

                AdsConfigUtils(this).apply {
                    for (i in configKeys.indices) {
                        putStatusAdsNetworking(configKeys[i], configValues[i].toInt())
                    }
                    putStatusAdsNetworking(SHOW_INTER_ADS, timeShowInter.toInt())

                }
                checkFinishPermission()
            }.addOnFailureListener {
                AdsConfigUtils(this).apply {
                    for (i in configKeys.indices) {
                        putStatusAdsNetworking(configKeys[i], DEFAULT_STATUS_ADS)
                    }
                    putStatusAdsNetworking(SHOW_INTER_ADS, DEFAULT_TIME_BANNER)
                }
                checkFinishPermission()
            }
    }


    private fun checkFinishPermission() {
        if (isOnline()) {
            whenConnected()
        } else {
            whenDisconnect()
        }
    }

    /** không có mạng thì sẽ chuyển sang màn tiếp theo luôn*/
    private fun whenDisconnect() {
        Handler(Looper.getMainLooper()).postDelayed({
            Log.d("tagSplash", "whenDisconnect_196: ")
            followOfNewTurn()
        }, 2000)
    }

    /** nếu có mạng thì sẽ request ads*/
    private fun whenConnected() {
        Log.d("tagSplash", "whenConnected_194: ")
        requestAds()
    }

    private var bannerIsShowing = false
    private var interIsShowing = false
    val inter = InterstitialPreloadAdManager(this, BuildConfig.inter_splash_id)

    private fun requestAds() {
        requestInter()
        hideAndShowBannerShimmer(true)
        requestAndShowBanner(
            banner_splash_status,
            BuildConfig.banner_splash_id,
            false,
            binding.bannerSplashBottom,
            actionDone = {},
            actionFail = {}
        )

        requestAndShowBannerMrect (
            banner_splash_mrec_status,
            BuildConfig.banner_splash_mrec_id,
            binding.bannerSplashMedium,
            actionDone = {
                hideAndShowBannerShimmer(false)
                Log.d("tagSplash", "requestBanner_246: ")
                lifecycleScope.launch {
                    delay(6000)
                    bannerIsShowing = true
                    if (inter.isAdAvailable()) {
                        Log.d("tagSplash", "SplashActivity_requestAds_256: ")
                        showInter()
                    }
                }
            },
            actionFail = {
                Log.d("tagSplash", "requestBanner_261: ")
                hideAndShowBannerShimmer(null)
                lifecycleScope.launch {
                    delay(6000)
                    bannerIsShowing = true
                    if (inter.isAdAvailable()) {
                        Log.d("tagSplash", "SplashActivity_requestAds_268: ")
                        showInter()
                    }
                }
            }
        )
    }

    private fun requestInter() {
        Log.d("tagSplash", "requestInter_254: ")
        requestInterFunction(
            inter_splash_status,
            inter,
            onAdLoader = {
                Log.d("tagSplash", "requestAds_222: ${inter.isAdAvailable()}/ $bannerIsShowing")
                if (inter.isAdAvailable() && bannerIsShowing) {
                    Log.d("tagSplash", "requestInter_225: ")
                    showInter()
                }
            },
            onAdLoadFail = {
                Log.d("tagSplash", "requestAds_231: $bannerIsShowing")
                if (bannerIsShowing) {
                    Log.d("tagSplash", "requestInter_232 ")
                    followOfNewTurn()
                }
            },
            onAdLoading = {}
        )
    }

    private fun showInter() {
        launchWhenResumed {
            inter.showAds(
                this@SplashActivity,
                onAdClose = {
                    Log.d("tagSplash", "showInter_282: ")
                    interIsShowing = true
                    followOfNewTurn()

                }, onAdError = {
                    Log.d("tagSplash", "showInter_253: ")
                    interIsShowing = true
                    followOfNewTurn()
                },
                onAdShowing = {
                    interIsShowing = true
                    if (handlerInter != null) {
                        runableInter?.let { handlerInter?.removeCallbacks(it) }
                        handlerInter = null
                    }
                    Log.d("tagSplash", "showInter_287: ")
                },
                onAdClicked = {})
        }
    }

    private fun followOfNewTurn() {
        Log.d("tagSplash", "followOfNewTurn_297: " + sharedPreferences)
        var turnOpenApp = sharedPreferences?.getInt(ConstantsApp.TURN_OPEN_APP, 0)
        val openedHome = sharedPreferences?.getBoolean(ConstantsApp.OPENED_HOME, false)
        if (turnOpenApp == 0) {
            turnOpenApp = 1
            sharedPreferences?.setInt(ConstantsApp.TURN_OPEN_APP, turnOpenApp)
        } else if (turnOpenApp == 1 && openedHome == true) {
            turnOpenApp = 2
            sharedPreferences?.setInt(ConstantsApp.TURN_OPEN_APP, turnOpenApp)
        } else if (turnOpenApp == 2 && openedHome == true) {
            turnOpenApp = 3
            sharedPreferences?.setInt(ConstantsApp.TURN_OPEN_APP, turnOpenApp)
        }

        if (handlerInter != null) {
            runableInter?.let { handlerInter?.removeCallbacks(it) }
            handlerInter = null
        }

        if (Utils.isNetworkConnected(this@SplashActivity)) logEventFirebase(
            this@SplashActivity,
            "finish_plash_has_connection"
        )
        else {
            logEventFirebase(this@SplashActivity, "finish_plash_no_connection")
        }
        launchWhenResumed {
            if (turnOpenApp == 1) {
                Log.d("tagSplash", "followOfNewTurn_317: ")
                val intent = Intent(this@SplashActivity, LanguageActivity::class.java)
                intent.putExtra("mobile", "fromSplash")
                startActivity(intent)
                finish()
            } else {
                Log.d("tagSplash", "followOfNewTurn_323: ")
                val intent = Intent(this@SplashActivity, IntroActivity::class.java)
                intent.putExtra("meme", true)
                startActivity(intent)
                finish()
            }
//            val intent = Intent(this@SplashActivity, HomeActivity::class.java)
//            startActivity(intent)
//            finish()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun hideAndShowBannerShimmer(showShimmer: Boolean?) {
        if (showShimmer == true) {
//            binding.shimmer.shimmerViewf.visibleExt()
            binding.bannerSplashMedium.invisibleExt()
            binding.bannerSplashBottom.invisibleExt()
        } else if (showShimmer == false) {
//            binding.shimmer.shimmerViewf.goneExt()
            binding.bannerSplashMedium.visibleExt()
            binding.bannerSplashBottom.visibleExt()
        } else {
//            binding.shimmer.shimmerViewf.goneExt()
            binding.bannerSplashMedium.invisibleExt()
            binding.bannerSplashBottom.invisibleExt()
        }

    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
//        finishAffinity()
    }

    override fun onResume() {
        super.onResume()
        isResume = true
    }

    override fun onPause() {
        super.onPause()
        isResume = false
    }
}