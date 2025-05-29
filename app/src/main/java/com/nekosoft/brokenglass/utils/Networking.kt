package com.nekosoft.brokenglass.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import java.io.IOException
import java.lang.reflect.Method
import java.net.HttpURLConnection
import java.net.URL


object Networking {
    /** kiểm tra xem có kết nối mạng hay không. Đối với trường hợp 3g không dữ liệu thì sẽ phản hồi là không có mạng*/
    fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)&& capabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_VALIDATED
            ) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)&& capabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_VALIDATED
            ) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)&& capabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_VALIDATED
            ) -> true
            else -> false
        }
    }

    /** kiểm tra xem đang kết nối 3G, 4G hay không */
    fun isEnableMobileData(context: Context): Boolean {
        var mobileDataEnabled = false
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
        if (tm?.simState == TelephonyManager.SIM_STATE_READY) {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE)
            try {
                val cmClass = Class.forName(cm.javaClass.name)
                val method: Method = cmClass.getDeclaredMethod("getMobileDataEnabled")
                method.isAccessible = true
                mobileDataEnabled = method.invoke(cm) as Boolean
            } catch (e: java.lang.Exception) {
            }
        }
        return mobileDataEnabled
    }

    /** kiểm tra xem đang kết nối wifi hay không */
    fun isEnableWifi(context: Context): Boolean {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.isWifiEnabled
        return wifiManager.isWifiEnabled
    }
}