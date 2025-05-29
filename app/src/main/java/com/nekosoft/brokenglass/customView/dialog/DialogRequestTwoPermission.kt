package com.nekosoft.brokenglass.customView.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.DialogRequestTwoPermissionBinding
import com.nekosoft.brokenglass.utils.canDrawOverlay
import com.nekosoft.brokenglass.extention.disableExt
import com.nekosoft.brokenglass.extention.enableExt
import com.nekosoft.brokenglass.utils.isNotificationPermissionGranted
import com.nekosoft.brokenglass.utils.clickSafe

class DialogRequestTwoPermission(
    private val context: Activity,
    onOK: () -> Unit?,
    eventSwitchNotifi: () -> Unit?,
    eventSwitchApearOnTop: () -> Unit?,
    listenerDismiss: () -> Unit,
) {
    private var binding: DialogRequestTwoPermissionBinding =
        DialogRequestTwoPermissionBinding.inflate(LayoutInflater.from(context))
    private var dialog: Dialog? = null

    init {
        dialog = Dialog(context)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(binding.root)
        val window = dialog?.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.CENTER
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.window!!.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        window.attributes = wlp
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(context.getColor(R.color.transparent)))
        dialog?.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT
        )


        dialog?.setOnDismissListener {
            listenerDismiss.invoke()
        }
        binding.apply {
            btnCancel.clickSafe {
                dismissDialog()
            }
            btnAccept.clickSafe {
                onOK.invoke()
                dismissDialog()
            }
            switchApearOnTop.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    if (!canDrawOverlay(context)) {
                        eventSwitchApearOnTop.invoke()
                    }
                }
            }

            switchNotifi.setOnCheckedChangeListener { buttonView, isChecked ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (isChecked) {
                        if (!isNotificationPermissionGranted(context)) {
                            eventSwitchNotifi.invoke()
                        }
                    }
                }
            }
        }
    }


    fun initView() {
        binding.apply {
            switchNotifi.isChecked = isNotificationPermissionGranted(context)
            switchApearOnTop.isChecked = Settings.canDrawOverlays(context)

            if (!isNotificationPermissionGranted(context) || !Settings.canDrawOverlays(context)) {
                disableBtnContinue()
            } else {
                enableBtnContinue()
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                switchNotifi.disableExt(50f)
            }

            if (!isNotificationPermissionGranted(context)) {
                notifiOff()
            } else {
                notifiOn()
            }

            if (!Settings.canDrawOverlays(context)) {
                apearOnTopOff()
            } else {
                apearOnTopOn()
            }
        }
    }


    fun notifiOn() {
        binding.switchNotifi.apply {
            isChecked = true
            disableExt(1f)
        }
    }

    fun notifiOff() {
        binding.switchNotifi.isChecked = false
    }

    fun apearOnTopOn() {
        binding.switchApearOnTop.apply {
            isChecked = true
            disableExt(1f)
        }
    }

    fun apearOnTopOff() {
        binding.switchApearOnTop.isChecked = false
    }

    fun enableBtnContinue() {
        binding.btnAccept.enableExt()
        binding.btnAccept.setBackgroundResource(R.drawable.bg_ra50_gra)
    }

    fun disableBtnContinue() {
        binding.btnAccept.disableExt(null)
        binding.btnAccept.setBackgroundResource(R.drawable.bg_ra50_gray)
    }

    @SuppressLint("SetTextI18n")
    fun showDialog() {
        try {
            if (dialog != null && dialog?.isShowing == false) {
                dialog?.show()
                initView()
            }
        } catch (e: Exception) {

        }
    }

    fun dismissDialog() {
        try {
            if (dialog != null && !context.isFinishing) {
                dialog?.dismiss()
            }
        } catch (e: Exception) {

        }
    }

}