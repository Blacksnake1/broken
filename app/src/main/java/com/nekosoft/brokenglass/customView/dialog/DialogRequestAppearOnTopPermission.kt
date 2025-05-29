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
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.DialogRequestAppearOnTopPermissionBinding
import com.bumptech.glide.Glide

class DialogRequestAppearOnTopPermission(
    context: Context?,
    themeResId: Int,
    mBuilder: Builder,
) : Dialog(context!!) {
    private var binding: DialogRequestAppearOnTopPermissionBinding? = null
    private var mBuilder: Builder? = null

    init {
        this.mBuilder = mBuilder
    }

    class Builder {
        var mOnClickDialog: OnClickDialog? = null
        var content: String? = null

        interface OnClickDialog {
            fun onAgree()
        }

        fun setContent(content: String?): Builder {
            this.content = content
            return this
        }

        fun setOnClickListener(mOnClickDialog: OnClickDialog?): Builder {
            this.mOnClickDialog = mOnClickDialog
            return this
        }

        fun build(mContext: Context?): DialogRequestAppearOnTopPermission {
            return DialogRequestAppearOnTopPermission(
                mContext!!, R.style
                    .DialogPermissrion, this
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val displayMetrics = DisplayMetrics()
//        setCanceledOnTouchOutside(true)
//        setCancelable(true)
        binding = DialogRequestAppearOnTopPermissionBinding.inflate(LayoutInflater.from(context))
        setContentView(binding?.root!!)
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val layoutParams: WindowManager.LayoutParams? = this.window?.attributes
        layoutParams?.x = 100; // left margin
//        layoutParams?.y = 170; // bottom margin
        this.window?.attributes = layoutParams
        Glide.with(context).load(R.drawable.logo_tiny).into(binding?.image!!)
        initEvent()
    }


    private fun initEvent() {
        binding?.apply {
            btnAgree.isSelected = true
            btnAgree.setOnClickListener {
                if (mBuilder != null && mBuilder?.mOnClickDialog != null) {
                    mBuilder?.mOnClickDialog?.onAgree()
                    dismiss()
                }
            }
            view2.setOnClickListener {
                dismiss()
            }
            view3.setOnClickListener {
                dismiss()
            }
        }
        if (mBuilder == null) return

    }

    override fun onBackPressed() {
        dismiss()
    }

}