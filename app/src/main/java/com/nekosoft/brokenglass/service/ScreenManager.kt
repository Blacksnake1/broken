package com.nekosoft.brokenglass.service

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager



class ScreenManager(
    context: Context,
    private val mWindowManager: WindowManager
) {

    private val DEFAULT_NAV_BAR_HEIGHT_DP = 48
    private val DEFAULT_STATUS_BAR_HEIGHT_DP = 25
    private val mResources: Resources = context.resources
    private var mStatusBarHeight = -1
    private var mNavigationBarHeight = -1

    val screenHeight: Int
        get() {
            val display = mWindowManager.defaultDisplay
            val dm = DisplayMetrics()
            display.getRealMetrics(dm)
            return if (inPortrait) {
                dm.heightPixels + statusBarHeightPx + navigationBarHeightPx
            } else {
                dm.heightPixels + statusBarHeightPx
            }
        }

    val statusBarHeightPx: Int
        get() {
            if (mStatusBarHeight == -1) {
                val statusBarHeightId =
                    mResources.getIdentifier("status_bar_height", "dimen", "android")

                if (statusBarHeightId > 0) {
                    mStatusBarHeight = mResources.getDimensionPixelSize(statusBarHeightId)
                } else {
                    mStatusBarHeight = dpToPx(DEFAULT_STATUS_BAR_HEIGHT_DP)
                }
            }

            return mStatusBarHeight
        }

    private val navigationBarHeightPx: Int
        get() {
            if (mNavigationBarHeight == -1) {
                val navBarHeightId =
                    mResources.getIdentifier("navigation_bar_height", "dimen", "android")

                if (navBarHeightId > 0) {
                    mNavigationBarHeight = mResources.getDimensionPixelSize(navBarHeightId)
                } else {
                    mNavigationBarHeight = dpToPx(DEFAULT_NAV_BAR_HEIGHT_DP)
                }
            }

            return mNavigationBarHeight
        }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(), mResources.displayMetrics
        ).toInt()
    }

    private val inPortrait: Boolean
        get() = mResources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT


}