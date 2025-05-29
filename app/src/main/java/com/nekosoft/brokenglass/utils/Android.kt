package com.nekosoft.brokenglass.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.brokenscreen.prankapp.crack.screen.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nekosoft.brokenglass.App

fun Long?.orZero(): Long = this ?: 0
fun Int?.orZero(): Int = this ?: 0
fun Int?.orOne() : Int = this ?: 1
fun Double?.orZero(): Double = this ?: 0.0
fun Float?.orZero(): Float = this ?: 0.0F
fun Float?.orFive(): Float = this ?: 5.0F
fun Boolean?.orFalse(): Boolean = this ?: false
fun String?.orNotProvide() : String = this ?: getStringExt(R.string.not_provide)
fun String?.orNotInformation() : String = this ?: getStringExt(R.string.not_have_information)

fun Any?.isNullOrBlankExt(): Boolean {
    return if (this is String?) this.isNullOrBlank()
    else this == null
}

/**
 * Used to retrieve object from the Json.
 **/
inline fun <reified T> Any.toObject(): T? {
    return try {
        val jsonData = if (this is String) this else Gson().toJson(this)
        GsonBuilder().create().fromJson(jsonData, T::class.java)
    } catch (e: Throwable) {
        Log.e("tag123", "toObject_36: $e")
        null
    }
}

fun Any.toJson(): String {
    return Gson().toJson(this)
}

fun getStringExt(@StringRes stringRes: Int): String {
    return try {
        App.getInstance().getString(stringRes)
    } catch (e: Exception) {
        "EMPTY_STRING"
    }
}

fun getStringExt(@StringRes resId: Int, vararg formatArgs: Any?): String {
    return App.getInstance().getString(resId, *formatArgs)
}

fun getDrawableExt(@DrawableRes drawableRes: Int,context : Context = App.getInstance()): Drawable? {
    return ContextCompat.getDrawable(context, drawableRes)
}
