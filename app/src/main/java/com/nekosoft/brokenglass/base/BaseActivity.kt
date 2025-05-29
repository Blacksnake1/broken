package com.nekosoft.brokenglass.base

import android.content.BroadcastReceiver
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.brokenscreen.prankapp.crack.screen.BuildConfig.inter_resume_id
import com.brokenscreen.prankapp.crack.screen.R
import com.gianghv.utils.AdsConfigUtils.Companion.inter_resume_status
import com.nekosoft.brokenglass.App
import com.nekosoft.brokenglass.data.local.AppPreference
import com.nekosoft.brokenglass.extention.ContextExt.isOnline
import com.nekosoft.brokenglass.extention.requestAndShowInterHaveNativeFull
import com.nekosoft.brokenglass.ui.language.LanguageModel
import com.nekosoft.brokenglass.ui.language.LanguageUtils
import com.nekosoft.brokenglass.ui.language.LanguageUtils.LANGUAGE_CURRENT_CODE
import com.nekosoft.brokenglass.ui.language.LanguageUtils.saved
import com.nekosoft.brokenglass.utils.hideKeyword
import kotlinx.coroutines.launch
import java.util.Locale


abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    var sharedPreferences: AppPreference? = null

    private var _binding: VB? = null
    open val binding get() = _binding!!

    private var networkStateChangeReceiver: BroadcastReceiver? = null

    private var isInternetConnected = true

    private var toastNoInternet: Toast? = null

    private var toastHasInternet: Toast? = null

    var isFirstTimeLaunch = true
    var languageModelList = mutableListOf<LanguageModel>()
    var itemIndex = -1
    var countryCodeSelected = "en"
    var deviceLang = ""
    private lateinit var networkStateMonitor: NetworkStateMonitor
    private var passedSetupAds = false
    private var passedNetworkStateMonitor = false
    var onBackPressedAction: (() -> Unit)? = null


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        val updatedContext = updateBaseContextLocale(base)
        applyOverrideConfiguration(updatedContext.resources.configuration)
    }

    private fun updateBaseContextLocale(context: Context): Context {
        sharedPreferences = AppPreference.getInstance(context)
        languageModelList = LanguageUtils.languageList()
        deviceLang = Locale.getDefault().language.toString()
        if (sharedPreferences?.getBoolean(
                saved,
                false
            ) == false
        ) { // lần đầu vào app chưa accept ngôn ngữ
            itemIndex = languageModelList.indexOfFirst { it.nameCode == deviceLang }
            if (itemIndex == -1) {
                itemIndex = 0
            }
            countryCodeSelected = languageModelList[itemIndex].nameCode
        } else {
            countryCodeSelected =
                sharedPreferences?.getString(LANGUAGE_CURRENT_CODE, "en").toString()
            itemIndex = languageModelList.indexOfFirst { it.nameCode == countryCodeSelected }
        }
        val locale = Locale(countryCodeSelected)
        Locale.setDefault(locale)
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        setScreen()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else if (onBackPressedAction != null) {
                    onBackPressedAction?.invoke() // Gọi callback nếu có
                }
            }
        })

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                onActivityStarted()
            }
        }

        initViewBinding()
        setContentView(binding.root)

        App.getInstance().postOpenAds.observe(this) {
            if (it) {
                App.getInstance().postOpenAds.postValue(false)
                requestAndShowInterHaveNativeFull(
                    this,
                    inter_resume_status,
                    inter_resume_id,
                    true,
                    showLoading = true,
                    actionDone = {
                        eventAfferNativeFullDismiss()
                    },
                    actionFail = {})
            }
        }

        networkStateMonitor = NetworkStateMonitor(this) {
            if (passedSetupAds) {
                passedSetupAds = false
                return@NetworkStateMonitor
            }
            passedNetworkStateMonitor = true
            onNetworkStateChanged(it)
        }
        networkStateMonitor.startMonitoring()

        if (!passedNetworkStateMonitor && isOnline()) {
            passedSetupAds = true
            setupAds()
        }
        if (!isOnline()) {
            passedSetupAds = false
            setupUiWhenDisconnect()
        }

        setupData()
        setupViews()
        setupObservers()
        setupEvent()

    }


    open fun setScreen() {}


    abstract fun setupData()

    abstract fun setupViews()

    open fun setupObservers() {}


    abstract fun setupEvent()

    open fun setupAds() {}
    open fun setupUiWhenDisconnect() {}

    open fun onActivityStarted() {}

    protected open fun onNetworkStateChanged(isConnected: Boolean) {
//        setupAds()
    }

    protected open fun eventAfferNativeFullDismiss() {}


    private fun initViewBinding() {
        _binding = getViewBinding()
        hideKeyword(binding.root, this)
    }

    protected abstract fun getViewBinding(): VB

    override fun onDestroy() {
        try {
            networkStateMonitor.stopMonitoring()
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
        super.onDestroy()
    }


    fun findFragment(TAG: String): Fragment? {
        return supportFragmentManager.findFragmentByTag(TAG)
    }

    fun addFragment(
        containerViewId: Int,
        destinationFragment: Fragment,
        addToBackStack: Boolean = false,
        TAG: String? = null,
        anim: Boolean = false,
    ) {
        supportFragmentManager
            .beginTransaction()
            .add(containerViewId, destinationFragment).apply {
                if (addToBackStack) {
                    addToBackStack(TAG)
                }
                if (anim) {
                    setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                }
            }.commit()
    }

    fun replaceFragment(
        containerViewId: Int,
        destinationFragment: Fragment,
        addToBackStack: Boolean = false,
        TAG: String? = null,
    ) {
        supportFragmentManager
            .beginTransaction()
            .replace(containerViewId, destinationFragment).apply {
                if (addToBackStack) {
                    addToBackStack(TAG)
                }
            }.commit()
    }

    fun fullScreen(boolean: Boolean) {
        if (boolean)
            window?.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        else
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

//    protected fun changeTextStatusBar() {
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//    }

//    protected fun changeStatusBarColor(color: Int) {
//        window?.let { window ->
//            // clear FLAG_TRANSLUCENT_STATUS flag:
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//
//            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//
//            // finally change the color
//            window.statusBarColor = color
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                window.insetsController?.setSystemBarsAppearance(
//                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
//                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
//                )
//
//            } else {
//                val windowInsetController =
//                    WindowCompat.getInsetsController(window, window.decorView)
//                windowInsetController.isAppearanceLightStatusBars = true
//            }
//        }
//    }

    private fun changeLightStatusBar() {
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
    }


    /** ẩn bottom navigation */
    open fun fullScreencall() {
        //for new api versions.
        val decorView: View = window.decorView
        val uiOptions: Int =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.systemUiVisibility = uiOptions
    }

    /** cần thêm <item name="android:windowFullscreen">true</item> trong theme*/
    private fun fullScreenActivity() {
        val window: Window = window
        val winParams: WindowManager.LayoutParams = window.attributes
        winParams.flags =
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        window.attributes = winParams
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    /** cần thêm <item name="android:windowFullscreen">true</item> trong theme */
    fun hideStartusBar() {
        val window: Window = window
        val winParams: WindowManager.LayoutParams = window.attributes
        winParams.flags =
            winParams.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.transparent)
        window.statusBarColor = Color.TRANSPARENT
        /** Hiện chữ đen trên thanh stt bar */
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

}