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
import com.brokenscreen.prankapp.crack.screen.databinding.DialogRequestRewardBinding
import com.bumptech.glide.Glide
import com.gianghv.utils.ConstantsAds
import com.nekosoft.brokenglass.data.model.ScreenModel
import com.nekosoft.brokenglass.utils.clickSafe

class DialogRequestReward(
    @NonNull context: Context,
    themeResId: Int,
    mBuilder: Builder,
) :
    Dialog(context) {
    private var binding: DialogRequestRewardBinding? = null
    private var mBuilder: Builder? = null
    var screenModel: ScreenModel? = null
        set(value) {
            field = value
            initEvent()
        }

    init {
        this.mBuilder = mBuilder
    }

    class Builder() {
        var mOnClickDialog: OnClickDialog? = null
        var content: String? = null

        interface OnClickDialog {
            fun onOK()
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

        fun build(mContext: Context?): DialogRequestReward {
            return DialogRequestReward(mContext!!, R.style.DialogPermissrion, this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val displayMetrics = DisplayMetrics()

        setCancelable(false)
        binding = DialogRequestRewardBinding.inflate(LayoutInflater.from(context))
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
        initEvent()
    }

    private fun initEvent() {
        binding?.apply {
            tvWatchAds.isSelected = true
            btnCancel.clickSafe {
                if (mBuilder != null && mBuilder?.mOnClickDialog != null) {
                    dismissAdwardDialog()
                    ConstantsAds.isShowAdsFull = false
                    mBuilder!!.mOnClickDialog!!.onCancel()
                }
            }
            btnWatchAds.clickSafe {
                ConstantsAds.isShowAdsFull = true
                dismissAdwardDialog()
                mBuilder!!.mOnClickDialog!!.onOK()
            }
            Glide.with(context).load(screenModel?.url).into(ivReview)
        }
        if (mBuilder == null) return
    }

    fun dismissAdwardDialog() {
        try {
            if (mBuilder != null && isShowing) {
                dismiss()
            }
        } catch (e: Exception) {

        }
    }

    fun showAdwardDialog() {
        try {
            if (mBuilder != null && !isShowing) {
                show()
            }
        } catch (e: Exception) {

        }
    }

}