package com.gianghv

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.gianghv.libads.R

@SuppressLint("SetTextI18n")
class DialogLoadingAds(
    val activity: Activity
) {
    private var dialog: Dialog? = null

    init {
        dialog = Dialog(activity)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.dialog_loading_ads)
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
        setShowText(true)
    }

    private fun setShowText(isShowText: Boolean) {
        if (isShowText) {
            dialog?.findViewById<TextView>(R.id.tvRuning)?.apply {
                visibility = View.VISIBLE
                text =
                    activity.getString(R.string.current_app_name) + " " + activity.getString(R.string.is_running_please_wait)
            }
        } else {
            dialog?.findViewById<TextView>(R.id.tvRuning)?.visibility = View.GONE
        }
    }


    fun showDialog(showText: Boolean = true): Dialog? {
        setShowText(showText)
        try {
            if (dialog != null && dialog?.isShowing == false && !activity.isFinishing) {
                dialog?.show()
            }
        } catch (e: Exception) {

        }

        return dialog
    }

    fun dismissDialog() {
        try {
            if (dialog != null && dialog?.isShowing == true && !activity.isFinishing) {
                Log.d("dialog", "dismissDialog_66: ")
                dialog?.dismiss()
            }
        } catch (e: Exception) {

        }
    }

    fun isShowing(): Boolean? {
        return dialog?.isShowing
    }
}