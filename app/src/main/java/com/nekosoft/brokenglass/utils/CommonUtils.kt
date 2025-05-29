package com.nekosoft.brokenglass.utils

import android.app.Activity
import android.app.ActivityManager
import android.app.AlarmManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.pm.PackageManager
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.Settings
import android.text.TextPaint
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.gianghv.utils.AdsConfigUtils
import com.gianghv.utils.AdsConfigUtils.Companion.TIME_CLOSED_INTER
import com.google.android.gms.ads.AdError
import com.google.firebase.analytics.FirebaseAnalytics
import com.nekosoft.brokenglass.App
import com.nekosoft.brokenglass.data.local.AppPreference
import java.util.Locale

fun isPermissionGranted(context: Context?, permission: String?): Boolean {
    return if (context == null || TextUtils.isEmpty(permission)) false
    else ContextCompat.checkSelfPermission(
        context,
        permission!!
    ) == PackageManager.PERMISSION_GRANTED
}

fun hideKeyword(view: View?, activity: Activity?) {
    // Set up touch listener for non-text box views to hide keyboard.
    if (view !is EditText) {
        view?.setOnTouchListener { v, event ->
            hideSoftKeyboard(activity)
            false
        }
    }

    //If a layout container, iterate over children and seed recursion.
    if (view is ViewGroup) {
        for (i in 0 until view.childCount) {
            val innerView = view.getChildAt(i)
            hideKeyword(innerView, activity)
        }
    }
}

fun hideSoftKeyboard(activity: Activity?) {
    if (activity == null) {
        return
    }
    val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    val v = activity.currentFocus
    if (imm != null && v != null) {
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }
}

fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
    val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
    val services = manager.getRunningServices(Integer.MAX_VALUE)

    for (service in services) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}

fun canShowInter(context: Context): Boolean {
    val getTimeClosedInter = AppPreference.getInstance(context)?.getLong(
        TIME_CLOSED_INTER,
        0
    )

    if (getTimeClosedInter != null) {
        if (System.currentTimeMillis() >= getTimeClosedInter + AdsConfigUtils(context).getTimeAdsNetworking(
                AdsConfigUtils.SHOW_INTER_ADS
            )
        ) {
//        if (System.currentTimeMillis() >= getTimeClosedInter + 0) {
            return true
        }
    } else {
        return true
    }
    return false
}

fun setTimeAdsClose(context: Context) {
    AppPreference.getInstance(context)?.setLong(
        TIME_CLOSED_INTER,
        System.currentTimeMillis()
    )
}

fun canDrawOverlay(context: Context): Boolean {
    if (Settings.canDrawOverlays(context)) {
        // continue here - permission was granted
        return true
    }
    return false
}

fun isAlarmPermissionGranted(context: Context): Boolean {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE)!! as AlarmManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        alarmManager.canScheduleExactAlarms()
    } else {
        true
    }
}

//fun isNotifiPermissionGrandted(context: Context): Boolean {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//        if (ContextCompat.checkSelfPermission(
//                context,
//                Manifest.permission.POST_NOTIFICATIONS
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            return true
//        }
//    }
//    return false
//}
fun isNotificationPermissionGranted(context: Context): Boolean {
    return NotificationManagerCompat.from(context).areNotificationsEnabled()
}

fun setGradientTextColor(
    vertical: Boolean,
    text: String,
    textView: TextView,
    color1: Int,
    color2: Int
) {
    val colors = intArrayOf(color1, color2)
    textView.text = text.uppercase(Locale.getDefault())
    val paint: TextPaint = textView.paint
    val width = paint.measureText(text)
    val textShader: Shader = if (vertical) {
        LinearGradient(0f, 0f, 0f, textView.textSize, color1, color2, Shader.TileMode.CLAMP)
    } else {
        LinearGradient(0f, 0f, width, textView.textSize, colors, null, Shader.TileMode.CLAMP)
    }
    textView.paint.shader = textShader
}

fun logEventFirebase(context: Context, key: String) {
    FirebaseAnalytics.getInstance(context).logEvent(key, null)
}

fun logEventFirebase(context: Context, adsName: String, adError: AdError) {
    FirebaseAnalytics.getInstance(context)
        .logEvent("showFail:${adsName}-code:${adError.code}-message:${adError.message}", null)
}

 fun saveCurrentPosition(context: Context, cupos: Int) {
    AppPreference.getInstance(context)?.setInt(ConstantsApp.CURRENT_POSITION, cupos)
}

fun getCurrentPosition(context: Context): Int? {
    return AppPreference.getInstance(context)?.getInt(ConstantsApp.CURRENT_POSITION, 0)
}





