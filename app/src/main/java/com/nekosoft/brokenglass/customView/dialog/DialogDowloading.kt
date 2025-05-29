package com.nekosoft.brokenglass.customView.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.annotation.NonNull
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.DialogDownloadingBinding
import com.brokenscreen.prankapp.crack.screen.databinding.DialogRecorderBinding

class DialogDowloading(
    @NonNull context: Context,
    themeResId: Int,
    mBuilder: Builder
) :
    Dialog(context) {
    private var binding: DialogDownloadingBinding? = null
    private var mBuilder: Builder? = null

    init {
        this.mBuilder = mBuilder
    }

    class Builder {
        var mOnClickDialog: OnClickDialog? = null
        var content: String? = null

        interface OnClickDialog {
            fun onDissmisListener()
        }

        fun setContent(content: String?): Builder {
            this.content = content
            return this
        }

        fun setOnClickListener(mOnClickDialog: OnClickDialog?): Builder {
            this.mOnClickDialog = mOnClickDialog
            return this
        }

        fun build(mContext: Context?): DialogDowloading {
            return DialogDowloading(mContext!!, R.style.DialogPermissrion, this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val displayMetrics = DisplayMetrics()

        setCancelable(false)
        binding = DialogDownloadingBinding.inflate(LayoutInflater.from(context))
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
    }

    private fun initEvent() {
        binding?.apply {
//            btnCancel.setOnClickListener {
//                if (mBuilder != null && mBuilder?.mOnClickDialog != null) {
//                    dismiss()
//                    mBuilder!!.mOnClickDialog!!.onCancel()
//                }
//            }

        }
        if (mBuilder == null) return

        this.setOnDismissListener {
            mBuilder!!.mOnClickDialog!!.onDissmisListener()
        }
    }
    fun dismissDownloadingDialog() {
        try {
            if (mBuilder != null && isShowing ) {
                dismiss()
            }
        }catch (e:Exception){

        }
    }
    fun showDownloadingDialog() {
        try {
            if (mBuilder != null && !isShowing) {
                show()
            }
        }catch (e:Exception){

        }
    }


}