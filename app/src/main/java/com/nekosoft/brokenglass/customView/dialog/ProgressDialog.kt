package com.nekosoft.brokenglass.customView.dialog

import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.util.Log
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import com.brokenscreen.prankapp.crack.screen.R

class ProgressDialog @JvmOverloads constructor(
    var activity: Activity,
    var isCancelable: Boolean = false
) {

    private lateinit var imgOutSideLoading: ImageView

    private var isShowing: Boolean = false
    private var mListenner: Dialoglistern? = null
    private var dialog: Dialog? = null

    init {
        setupDialog()
    }

    /**
     * Setup progress dialog
     * */
    private fun setupDialog() {
        dialog = Dialog(activity)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.dialog_progress_sample)
        val window = dialog?.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.CENTER
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        window.attributes = wlp
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    fun showDialogLoading(): Dialog? {
        if (dialog != null && dialog?.isShowing == false && !activity.isFinishing) {
            dialog?.show()
        }
        return dialog
    }

    fun dismissDialog() {
        if (dialog != null && dialog?.isShowing == true && !activity.isFinishing) {
            dialog?.dismiss()
        }
    }

    /**
     * dialog run animation
     * */
    private fun progressAnimation() {
        val scale = ScaleAnimation(
            1f,
            0.9f,
            1f,
            0.9f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        scale.duration = 1000
        scale.repeatCount = -1
        scale.repeatMode = ValueAnimator.REVERSE
        imgOutSideLoading.startAnimation(scale)
    }

    fun listenDialog(mListenner: Dialoglistern) {
        this.mListenner = mListenner
    }

    interface Dialoglistern {
        fun listenDismiss()
    }
}