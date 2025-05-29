package com.nekosoft.brokenglass.base

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewbinding.ViewBinding
import com.brokenscreen.prankapp.crack.screen.R
import com.nekosoft.brokenglass.utils.hideKeyword


abstract class BaseActivityNotFullScreen<VB : ViewBinding> : AppCompatActivity() {

    private var _binding: VB? = null
    open val binding get() = _binding!!


    var isFirstTimeLaunch = true

    open fun preventShowToastNoInternet() = false

    open fun toastPaddingBottom(): Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        window.statusBarColor = getColor(R.color.black_1d1c1c)
        super.onCreate(savedInstanceState)
        initViewBinding()
        setContentView(binding.root)
//        changeLightStatusBar()
        setUpAds()
        setUpData()
        setUpViews()
        setUpObserver()
        setUpEvent()

    }


    abstract fun setUpData()

    abstract fun setUpViews()

    open fun setUpObserver() {}


    abstract fun setUpEvent()

    open fun setUpAds() {}


    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }

    }


    private fun initViewBinding() {
        _binding = getViewBinding()
        hideKeyword(binding.root, this)
    }

    protected abstract fun getViewBinding(): VB

    override fun onDestroy() {

        super.onDestroy()
    }


    fun fullScreen() {
        window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    fun clearFullScreen() {
        window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    protected fun makerStatusBarTransparent() {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            statusBarColor = Color.TRANSPARENT
        }
    }

    protected fun changeTextStatusBar() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    protected fun changeStatusBarColor(color: Int) {
        window?.let { window ->
            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            // finally change the color
            window.statusBarColor = color

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )

            } else {
                val windowInsetController =
                    WindowCompat.getInsetsController(window, window.decorView)
                windowInsetController.isAppearanceLightStatusBars = true
            }
        }
    }

    private fun changeLightStatusBar() {
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
    }

    open fun fullScreencall() {
        //for new api versions.
        val decorView: View = window.decorView
        val uiOptions: Int =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.systemUiVisibility = uiOptions
    }


    private fun fullScreenActivity() {
        val window: Window = window
        val winParams: WindowManager.LayoutParams = window.attributes
        winParams.flags =
            winParams.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.transparent)
        window.statusBarColor = Color.TRANSPARENT
    }

}