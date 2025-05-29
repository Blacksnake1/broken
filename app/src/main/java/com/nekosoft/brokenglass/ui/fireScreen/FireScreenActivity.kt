package com.nekosoft.brokenglass.ui.fireScreen

import android.Manifest.permission.RECORD_AUDIO
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.brokenscreen.prankapp.crack.screen.BuildConfig
import com.brokenscreen.prankapp.crack.screen.BuildConfig.banner_home_id
import com.brokenscreen.prankapp.crack.screen.BuildConfig.banner_home_id
import com.brokenscreen.prankapp.crack.screen.BuildConfig.native_function_id
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.ActivityFireBinding
import com.gianghv.utils.AdsConfigUtils
import com.gianghv.utils.AdsConfigUtils.Companion.banner_home_status
import com.gianghv.utils.AdsConfigUtils.Companion.native_downloading_status
import com.gianghv.utils.AdsConfigUtils.Companion.native_firescreen_status
import com.gianghv.utils.ConstantsAds.isShowAdsFull
import com.nekosoft.brokenglass.App
import com.nekosoft.brokenglass.base.BaseStoragePermissionActivity
import com.nekosoft.brokenglass.customView.dialog.DialogFakeDownloading
import com.nekosoft.brokenglass.customView.dialog.DialogFakeDownloading3
import com.nekosoft.brokenglass.customView.dialog.DialogHowToTurnOffFire
import com.nekosoft.brokenglass.customView.dialog.DialogRateUtil
import com.nekosoft.brokenglass.customView.dialog.DialogReadyToUse
import com.nekosoft.brokenglass.customView.dialog.DialogRequestRecorder
import com.nekosoft.brokenglass.customView.dialog.TypeClick
import com.nekosoft.brokenglass.data.local.AppPreference
import com.nekosoft.brokenglass.extention.ContextExt.changeStatusBarColor
import com.nekosoft.brokenglass.extention.ContextExt.isOnline
import com.nekosoft.brokenglass.extention.goneExt
import com.nekosoft.brokenglass.extention.requestAndShowBanner
import com.nekosoft.brokenglass.extention.requestAndShowInterHaveNativeFull
import com.nekosoft.brokenglass.extention.requestAndShowNative
import com.nekosoft.brokenglass.service.OverlayService
import com.nekosoft.brokenglass.ui.home.HomeActivity
import com.nekosoft.brokenglass.utils.ConstantsApp
import com.nekosoft.brokenglass.utils.ConstantsApp.FROM_FIRE_TO
import com.nekosoft.brokenglass.utils.ConstantsApp.THE_FIRST_OPEN_FIRESCREEN
import com.nekosoft.brokenglass.utils.clickSafe
import com.nekosoft.brokenglass.utils.isServiceRunning
import com.nekosoft.brokenglass.utils.logEventFirebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FireScreenActivity : BaseStoragePermissionActivity<ActivityFireBinding>() {
    @Inject
    lateinit var appPreference: AppPreference
    private var dialogRequestRecord: DialogRequestRecorder? = null
    private var dialogHowToTurnOffFire: DialogHowToTurnOffFire? = null
    private var dontShowBlow: Boolean? = null
    private lateinit var dialogFakeDownloading: DialogFakeDownloading3
    private lateinit var dialogReadyToUse: DialogReadyToUse
    private var isFirstOpenFireScreen = false
    private var isClicked = false // biến này để chặn double click vào các btn
    private var typeClick: TypeClick? = null

    //    private var dialogReadyToUseIsShowed = false
    private var backFromEffect = false
    private var clickAds = false
    private var allowShowReadyToUseDialog = false

    override fun setScreen() {
        fullScreencall()
        changeStatusBarColor(R.color.black)
    }

    override fun setupAds() {
//        requestAndShowBanner(
//            banner_home_id,
//            banner_home_status,
//            true,
//            binding.vgNative
//        )
        requestAndShowNative(
            native_firescreen_status,
            BuildConfig.native_function_id,
            admobView = binding.adMobNativeAds,
            actionDone = {},
            actionFail = {},
            actionClicked = {
                allowShowReadyToUseDialog = false
            }
        )
    }

    override fun onNetworkStateChanged(isConnected: Boolean) {
        super.onNetworkStateChanged(isConnected)
        setupAds()
    }

    override fun setupData() {
        isFirstOpenFireScreen = appPreference.getBoolean(THE_FIRST_OPEN_FIRESCREEN, false)
        backFromEffect = intent.getBooleanExtra(FROM_FIRE_TO, false)
        if (DialogRateUtil().canShowRate(this, true) && App.getInstance().backToFireActivity == 1) {
            DialogRateUtil().showDialogRate(this) {}
        }
    }

    private var fromSetting = false
    override fun setupViews() {
        setupDialogHowToTurnOffFire()
        setupDialogRequestRecord()
        setupDialogAfterClick()
//        if (!isOnline()) {
//            binding.itemNative.goneExt()
//        }
    }

    private fun setupDialogAfterClick() {
        dialogFakeDownloading =
            DialogFakeDownloading3(this, native_downloading_status, native_function_id) {
                if (!isShowAdsFull) {
                    allowShowReadyToUseDialog = false
                    dialogReadyToUse.showDialog(null,"fire")
                }
            }

        dialogReadyToUse = DialogReadyToUse(this, "fire") {
            requestAndShowInterHaveNativeFull(
                this,
                AdsConfigUtils.inter_done_status,
                BuildConfig.inter_function_id,
                actionDone = {
                    eventOpenFireEffect()
                }
            )

        }
    }

    override fun eventAfferNativeFullDismiss() {
        // Những trường hợp chặn show dialog ready to use:
        // - đang loading,
        // - click ads rồi quay về, sau khi tắt native full thì không show
        // - tắt native sau khi quay lại từ màn hiệu ứng

//        Những trường hợp cho show dialog ready to use:
//        - click btn chạy hết loading là show dialog
        Log.d("Tag123", "FireScreenActivity_eventAfferNativeFullDismiss_133:$allowShowReadyToUseDialog / $backFromEffect ")
        if (allowShowReadyToUseDialog && !backFromEffect) {
            allowShowReadyToUseDialog = false
            dialogReadyToUse.showDialog(null,"fire")
        }
    }

    private fun eventOpenFireEffect() {
        if (typeClick == TypeClick.BLOW) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                logEventFirebase(
                    this,
                    "click_continue_permission_blow"
                )
                dialogRequestRecord?.show()
            } else {
                typeClick?.let { it1 -> eventBtn(it1) }
            }
        } else {
            typeClick?.let { it1 -> eventBtn(it1) }
        }
    }

    private fun setupDialogRequestRecord() {
        dialogRequestRecord = DialogRequestRecorder.Builder()
            .setOnClickListener(object : DialogRequestRecorder.Builder.OnClickDialog {
                override fun onCancel() {}

                override fun onOK() {
                    fromSetting = true
                    App.getInstance().enableShowOpenAds = false
                    recorderPermissionResultReceiver()
                }

                override fun onDissmisListener() {
                    isClicked = false
                }

            }).build(this)

    }

    private fun setupDialogHowToTurnOffFire() {
        dialogHowToTurnOffFire = DialogHowToTurnOffFire.Builder()
            .setOnClickListener(object : DialogHowToTurnOffFire.Builder.OnClickDialog {
                override fun onFireTouch() {
                    flow(TypeClick.TOUCH)
                }

                override fun onFireBlow() {
                    flow(TypeClick.BLOW)
                }

                override fun onElectric() {
                    flow(TypeClick.ELECTRIC)
                }

                override fun onDissmisListener() {
                    isClicked = false
                }
            }).build(this)
    }


    override fun setupEvent() {
        binding.btnFireTouch.clickSafe {
            clickBtn(TypeClick.TOUCH)
        }

        binding.btnElectric.clickSafe {
            clickBtn(TypeClick.ELECTRIC)
        }

        binding.btnFireBlow.clickSafe {
            clickBtn(TypeClick.BLOW)
        }

        binding.apply {
            btnBack.setOnClickListener {
                onBackPressed()
            }
            btnInfor.clickSafe {
                dialogHowToTurnOffFire?.uiOfType(false)
                dialogHowToTurnOffFire?.show()
            }
        }
    }

    private fun clickBtn(typeClick: TypeClick) {
        if (isClicked) return
        allowShowReadyToUseDialog = true
        isClicked = true
        backFromEffect = false
        this.typeClick = typeClick
        dialogFakeDownloading.showDialog()
    }

    private fun eventBtn(typeClick: TypeClick) {
        val isShow = when (typeClick) {
            TypeClick.TOUCH -> {
                AppPreference.getInstance(this)
                    ?.getBoolean(ConstantsApp.dialogDontShowTurnOffFireTouch, false)
            }

            TypeClick.BLOW -> {
                AppPreference.getInstance(this)
                    ?.getBoolean(ConstantsApp.dialogDontShowTurnOffFireBlow, false)
            }

            else -> {
                AppPreference.getInstance(this)
                    ?.getBoolean(ConstantsApp.dialogDontShowTurnOffElectric, false)
            }
        }

        if (isShow == false) {
            dialogHowToTurnOffFire?.typeClick = typeClick
            dialogHowToTurnOffFire?.uiOfType(true)
            dialogHowToTurnOffFire?.show()
        } else {
            flow(typeClick)
        }
    }


    private fun flow(typeClick: TypeClick) {
        if (isServiceRunning(this, OverlayService::class.java)) {
            OverlayService.turnOffService(this)
        }
        App.getInstance().enableShowOpenAds = true
        when (typeClick) {
            TypeClick.TOUCH -> goToFireTouch()
            TypeClick.BLOW -> goToFireBlow()
            TypeClick.ELECTRIC -> goToElectric()
        }
    }

    private fun goToFireTouch() {
        val intent = Intent(this@FireScreenActivity, FireTouchActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    private fun goToFireBlow() {
        val intent = Intent(this@FireScreenActivity, FireBlowActicity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    private fun goToElectric() {
        val intent = Intent(this@FireScreenActivity, ElectricActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }


    private fun recorderPermissionResultReceiver() {
        var a = appPreference.getInt("permission_recoder", 0)
        a++
        if (a < 3) {
            appPreference.setInt("permission_recoder", a)
            recorderPermissionResultReceiver.launch(RECORD_AUDIO)
        } else {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            activityResultReceiver.launch(intent)
        }
    }

    private val activityResultReceiver =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            fromSetting = false
            if (ContextCompat.checkSelfPermission(
                    this,
                    RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (dontShowBlow == false) {
                    dialogHowToTurnOffFire?.typeClick = TypeClick.BLOW
                    dialogHowToTurnOffFire?.uiOfType(true)
                    dialogHowToTurnOffFire?.show()
                } else {
                    flow(TypeClick.BLOW)
                }
            }
        }

    private val recorderPermissionResultReceiver =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (dontShowBlow == false) {
                    dialogHowToTurnOffFire?.typeClick = TypeClick.BLOW
                    dialogHowToTurnOffFire?.uiOfType(true)
                    dialogHowToTurnOffFire?.show()
                } else {
                    flow(TypeClick.BLOW)
                }
            }
        }


    override fun onStart() {
        if (!fromSetting) {
            App.getInstance().enableShowOpenAds = true
        }
        super.onStart()
    }

    override fun onDestroy() {
        appPreference.setBoolean(THE_FIRST_OPEN_FIRESCREEN, false)
        super.onDestroy()
    }


    override fun onResume() {
        super.onResume()
        if (!fromSetting) {
            App.getInstance().enableShowOpenAds = true
        }
    }

    override fun onPause() {
        super.onPause()
    }


    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        isClicked = false

        val intent = Intent(this, HomeActivity::class.java)
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out)
        startActivity(intent)
        finish()
    }

    override fun getViewBinding(): ActivityFireBinding {
        return ActivityFireBinding.inflate(layoutInflater)
    }
}