package com.gianghv.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.applovin.mediation.MaxAd
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdValue


object Utils {
//    fun isOnline(context: Context): Boolean {
//        return try {
//            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//            val netInfo = cm.activeNetworkInfo
//            netInfo != null && netInfo.isConnected
//        } catch (e: NullPointerException) {
//            e.printStackTrace()
//            false
//        }
//    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) && capabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_VALIDATED
            ) -> true

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) && capabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_VALIDATED
            ) -> true

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) && capabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_VALIDATED
            ) -> true

            else -> false
        }
    }

    fun postRevenueAdjust(ad: AdValue, adUnit: String?) {
        val adjustAdRevenue = AdjustAdRevenue("AdMob")
        val rev = ad.valueMicros.toDouble() / 1000000
        adjustAdRevenue.setRevenue(rev, ad.currencyCode)
        adjustAdRevenue.setAdRevenueUnit(adUnit)
        Adjust.trackAdRevenue(adjustAdRevenue)
    }

    fun postRevenueMaxToAdjust(ad: MaxAd, adUnit: String?) {
        val adjustAdRevenue = AdjustAdRevenue("AppLovin MAX")
        adjustAdRevenue.setRevenue(ad.revenue, "USD")
        adjustAdRevenue.setAdRevenueUnit(adUnit)
        Adjust.trackAdRevenue(adjustAdRevenue)
    }




}