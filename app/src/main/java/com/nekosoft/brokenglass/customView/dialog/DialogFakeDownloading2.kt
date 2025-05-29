package com.nekosoft.brokenglass.customView.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.brokenscreen.prankapp.crack.screen.BuildConfig.native_function_id
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.DialogFakeDownloading2Binding
import com.gianghv.utils.AdsConfigUtils
import com.nekosoft.brokenglass.extention.goneExt
import com.nekosoft.brokenglass.extention.invisibleExt
import com.nekosoft.brokenglass.extention.requestAndShowNativeOnDialog
import com.nekosoft.brokenglass.extention.visibleExt

class DialogFakeDownloading2(
    private val activity: Activity,
    listenerDismiss: () -> Unit?
) {
    private var dialog: Dialog? = null

    private var adsShowDone = false
    private var loadingDone = false
    private var binding: DialogFakeDownloading2Binding =
        DialogFakeDownloading2Binding.inflate(LayoutInflater.from(activity))
    private var count = 0
    private val listcount = listOf(7, 10, 15, 14, 8, 12)
    private var action = ""

    init {
        dialog = Dialog(activity)
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
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(activity.getColor(R.color.transparent)))
        dialog?.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT
        )
        initEvent()


        dialog?.setOnDismissListener {
            listenerDismiss.invoke()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun initEvent() {
        binding.ivFolder.text = activity.getString(R.string.download_successful)
//        binding.ivCancel.clickSafe {
//            dismissDialog()
//        }
    }


    private fun initAds() {
        dialog?.requestAndShowNativeOnDialog(
            activity,
            AdsConfigUtils.native_downloading_status,
            native_function_id,
            null,
            binding.nativeAdMedium,
            binding.itemMax,
            actionDone = {
                adsShowDone = true
//                if (loadingDone) {
                Handler(Looper.getMainLooper()).postDelayed({
                    dismissDialog()
                }, 5000)
//                }
            },
            actionFail = {
                adsShowDone = true
                binding.vgNativeAds.goneExt()
//                if (loadingDone) {
                Handler(Looper.getMainLooper()).postDelayed({
                    dismissDialog()
                }, 5000)
//                }
                binding.ivLine.goneExt()
            }
        )
    }


//    @SuppressLint("SetTextI18n")
//    private fun resetDialog() {
//        binding.tvPercent.apply {
//            visibleExt()
//            text = "0"
//        }
//
//        binding.progressBar.apply {
//            visibleExt()
//            setProgress(0, true)
//        }
//
//        binding.ivCancel.goneExt()
//        binding.ivSucces.invisibleExt()
//        binding.ivFolder.text = "${activity.getString(R.string.downloading)}..."
//        timer.cancel()
//        count = 0
//    }


//    val timer = object : CountDownTimer(100000, 500) {
//        override fun onTick(millisUntilFinished: Long) {
//            count += listcount.random()
//            setProgress(count)
//        }
//
//        override fun onFinish() {
//
//        }
//    }

//    @SuppressLint("SetTextI18n")
//    fun setProgress(p: Int) {
//        binding.tvPercent.apply {
//            visibleExt()
//            text = p.toString()
//        }
//        binding.progressBar.setProgress(p, true)
//        if (p >= 100) {
//            loadingDone = true
//            if (adsShowDone) {
//                timer.cancel()
//                dismissDialog()
//                binding.apply {
//                    ivFolder.text = activity.getString(R.string.download_successful)
//                    ivSucces.visibleExt()
//                    vgProgess.goneExt()
//                    progressBar.goneExt()
//                }
//            }
//        }
//    }

    @SuppressLint("SetTextI18n")
    fun showDialog() {
        loadingDone = false
        try {
            if (dialog != null && dialog?.isShowing == false) {
                dialog?.show()
//                timer.start()
                initAds()
            }
        } catch (e: Exception) {

        }
    }

    fun dismissDialog() {
        try {
            if (dialog != null && !activity.isFinishing) {
//                resetDialog()
                dialog?.dismiss()
            }
        } catch (e: Exception) {

        }
    }
}

fun showDialogFakeDownloading2(activity: Activity, listenerDismiss: () -> Unit?) {
    DialogFakeDownloading2(activity, listenerDismiss).showDialog()
}