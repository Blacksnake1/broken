package com.nekosoft.brokenglass.customView.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.annotation.NonNull
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.DialogRequestInternetBinding
import com.nekosoft.brokenglass.utils.Networking

class DialogRequestInternet(
    @NonNull context: Context,
    themeResId: Int,
    mBuilder: Builder,
) :
    Dialog(context) {
    private var binding: DialogRequestInternetBinding? = null
    private var mBuilder: Builder? = null

    init {
        this.mBuilder = mBuilder
    }

    class Builder {
        var mOnClickDialog: OnClickDialog? = null
        var content: String? = null

        interface OnClickDialog {
            fun onSwitchWifi()
            fun onCancel3g()
            fun onCancel()

        }

        fun setContent(content: String?): Builder {
            this.content = content
            return this
        }

        fun setOnClickListener(mOnClickDialog: OnClickDialog?): Builder {
            this.mOnClickDialog = mOnClickDialog
            return this
        }

        fun build(mContext: Context?): DialogRequestInternet {
            return DialogRequestInternet(mContext!!, R.style.DialogPermissrion, this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val displayMetrics = DisplayMetrics()

        setCancelable(true)
        binding = DialogRequestInternetBinding.inflate(LayoutInflater.from(context))
        setContentView(binding?.root!!)
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val layoutParams: WindowManager.LayoutParams? = this.window?.attributes
        layoutParams?.x = 100; // left margin
//        layoutParams?.y = 170; // bottom margin
        this.window?.attributes = layoutParams
        initEvent()
        if (Networking.isEnableWifi(context)) {
            binding?.swWifi?.setImageResource(R.drawable.ic_switch_on)
        } else {
            binding?.swWifi?.setImageResource(R.drawable.ic_switch_off)
        }

    }

    private fun initEvent() {
        binding?.apply {
            swWifi.setOnClickListener {
                if (mBuilder != null && mBuilder?.mOnClickDialog != null) {
                    mBuilder!!.mOnClickDialog!!.onSwitchWifi()
                    dismiss()
                }
            }
            sw3G.setOnClickListener {
                if (mBuilder != null && mBuilder?.mOnClickDialog != null) {
                    mBuilder!!.mOnClickDialog!!.onCancel3g()
                    dismiss()
                }
            }

            btnDone.setOnClickListener {
                mBuilder!!.mOnClickDialog!!.onCancel()
                dismiss()
            }

        }
        if (mBuilder == null) return

    }

    fun dismissRequestInternetDialog() {
        try {
            if (mBuilder != null && isShowing) {
                dismiss()
            }
        }catch (e:Exception){

        }
    }
}