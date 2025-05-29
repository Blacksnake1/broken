package com.nekosoft.brokenglass.extention

import android.app.Activity
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.gianghv.utils.AdsConfigUtils
import com.gianghv.utils.Utils

object ContextExt {

    fun Activity.getAdsNetworking(key: String): Int {
        return AdsConfigUtils(this).getStatusAdsNetworking(key)
    }

    fun Fragment.getAdsNetworking(key: String): Int {
        return AdsConfigUtils(requireContext()).getStatusAdsNetworking(key)
    }

    fun Activity.isOnline() = Utils.isNetworkConnected(this)
    fun Fragment.isOnline() = Utils.isNetworkConnected(requireContext())

    fun FragmentActivity.changeStatusBarColor(color: Int) {
        window?.let { window ->
            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            // finally change the color
            window.statusBarColor = resources.getColor(color)
        }
    }

}
