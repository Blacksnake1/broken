package com.nekosoft.brokenglass.ui

import android.animation.Animator
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.brokenscreen.prankapp.crack.screen.BuildConfig
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.FragmentBrokenScreenBinding
import com.bumptech.glide.Glide
import com.gianghv.DialogLoadingAds
import com.gianghv.utils.AdsConfigUtils
import com.gianghv.utils.AdsConfigUtils.Companion.native_downloading_status
import com.gianghv.utils.ConstantsAds.isShowAdsFull
import com.nekosoft.brokenglass.base.BaseActivity
import com.nekosoft.brokenglass.customView.dialog.DialogClickNotifiForRemoveBrokenGlass
import com.nekosoft.brokenglass.customView.dialog.DialogReadyToUse
import com.nekosoft.brokenglass.customView.dialog.DialogRequestTwoPermission
import com.nekosoft.brokenglass.data.model.EffectBrokenModel
import com.nekosoft.brokenglass.extention.disableViewsExt
import com.nekosoft.brokenglass.extention.enableViewsExt
import com.nekosoft.brokenglass.extention.goneExt
import com.nekosoft.brokenglass.extention.requestAndShowInterHaveNativeFull
import com.nekosoft.brokenglass.extention.requestAndShowNative
import com.nekosoft.brokenglass.extention.setBackgroundTint
import com.nekosoft.brokenglass.extention.visibleExt
import com.nekosoft.brokenglass.service.OverlayService
import com.nekosoft.brokenglass.ui.crankEffect.BROKEN_REVIEW
import com.nekosoft.brokenglass.utils.ConstantsApp
import com.nekosoft.brokenglass.utils.ConstantsApp.EFFECT
import com.nekosoft.brokenglass.utils.ConstantsApp.SCREEN_EFFECT
import com.nekosoft.brokenglass.utils.ConstantsApp.SELLECT_SHAKE
import com.nekosoft.brokenglass.utils.ConstantsApp.SELLECT_TOUCH
import com.nekosoft.brokenglass.utils.clickSafe
import com.nekosoft.brokenglass.utils.isNotificationPermissionGranted
import com.nekosoft.brokenglass.utils.isServiceRunning
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BrokenScreenActivity : BaseActivity<FragmentBrokenScreenBinding>() {

    private var screenBroken: EffectBrokenModel? = null
    private var sellectEffect: String? = null
    private var dialogClickNotifiForRemoveBrokenGlass: DialogClickNotifiForRemoveBrokenGlass? = null
    private var dialogTwoPermission: DialogRequestTwoPermission? = null
    private var fromSetting = false

    private var dialogLoadingAds: DialogLoadingAds? = null
    private var count = 0
    private var dialogReadyToUse: DialogReadyToUse? = null
    private var clicked = false
    private var isLoading = true
    private var dialogReadyToUseIsShowed = false


    init {
        onBackPressedAction = {
            if (!isLoading) finish()
        }
    }

    override fun setScreen() {
        fullScreencall()
    }

    override fun setupAds() {
        dialogLoadingAds = DialogLoadingAds(this)
        requestAndShowNative(
            native_downloading_status,
            BuildConfig.native_function_id,
            null,
            binding.nativeAdMedium,
            binding.maxNativeAds,
            null,
            null,
            null
        )
    }


    override fun setupData() {
        screenBroken = intent.getParcelableExtra(BROKEN_REVIEW)
        sellectEffect = sharedPreferences?.getString(SCREEN_EFFECT, SELLECT_TOUCH)
    }


    private fun eventDismissDialogReadyUse() {
        binding.clLock2.visibleExt()
        binding.loadingView.goneExt()

    }

    override fun setupViews() {
        setDialogTwoPermission()
        setDialogClickNotifiForRemoveBrokenGlass()
        setDialogReadyToUse()
        setFakeDownloading()


        Glide.with(this).load(screenBroken?.image).into(binding.ivScreen)

        if (sellectEffect == SELLECT_TOUCH) {
            eventTouch()
        } else {
            eventShake()
        }

    }

    private fun setDialogReadyToUse() {
        dialogReadyToUse = DialogReadyToUse(this, "BrokenScreen") {
            enableViewsExt(binding.btnBack, binding.btnStart, binding.vgShake, binding.vgTouch)
            requestAndShowInterHaveNativeFull(
                this,
                AdsConfigUtils.inter_done_status,
                BuildConfig.inter_function_id,
                actionDone = {
                    eventDismissDialogReadyUse()
                }
            )
        }
    }

    private fun setDialogTwoPermission() {
        dialogTwoPermission = DialogRequestTwoPermission(
            this,
            onOK = {
                eventOfBtnStart()
            },

            eventSwitchNotifi = {
                fromSetting = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:${packageName}")
                    resultNotifiListerner.launch(intent)
//                        requestNotificationsPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            },

            eventSwitchApearOnTop = {
                fromSetting = true
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                    data = Uri.parse("package:${packageName}")
                }
                resultApearOnTopListerner.launch(intent)
            },
            listenerDismiss = {
                clicked = false
            }
        )
    }

    private fun setDialogClickNotifiForRemoveBrokenGlass() {
        dialogClickNotifiForRemoveBrokenGlass = DialogClickNotifiForRemoveBrokenGlass.Builder()
            .setOnClickListener(object :
                DialogClickNotifiForRemoveBrokenGlass.Builder.OnClickDialog {
                override fun onOK() {
                    eventTurnOnOverlay()
                }

            }).build(this)

    }

    private fun setFakeDownloading() {
        binding.loadingView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                disableViewsExt(
                    binding.btnBack,
                    binding.btnStart,
                    binding.vgShake,
                    binding.vgTouch,
                    opacity = 1f
                )
            }

            override fun onAnimationEnd(animation: Animator) {
                isLoading = false
                if (!isShowAdsFull) {
                    binding.loadingView.goneExt()
                    screenBroken?.let {
                        dialogReadyToUseIsShowed = true
                        dialogReadyToUse?.showDialog(it, "BrokenScreen")

                    }
                }
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    override fun eventAfferNativeFullDismiss() {
        if (!isLoading && !dialogReadyToUseIsShowed) {
            binding.loadingView.goneExt()
            screenBroken?.let {
                dialogReadyToUseIsShowed = true
                dialogReadyToUse?.showDialog(it, "BrokenScreen")
            }
        }
    }

    override fun setupEvent() {
        binding.vgTouch.clickSafe {
            eventTouch()
        }
        binding.vgShake.clickSafe {
            eventShake()
        }
        binding.btnBack.clickSafe {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnStart.clickSafe {
            if (isServiceRunning(this, OverlayService::class.java)) {
                Toast.makeText(
                    this,
                    getString(R.string.please_turn_off_the_current_broken_screen),
                    Toast.LENGTH_SHORT
                ).show()
                return@clickSafe
            }
            // chưa có 1 trong 2 quyền notifi và apearOnTop thì sẽ show dialog, còn có rồi thì thôi
            if (clicked) return@clickSafe
            clicked = true
            if (!isNotificationPermissionGranted(this) ||
                !Settings.canDrawOverlays(this)
            ) {
                dialogTwoPermission?.showDialog()
            } else {
                eventOfBtnStart()
            }
        }
    }


    /** phương thức xứ lý hành động khi click vào btnStart mà đã có đủ 2 quyền
     * notification và apearOnTop
     * */
    private fun eventOfBtnStart() {
        if (sharedPreferences?.getBoolean(ConstantsApp.isShowClickNotifi, false) == true) {
            eventTurnOnOverlay()
        } else {
            dialogClickNotifiForRemoveBrokenGlass?.show()
        }
    }

    private fun eventTurnOnOverlay() {
        OverlayService.turnOnService(this)
        sharedPreferences?.setString(SCREEN_EFFECT, sellectEffect)
        sharedPreferences?.saveObjectToSharePreference(EFFECT, screenBroken)
        hideApp()
    }

    private fun hideApp() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun eventShake() {
        sellectEffect = SELLECT_SHAKE
        resources?.getColor(R.color.gray_7b7c80)
            ?.let {
                binding.tvTouch.setTextColor(it)
                binding.ivTouch.setColorFilter(it)
                binding.cbTouch.setImageResource(R.drawable.ic_uncheck)
                binding.vgTouch.setBackgroundResource(R.color.gray_35383f)
            }
        resources?.getColor(R.color.yellow_ff7c05)
            ?.let {
                binding.tvShake.setTextColor(it)
                binding.ivShake.setColorFilter(it)
                binding.cbShake.setImageResource(R.drawable.ic_checked)
                binding.vgShake.setBackgroundResource(R.drawable.bg_ra5)
                binding.vgShake.setBackgroundTint(R.color.black_1d1c1c)
            }
    }

    private fun eventTouch() {
        sellectEffect = SELLECT_TOUCH
        resources?.getColor(R.color.yellow_ff7c05)
            ?.let {
                binding.tvTouch.setTextColor(it)
                binding.ivTouch.setColorFilter(it)
                binding.cbTouch.setImageResource(R.drawable.ic_checked)
                binding.vgTouch.setBackgroundResource(R.drawable.bg_ra5)
                binding.vgTouch.setBackgroundTint(R.color.black_1d1c1c)

            }
        resources?.getColor(R.color.gray_7b7c80)
            ?.let {
                binding.tvShake.setTextColor(it)
                binding.ivShake.setColorFilter(it)
                binding.cbShake.setImageResource(R.drawable.ic_uncheck)
                binding.vgShake.setBackgroundResource(R.color.gray_35383f)
            }
    }


    private val resultNotifiListerner = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        if (isNotificationPermissionGranted(this)) {
            //đồng ý cấp quyền sau khi show dialog từ setting về app
            dialogTwoPermission?.initView()

        } else {
            //từ chối cấp quyền sau khi show dialog từ setting về app
            dialogTwoPermission?.initView()
        }
    }

    private val resultApearOnTopListerner =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Settings.canDrawOverlays(this)) {
                dialogTwoPermission?.initView()
            } else {
                dialogTwoPermission?.initView()
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        dialogLoadingAds?.dismissDialog()
    }

    override fun onResume() {
        super.onResume()
//        loadNativeAds?.resumeLoadAds("resumeBrokenScreen")
        if (isServiceRunning(this, OverlayService::class.java)) {
            OverlayService.turnOffService(this)
        }
    }

    override fun onPause() {
        super.onPause()
//        loadNativeAds?.stopLoadAds("pauseBrokenScreen")
    }

    override fun getViewBinding(): FragmentBrokenScreenBinding {
        return FragmentBrokenScreenBinding.inflate(layoutInflater)
    }
}