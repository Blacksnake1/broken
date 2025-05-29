package com.nekosoft.brokenglass.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.fragment.app.Fragment

class RequestDrawOverAppsPermission {
    private var activity: Activity? = null
    private var fragment: Fragment? = null
    private var context: Context? = null

    constructor(activity: Activity?) {
        this.activity = activity
    }

    constructor(fragment: Fragment) {
        this.fragment = fragment
        context = fragment.context
    }


    fun needsPermissionApproval(): Boolean {
        /** check if we already  have permission to draw over other apps  */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                return true
            }
        }
        return false
    }

    fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context!!.packageName)
            )
            fragment!!.startActivityForResult(intent, REQUEST_CODE)
        }
    }

    fun requestCodeMatches(requestCode: Int): Boolean {
        return REQUEST_CODE == requestCode
    }

    fun canDrawOverlays(): Boolean {
        return canDrawOverlay(activity!!.applicationContext)
    }

    fun requestPermissionDrawOverOtherApps(context: Context) {

        /* check if we already have permission to draw over other apps */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                /* if not construct intent to request permission */
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context.packageName)
                )
                /* request permission via start activity for result */
                activity!!.startActivityForResult(
                    intent,
                    REQUEST_CODE
                )
            }
        }
    }

    companion object {
        /**
         * code to post/handler request for permission
         */
        private const val REQUEST_CODE = 5463
    }
}