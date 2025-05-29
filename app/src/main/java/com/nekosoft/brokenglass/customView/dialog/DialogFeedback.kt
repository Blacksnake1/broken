package com.nekosoft.brokenglass.customView.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.NonNull
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.DialogFeedbackBinding
import com.nekosoft.brokenglass.utils.Networking

class DialogFeedback(
    @NonNull context: Context,
    themeResId: Int,
    mBuilder: Builder,
) : Dialog(context) {
    private var binding: DialogFeedbackBinding? = null
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
        }

        fun setContent(content: String?): Builder {
            this.content = content
            return this
        }

        fun setOnClickListener(mOnClickDialog: OnClickDialog?): Builder {
            this.mOnClickDialog = mOnClickDialog
            return this
        }

        fun build(mContext: Context?): DialogFeedback {
            return DialogFeedback(mContext!!, R.style.DialogPermissrion, this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setCancelable(true)
        binding = DialogFeedbackBinding.inflate(LayoutInflater.from(context))
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

    @SuppressLint("ClickableViewAccessibility")
    private fun initEvent() {
        binding?.apply {
            btnCancel.setOnClickListener {
                if (mBuilder != null && mBuilder?.mOnClickDialog != null) {
                    mBuilder!!.mOnClickDialog!!.onCancel()
                    dismiss()
                }
            }
            btnSend.setOnClickListener {
                if (!Networking.isNetworkConnected(context)) {
                    Toast.makeText(context, R.string.error_network, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    if (binding?.edtReports?.text.toString().trim().isNotEmpty()) {
                        val emailIntent = Intent(Intent.ACTION_VIEW)
                        emailIntent.type = "message/rfc822"
                        emailIntent.data = Uri.parse("mailto:")
                        emailIntent.putExtra(
                            Intent.EXTRA_EMAIL,
                            arrayOf("nekosoft.feedback.app@gmail.com")
                        )
                        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback App Broken Grass")
                        emailIntent.putExtra(
                            Intent.EXTRA_TEXT,
                            binding?.edtReports?.text?.trim().toString()
                        )

                        try {
                            context.startActivity(Intent.createChooser(emailIntent, "Send mail..."))
                        } catch (ex: ActivityNotFoundException) {
                            Toast.makeText(
                                context,
                                "You need install email",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        mBuilder!!.mOnClickDialog!!.onOK()
                        dismiss()
                    } else {
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.fill_email),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }
            edtReports.setOnTouchListener { view, event ->
                view.parent.requestDisallowInterceptTouchEvent(true)
                if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                    view.parent.requestDisallowInterceptTouchEvent(false)
                }
                return@setOnTouchListener false
            }
        }
        if (mBuilder == null) return
    }

    override fun dismiss() {
        super.dismiss()
        binding?.edtReports?.text?.clear()
    }
}