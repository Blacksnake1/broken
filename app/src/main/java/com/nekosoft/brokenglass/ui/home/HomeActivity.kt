package com.nekosoft.brokenglass.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.brokenscreen.prankapp.crack.screen.BuildConfig
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.FragmentHomeBinding
import com.gianghv.DialogLoadingAds
import com.gianghv.utils.AdsConfigUtils.Companion.banner_home_status
import com.nekosoft.brokenglass.App
import com.nekosoft.brokenglass.alarm.AlarmUtils.setNoti
import com.nekosoft.brokenglass.base.BaseActivity
import com.nekosoft.brokenglass.customView.dialog.DialogRateUtil
import com.nekosoft.brokenglass.customView.dialog.DialogRequestAlarm
import com.nekosoft.brokenglass.extention.ContextExt.changeStatusBarColor
import com.nekosoft.brokenglass.extention.requestAndShowBannerMrect
import com.nekosoft.brokenglass.ui.EffectWeaponActivity
import com.nekosoft.brokenglass.ui.SettingActivity
import com.nekosoft.brokenglass.ui.crankEffect.CrankEffectActivity
import com.nekosoft.brokenglass.ui.fireScreen.FireScreenActivity
import com.nekosoft.brokenglass.ui.recent.RecentActivity
import com.nekosoft.brokenglass.ui.wallpaper.WallpaperActivity
import com.nekosoft.brokenglass.utils.ConstantsApp
import com.nekosoft.brokenglass.utils.ListUtils
import com.nekosoft.brokenglass.utils.clickSafe
import com.nekosoft.brokenglass.utils.isAlarmPermissionGranted
import com.nekosoft.brokenglass.utils.isNotificationPermissionGranted
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess


@AndroidEntryPoint
class HomeActivity : BaseActivity<FragmentHomeBinding>() {
    private var dialogLoadingAds: DialogLoadingAds? = null
    private var dialogRequestAlarm: DialogRequestAlarm? = null
    private var comebackFromSetting = false
    private var turnOpenApp: Int? = null
    private var homeAdapter: HomeAdapter? = null
    private var listItemHome = mutableListOf<Any>()

    override fun setScreen() {
        changeStatusBarColor(R.color.black)
        fullScreencall()
    }

    override fun setupData() {
        turnOpenApp = sharedPreferences?.getInt(ConstantsApp.TURN_OPEN_APP, 0)
        sharedPreferences?.setBoolean(ConstantsApp.OPENED_HOME, true)
        listItemHome.addAll(ListUtils.itemHomeList)
        val action = App.getInstance().turnOffOverlayService
        if (DialogRateUtil().canShowRate(this, action)) {
            DialogRateUtil().showDialogRate(this) {}
        }

        setupDialogRequestAlarm()

        if (App.getInstance().newTurnOpenHome) {
            App.getInstance().newTurnOpenHome = false
            requestNotificationPermission()
        }

        if (turnOpenApp == 2 &&
            sharedPreferences?.getInt("Deny_Alarm", 0) != 1 &&
            !isAlarmPermissionGranted(this) &&
            isNotificationPermissionGranted(this)
        ) {
            dialogRequestAlarm?.show()
        } else if (
            sharedPreferences?.getBoolean("seted_alarm", false) == false &&
            isAlarmPermissionGranted(this) &&
            isNotificationPermissionGranted(this)
        ) {
            sharedPreferences?.setBoolean("seted_alarm", true)
            setNoti(this)
        }

    }

    override fun setupViews() {
//        homeAdapter = HomeAdapter(
//            this,
//            onClick = { it, pos ->
//                if (!clicked) {
//                    clicked = true
//                    if (it.isloadSuccess) {
//                        when (it.name) {
//                            "broken_screen" -> {
//                                eventClickItem("btnOpenBrokenScreen")
//                            }
//
//                            "broken_wallpaper" -> {
//                                eventClickItem("btnOpenWallpaper")
//                            }
//
//                            "prank_screen" -> {
//                                eventClickItem("btnOpenFireScreen")
//                            }
//
//                            "destroy_screen" -> {
//                                eventClickItem("btnOpenDestroyScreen")
//                            }
//                        }
//                    } else {
//                        clicked = false
//                    }
//                }
//            }, isShowing = {}).apply {
//            setData(listScreen = listItemHome)
//        }
//        with(binding.rcvHome) {
//            adapter = homeAdapter
//            layoutManager = GridLayoutManager(this@HomeActivity, 2)
//        }
    }

    private fun setupDialogRequestAlarm() {
        dialogRequestAlarm = DialogRequestAlarm.Builder()
            .setOnClickListener(object : DialogRequestAlarm.Builder.OnClickDialog {
                override fun onOK() {
                    /** chuyển đến màn setting của thiết bị để bật alarm*/
                    App.getInstance().enableShowOpenAds = true
                    comebackFromSetting = true
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        requestAlarmPermissionLauncher.launch(intent)
                    }
                }

                override fun onCancel() {
                    sharedPreferences?.setInt("Deny_Alarm", 1)
                }
            }).build(this)

    }

//    private var loadNativeAds: LoadNativeAds? = null

    override fun setupAds() {
        dialogLoadingAds = DialogLoadingAds(this)
        requestBanner()


//        requestAndShowNative(native_intro_status,
//            BuildConfig.native_full_id,
//            actionDone = {
//                if (it is NativeAd) {
//                    val listcopy= listItemHome.toMutableList()
//                    listcopy.add(1, it)
//                    listcopy.add(5, it)
//                    homeAdapter?.setData(listcopy)
//                }
//            }, actionFail = {
//                homeAdapter?.setData(listItemHome)
//            })
    }
    private fun requestBanner(){
        Log.d("Tag123", "HomeActivity_requestBanner_168: ")
        requestAndShowBannerMrect(
            banner_home_status,
            BuildConfig.banner_home_id,
            binding.banner,
            actionDone = {
            },
            actionFail = {

            })
    }

    override fun onNetworkStateChanged(isConnected: Boolean) {
        super.onNetworkStateChanged(isConnected)
        setupAds()
    }

    private var clicked = false
    override fun setupEvent() {
        binding.btnSetting.clickSafe(500) {
            if (!clicked) {
                clicked = true
                eventOfButton("btnOpenSetting")

            }
        }
        binding.btnRecent.clickSafe(500) {
            if (!clicked) {
                clicked = true
                eventOfButton("btnRecentOnHome")
            }
        }
        binding.btnBroken.clickSafe(500) {
            if (!clicked) {
                clicked = true
                eventClickItem("btnOpenBrokenScreen")
            }
        }

        binding.btnWallpaper.clickSafe(500) {
            if (!clicked) {
                clicked = true
                eventClickItem("btnOpenWallpaper")
            }
        }

        binding.btnFire.clickSafe(500) {
            if (!clicked) {
                clicked = true
                eventClickItem("btnOpenFireScreen")
            }
        }

        binding.btnDestroy.clickSafe(500) {
            if (!clicked) {
                clicked = true
                eventClickItem("btnOpenDestroyScreen")
            }
        }
    }

    private fun eventClickItem(key: String) {
//        val clickOfTurnInstall =
//            sharedPreferences?.getBoolean(ConstantsApp.CLICK_OF_TURN_INSTANT, true)
//        if (clickOfTurnInstall == false) {
//            requestAndShowInter(
//                inter_function_status,
//                BuildConfig.inter_n_function_id,
//                dialogLoadingAds,
//                true,
//                actionDone = {
//                    eventOfButton(key)
//                },
//                actionFail = {
//                    eventOfButton(key)
//                },
//                null
//            )
//        } else {
//            sharedPreferences?.setBoolean(ConstantsApp.CLICK_OF_TURN_INSTANT, false)
        eventOfButton(key)
//        }
    }


    private fun eventOfButton(eventName: String) {
//        dialogLoadingAds?.dismissDialog()
        when (eventName) {
            "btnOpenBrokenScreen" -> {
                val intent = Intent(this, CrankEffectActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.slide_out)
            }

            "btnOpenWallpaper" -> {
                val intent = Intent(this, WallpaperActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.slide_out)
            }

            "btnOpenFireScreen" -> {
                val intent = Intent(this, FireScreenActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.slide_out)
            }

            "btnOpenDestroyScreen" -> {
                val intent = Intent(this, EffectWeaponActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.slide_out)
                finish()
            }

            "btnRecentOnHome" -> {
                sharedPreferences?.setBoolean("fromHome", true)
                val intent = Intent(this, RecentActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.slide_out)
            }

            "btnOpenSetting" -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.slide_out)
            }
        }
    }


    private fun requestNotificationPermission() {
        if (!isNotificationPermissionGranted(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestNotificationsPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private val requestNotificationsPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        if (result) {
            // cho phép quyền ở dialog của hệ thống
            if (sharedPreferences?.getInt(
                    ConstantsApp.TURN_OPEN_APP,
                    1
                ) == 2 && sharedPreferences?.getInt("Deny_Alarm", 0) != 1
            ) {
                dialogRequestAlarm?.show()
            }
        } else {
            //từ chối cấp quyền ở dialog của hệ thống
            if (sharedPreferences?.getInt("CANCEL_REQUEST_NOTIFI", 0) == 0) {
                //từ chối lần 1
                sharedPreferences?.setInt("CANCEL_REQUEST_NOTIFI", 1)
            } else {
                //từ chối lần 2
                sharedPreferences?.setInt("CANCEL_REQUEST_NOTIFI", 2)
                if (sharedPreferences?.getInt(ConstantsApp.TURN_OPEN_APP, 1) == 2 &&
                    sharedPreferences?.getInt("Deny_Alarm", 0) != 1
                ) {
                    dialogRequestAlarm?.show()
                }
            }
        }
    }


    private var requestAlarmPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == 0) {
                sharedPreferences?.setInt("Deny_Alarm", 1)
            } else {
                setNoti(this)
            }
        }

    override fun onPause() {
        super.onPause()
//        loadNativeAds?.stopLoadAds("destroyHome")
    }

    override fun onStop() {
        super.onStop()
        clicked = false
    }


    override fun onStart() {
        if (!comebackFromSetting) {
            App.getInstance().enableShowOpenAds = true
        }
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        App.getInstance().enableShowOpenAds = true

        if (DialogRateUtil().canShowRate(this, true) &&
            App.getInstance().SET_WALLPAPER_SUCCESSFUL
        ) {
            DialogRateUtil().showDialogRate(this) {}
        }
        App.getInstance().SET_WALLPAPER_SUCCESSFUL = false
        requestBanner()
    }

    private var doubleBackToExitPressedOnce = false

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (!doubleBackToExitPressedOnce) {
            Toast.makeText(
                this,
                resources.getString(R.string.click_bACK_again),
                Toast.LENGTH_SHORT
            )
                .show()
            doubleBackToExitPressedOnce = true
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                doubleBackToExitPressedOnce = false
            }, 2000)
        } else {
            this.finishAffinity()
            exitProcess(0)
        }
    }

    override fun getViewBinding(): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater)
    }
}