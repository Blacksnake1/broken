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
import com.brokenscreen.prankapp.crack.screen.databinding.DialogHowToTurnOffFireBinding
import com.nekosoft.brokenglass.data.local.AppPreference
import com.nekosoft.brokenglass.extention.goneExt
import com.nekosoft.brokenglass.extention.visibleExt
import com.nekosoft.brokenglass.utils.ConstantsApp
import com.nekosoft.brokenglass.utils.clickSafe

class DialogHowToTurnOffFire(
    @NonNull context: Context,
    themeResId: Int,
    mBuilder: Builder,
) :
    Dialog(context) {
    private var binding: DialogHowToTurnOffFireBinding? = null
    private var mBuilder: Builder? = null
    private var dontShow = false
    var typeClick: TypeClick? = null

    init {
        this.mBuilder = mBuilder
    }

    class Builder {
        var mOnClickDialog: OnClickDialog? = null
        var content: String? = null

        interface OnClickDialog {
            fun onFireTouch()
            fun onFireBlow()
            fun onElectric()
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

        fun build(mContext: Context?): DialogHowToTurnOffFire {
            return DialogHowToTurnOffFire(mContext!!, R.style.DialogPermissrion, this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val displayMetrics = DisplayMetrics()
        setCancelable(true)
        binding = DialogHowToTurnOffFireBinding.inflate(LayoutInflater.from(context))
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
        when (typeClick) {
            TypeClick.TOUCH -> {
                uiOfType(true)
                val showTouch = AppPreference.getInstance(context)
                    ?.getBoolean(ConstantsApp.dialogDontShowTurnOffFireTouch, false)
                if (showTouch != null) {
                    dontShow = showTouch
                }
            }

            TypeClick.BLOW -> {
                uiOfType(true)
                val showBlow = AppPreference.getInstance(context)
                    ?.getBoolean(ConstantsApp.dialogDontShowTurnOffFireBlow, false)
                if (showBlow != null) {
                    dontShow = showBlow
                }
            }

            TypeClick.ELECTRIC -> {
                uiOfType(true)
                val showElectric = AppPreference.getInstance(context)
                    ?.getBoolean(ConstantsApp.dialogDontShowTurnOffElectric, false)
                if (showElectric != null) {
                    dontShow = showElectric
                }
            }
            else->{
                uiOfType(false)
            }
        }

        binding?.cbDontShow?.isChecked = dontShow
    }
    fun uiOfType(shower:Boolean){
        if(shower) {
            binding?.btnStart?.visibleExt()
            binding?.lncb?.visibleExt()
        }else{
            binding?.btnStart?.goneExt()
            binding?.lncb?.goneExt()
        }
    }

    private fun initEvent() {
        binding?.apply {
            btnStart.clickSafe {
                when (typeClick) {
                    TypeClick.TOUCH -> {
                        AppPreference.getInstance(context)
                            ?.setBoolean(ConstantsApp.dialogDontShowTurnOffFireTouch, dontShow)
                        mBuilder!!.mOnClickDialog!!.onFireTouch()
                    }

                    TypeClick.BLOW -> {
                        AppPreference.getInstance(context)
                            ?.setBoolean(ConstantsApp.dialogDontShowTurnOffFireBlow, dontShow)

                        mBuilder!!.mOnClickDialog!!.onFireBlow()
                    }

                    else -> {
                        AppPreference.getInstance(context)
                            ?.setBoolean(ConstantsApp.dialogDontShowTurnOffElectric, dontShow)
                        mBuilder!!.mOnClickDialog!!.onElectric()

                    }
                }
                dismiss()
            }
            cbDontShow.clickSafe {
                if (!dontShow) {
                    dontShow = true
                    cbDontShow.isChecked = true
                } else {
                    dontShow = false
                    cbDontShow.isChecked = false
                }
            }
        }
        if (mBuilder == null) return

        this.setOnDismissListener {
            mBuilder!!.mOnClickDialog!!.onDissmisListener()
        }
    }

}

enum class TypeClick {
    TOUCH,
    BLOW,
    ELECTRIC
}