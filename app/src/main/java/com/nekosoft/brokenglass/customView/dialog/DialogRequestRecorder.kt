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
import com.brokenscreen.prankapp.crack.screen.databinding.DialogRecorderBinding

class DialogRequestRecorder(
    @NonNull context: Context,
    themeResId: Int,
    mBuilder: Builder
) :
    Dialog(context) {
    private var binding: DialogRecorderBinding? = null
    private var mBuilder: Builder? = null

    init {
        this.mBuilder = mBuilder
    }

    class Builder {
        var mOnClickDialog: OnClickDialog? = null
        var content: String? = null

        interface OnClickDialog {
            fun onCancel()
            fun onOK()
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

        fun build(mContext: Context?): DialogRequestRecorder {
            return DialogRequestRecorder(mContext!!, R.style.DialogPermissrion, this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val displayMetrics = DisplayMetrics()

        setCancelable(true)
        binding = DialogRecorderBinding.inflate(LayoutInflater.from(context))
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
            btnCancel.setOnClickListener {
                if (mBuilder != null && mBuilder?.mOnClickDialog != null) {
                    dismiss()
                    mBuilder!!.mOnClickDialog!!.onCancel()
                }
            }
            btnPermitManually.setOnClickListener {
                dismiss()
                mBuilder!!.mOnClickDialog!!.onOK()
            }

        }
        if (mBuilder == null) return

        this.setOnDismissListener {
            mBuilder!!.mOnClickDialog!!.onDissmisListener()
        }
    }


}