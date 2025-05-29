package com.nekosoft.brokenglass.customView.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.DialogRateUsBinding
import com.gianghv.utils.Utils.isNetworkConnected
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import com.nekosoft.brokenglass.data.local.AppPreference
import com.nekosoft.brokenglass.utils.ConstantsApp
import com.nekosoft.brokenglass.utils.clickSafe


class DialogRateUs() {

    private lateinit var binding: DialogRateUsBinding
    fun onCreateDialog(actvity: Activity, listener: () -> Unit?): Dialog {
        binding = DialogRateUsBinding.inflate(LayoutInflater.from(actvity))
        val dialog = Dialog(actvity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        val window = dialog.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.CENTER
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        window.attributes = wlp
        dialog.window!!.setBackgroundDrawable(ColorDrawable(actvity.getColor(R.color.transparent)))
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT
        )
        initView(actvity, dialog)
        dialog.setOnDismissListener {
            listener.invoke()
        }
        return dialog
    }


    private fun initView(actvity: Activity, dialog: Dialog) {
        initButton(actvity, dialog)

    }

    private fun openLink(context: Context, strUri: String?) {
        try {
            val uri = Uri.parse(strUri)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initButton(actvity: Activity, dialog: Dialog) {
        binding.btnRate.clickSafe {
            if (isNetworkConnected(actvity)) {
                showDiaglogRateOfSystem(actvity)
                dialog.dismiss()
            } else {
                Toast.makeText(
                    actvity,
                    actvity.getString(R.string.please_check_your_internet),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnRate.setOnLongClickListener {
            if (isNetworkConnected(actvity)) {
                showDiaglogRateOfSystem(actvity)
                dialog.dismiss()
            } else {
                Toast.makeText(
                    actvity,
                    actvity.getString(R.string.please_check_your_internet),
                    Toast.LENGTH_SHORT
                ).show()
            }
            return@setOnLongClickListener true
        }

        binding.btnLater.clickSafe {
            ConstantsApp.IS_NEW_TURN_FOR_SHOW_DIALOG_RATE = false
            DialogRateUtil().setTimeCancelRate(actvity, System.currentTimeMillis())
            DialogRateUtil().setLater(actvity)
            dialog.dismiss()
        }

        binding.btnCancel.clickSafe {
            ConstantsApp.IS_NEW_TURN_FOR_SHOW_DIALOG_RATE = false
            DialogRateUtil().setTimeCancelRate(actvity, System.currentTimeMillis())
            DialogRateUtil().setLater(actvity)
            dialog.dismiss()
        }

    }

    private fun showDiaglogRateOfSystem(actvity: Activity) {
        val manager = ReviewManagerFactory.create(actvity)
        manager.requestReviewFlow().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                manager.launchReviewFlow(actvity, task.result)
                    .addOnCompleteListener {
                        DialogRateUtil().setRate(actvity)
                    }
                    .addOnCanceledListener {
                    }
                    .addOnSuccessListener {
                    }
                    .addOnFailureListener {
                    }
            } else {
                @ReviewErrorCode val reviewErrorCode =
                    (task.exception as ReviewException).errorCode
            }
        }
    }
}

class DialogRateUtil() {
    companion object {
        const val RATED = "is rate"
        const val TIME_CANCEL_RATE = "time cancel rate"
        const val CLICKED_NOT_REALLY = "clicked btn not really"
    }

    // check user already rate or not
    fun showDialogRate(context: Activity, listener: () -> Unit) {
        DialogRateUs().onCreateDialog(context) {
            listener.invoke()
        }.show()
    }

    fun setRate(context: Context) {
        AppPreference.getInstance(context)?.setBoolean(RATED, true)
    }

    private fun getRate(context: Context): Boolean? {
        return AppPreference.getInstance(context)?.getBoolean(RATED, false)
    }

    fun setTimeCancelRate(context: Context, time: Long) {
        AppPreference.getInstance(context)?.setLong(TIME_CANCEL_RATE, time)
    }

    fun setLater(context: Context) {
        AppPreference.getInstance(context)?.setBoolean(CLICKED_NOT_REALLY, true)
    }

    //    fun canShowRate(context: Context, action: Boolean): Boolean {
//        val getTimeCancel = AppPreference.getInstance(context)?.getLong(
//            TIME_CANCEL_RATE,
//            System.currentTimeMillis()
//        )
//        if (AppPreference.getInstance(context)
//                ?.getInt(ConstantsApp.TURN_OPEN_APP, 1) == 1
//        ) { // lần đầu vào app thì sẽ không cho show ở màn main
//            return false
//        } else {
//            if (getRate(context) == false) {
//                if (action) { // sau khi dùng các tính năng của app sẽ yêu cầu show rate
//                    return true
//                } else {// dùng ở màn main mà cần check thời gian 3 ngày.
//                    if (getTimeCancel != null) {
////                        if (System.currentTimeMillis() >= getTimeCancel + 259200000L && ConstantsApp.IS_NEW_TURN_FOR_SHOW_DIALOG_RATE == true
//                        if (System.currentTimeMillis() >= getTimeCancel + 259200000L
//                        ) {
//                            return true
//                        }
//                    }
//                }
//            }
//        }
//        return false
//    }

    /* - lần đầu vào app:
    *    + không show ở màn main
    *    + lần đầu click các chức năng thì phải show
    *    + nếu tắt đi thì sau 3 ngày mới cho show*/

    fun canShowRate(context: Context, action: Boolean): Boolean {
        if (getRate(context) == true) {
            return false
        }

        if (!action) { // check dành cho màn main
            if (checkOver3Day(context)) {
                return true
            }
        } else {
            if (AppPreference.getInstance(context)?.getBoolean(CLICKED_NOT_REALLY, false) == true
            ) {      // trước đây đã click vào btnNotRelly thì sẽ dùng sự kiện này
                if (checkOver3Day(context)) {
                    return true
                }
            } else {  // trước đây chưa click vào btnNotRelly thì sẽ dùng sự kiện này
                return true
            }
        }
        return false
    }

    private fun checkOver3Day(context: Context): Boolean {
        val getTimeCancel = AppPreference.getInstance(context)
            ?.getLong(TIME_CANCEL_RATE, System.currentTimeMillis()) ?: System.currentTimeMillis()
        if (System.currentTimeMillis() >= getTimeCancel + 259200000L) return true
        return false
    }

}