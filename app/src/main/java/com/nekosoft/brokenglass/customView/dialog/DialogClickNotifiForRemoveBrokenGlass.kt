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
import com.brokenscreen.prankapp.crack.screen.databinding.DialogClickNotifiForRemoveBrokenGlassBinding
import com.nekosoft.brokenglass.data.local.AppPreference
import com.nekosoft.brokenglass.utils.ConstantsApp.isShowClickNotifi

class DialogClickNotifiForRemoveBrokenGlass(
    @NonNull context: Context,
    themeResId: Int,
    mBuilder: Builder,
) :
    Dialog(context) {
    private var binding: DialogClickNotifiForRemoveBrokenGlassBinding? = null
    private var mBuilder: Builder? = null

    init {
        this.mBuilder = mBuilder
    }

    class Builder {
        var mOnClickDialog: OnClickDialog? = null
        var content: String? = null

        interface OnClickDialog {
            fun onOK()
        }

        fun setContent(content: String?): Builder {
            this.content = content
            return this
        }

        fun setOnClickListener(mOnClickDialog: OnClickDialog?): Builder {
            this.mOnClickDialog = mOnClickDialog
            return this
        }

        fun build(mContext: Context?): DialogClickNotifiForRemoveBrokenGlass {
            return DialogClickNotifiForRemoveBrokenGlass(mContext!!, R.style.Dialog, this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val displayMetrics = DisplayMetrics()

        setCancelable(true)
        binding = DialogClickNotifiForRemoveBrokenGlassBinding.inflate(LayoutInflater.from(context))
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
        initView()
        initEvent()
    }

    private fun initView() {
        if (AppPreference.getInstance(context)?.getBoolean(isShowClickNotifi, false) == false) {
            ischecked = false
            binding?.cb?.setImageResource(R.drawable.checkbox_nocheck)
        } else {
            ischecked = true
            binding?.cb?.setImageResource(R.drawable.checkbox_checked)
        }
    }

    private var ischecked: Boolean? = null
    private fun initEvent() {

        binding?.apply {
            cb.setOnClickListener {
                if (ischecked == false) {
                    ischecked = true
                    binding?.cb?.setImageResource(R.drawable.checkbox_checked)
                } else {
                    ischecked = false
                    binding?.cb?.setImageResource(R.drawable.checkbox_nocheck)
                }
            }
            btnGotIt.setOnClickListener {
                ischecked?.let { it1 ->
                    AppPreference.getInstance(context)?.setBoolean(
                        isShowClickNotifi,
                        it1
                    )
                }
                mBuilder!!.mOnClickDialog!!.onOK()
                dismiss()
            }

        }
        if (mBuilder == null) return
    }

}