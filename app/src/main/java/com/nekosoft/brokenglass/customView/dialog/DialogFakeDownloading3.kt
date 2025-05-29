package com.nekosoft.brokenglass.customView.dialog

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.DialogFakeDownloadingBinding
import com.nekosoft.brokenglass.extention.goneExt
import com.nekosoft.brokenglass.extention.requestAndShowNativeOnDialog
import com.nekosoft.brokenglass.extention.visibleExt

class DialogFakeDownloading3(
    private val activity: Activity,
    private val adsStartus: String,
    private val adsId: String,
    val listenerDismiss: () -> Unit?
) {

    private val dialog: Dialog by lazy {
        Dialog(activity)
    }

    private var binding: DialogFakeDownloadingBinding =
        DialogFakeDownloadingBinding.inflate(LayoutInflater.from(activity))


    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        val window = dialog.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.FILL_VERTICAL
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.decorView?.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        window.attributes = wlp
        dialog.window?.setBackgroundDrawable(ColorDrawable(activity.getColor(R.color.transparent)))
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )


        dialog.setOnDismissListener {
            listenerDismiss.invoke()
        }

//        binding.btnDone.clickSafe {
//            dismissDialog()
//        }
        setFakeDownloading()
    }

    private fun setFakeDownloading() {
        binding.loadingView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                dismissDialog()
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    private fun initAds() {
        dialog.requestAndShowNativeOnDialog(
            activity,
            adsStartus,
            adsId,
            null,
            binding.nativeAdMedium,
            binding.itemMax,
            actionDone = {
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.vgNativeAds.goneExt()
                }, 5000)
            },
            actionFail = {
                binding.vgNativeAds.goneExt()
            }
        )
    }

    @SuppressLint("SetTextI18n")
    fun showDialog() {
        binding.loadingView.playAnimation()
        binding.loadingView.visibleExt()

        try {
            if (dialog != null && dialog.isShowing == false) {
                dialog.show()
                initAds()
            }
        } catch (e: Exception) {

        }
    }

    fun dismissDialog() {
        try {
            if (dialog != null && !activity.isFinishing) {
                dialog.dismiss()
            }
        } catch (e: Exception) {

        }
    }
}