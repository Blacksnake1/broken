package com.nekosoft.brokenglass.customView.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.brokenscreen.prankapp.crack.screen.BuildConfig
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.DialogReadyToUseBinding
import com.bumptech.glide.Glide
import com.gianghv.utils.AdsConfigUtils
import com.nekosoft.brokenglass.data.model.EffectBrokenModel
import com.nekosoft.brokenglass.data.model.ScreenModel
import com.nekosoft.brokenglass.extention.disableExt
import com.nekosoft.brokenglass.extention.enableExt
import com.nekosoft.brokenglass.extention.goneExt
import com.nekosoft.brokenglass.extention.requestAndShowNativeOnDialog
import com.nekosoft.brokenglass.utils.clickSafe

class DialogReadyToUse(
    private val activity: Activity,
    private val key: String,
    listenerDismiss: () -> Unit?
) {
    private val dialog: Dialog by lazy {
        Dialog(activity)
    }

    private var adsShowDone = false
    private var binding: DialogReadyToUseBinding =
        DialogReadyToUseBinding.inflate(LayoutInflater.from(activity))
    private var imageThumbnail: Any? = null

    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        val window = dialog.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.FILL_VERTICAL
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        window.attributes = wlp
        dialog.window?.setBackgroundDrawable(ColorDrawable(activity.getColor(R.color.transparent)))
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT
        )


        dialog?.setOnDismissListener {
            listenerDismiss.invoke()
        }
        binding.btnDone.clickSafe {
            dismissDialog()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun initView() {
        if (imageThumbnail is EffectBrokenModel) {
            Glide.with(activity).load((imageThumbnail as EffectBrokenModel).image)
                .into(binding.ivThumb)
        } else if (imageThumbnail is ScreenModel) {
            val wallpaperBroken = imageThumbnail as ScreenModel
            if (wallpaperBroken.name == "wp1" || wallpaperBroken.name == "wp2") {
                Glide.with(activity).asBitmap().load(wallpaperBroken.drawble)
                    .into(binding.ivThumb)
            } else {
                Glide.with(activity).asBitmap().load(wallpaperBroken.url)
                    .into(binding.ivThumb)
            }
        } else {
            Glide.with(activity).asBitmap().load(R.drawable.thumb_dialog).into(binding.ivThumb)
        }
    }


    private fun initAds(key: String) {
        var keyStartus: String = ""
        var idAds: String = ""
//        when (key) {
//            "BrokenScreen" -> {
//                keyStartus = AdsConfigUtils.native_crank_done_status
//                idAds = BuildConfig.native_function_id
//            }

//            "weapon" -> {
//                keyStartus = native_weapon_status
//            }
//
//            "crank" -> {
//                keyStartus = native_weapon_status
//            }
//
//            else -> {
//                keyStartus = native_fire_status
//            }
//        }
        binding.btnDone.disableExt(0.3f)

        dialog?.requestAndShowNativeOnDialog(
            activity,
            AdsConfigUtils.native_crank_done_status,
            BuildConfig.native_function_id,
            null,
            binding.itemAdmob,
            binding.itemMax,
            actionDone = {
                adsShowDone = true
                binding.btnDone.enableExt()
            },
            actionFail = {
                adsShowDone = true
                binding.vgNativeAds.goneExt()
                binding.btnDone.enableExt()
            }
        )
    }

    @SuppressLint("SetTextI18n")
    fun showDialog(item: Any?,key: String) {
        imageThumbnail = item
        try {
            if (dialog != null && dialog?.isShowing == false) {
                initAds(key)
                dialog?.show()
            }
        } catch (e: Exception) {

        }
    }

    fun dismissDialog() {
        try {
            if (dialog != null && !activity.isFinishing) {
                dialog?.dismiss()
            }
        } catch (e: Exception) {

        }
    }
}