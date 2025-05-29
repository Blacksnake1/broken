package com.gianghv

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.*
import android.widget.TextView
import com.gianghv.libads.R

class DialogLoadingOpenAds(private val context: Context) {
    private var dialog: Dialog? = null

    init {
        dialog = Dialog(context)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.dialog_loading_open_ads)
        val window = dialog?.window
        val wlp = window?.attributes
        wlp?.gravity = Gravity.CENTER
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog?.findViewById<TextView>(R.id.tvLoadingAds)?.text =
            context.getString(R.string.welcome_back)
    }

    fun showDialogLoading(): Dialog? {
        if (dialog != null && dialog?.isShowing == false) {
            dialog?.show()
        }
        return dialog
    }

    fun dismissDialog() {

        if (dialog != null && dialog != null) {
            dialog?.dismiss()
        }
    }

    fun isShowing(): Boolean {
        if (dialog != null && dialog?.isShowing == true) {
            return true
        }
        return false
    }

}