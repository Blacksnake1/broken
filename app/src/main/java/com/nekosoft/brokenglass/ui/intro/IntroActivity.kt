package com.nekosoft.brokenglass.ui.intro

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.viewpager2.widget.ViewPager2
import com.brokenscreen.prankapp.crack.screen.BuildConfig
import com.brokenscreen.prankapp.crack.screen.BuildConfig.inter_getstart_id
import com.brokenscreen.prankapp.crack.screen.BuildConfig.native_full_id
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.ActivityIntroBinding
import com.gianghv.DialogLoadingAds
import com.gianghv.utils.AdsConfigUtils.Companion.OFF
import com.gianghv.utils.AdsConfigUtils.Companion.inter_getstart_status
import com.gianghv.utils.AdsConfigUtils.Companion.native_intro_status
import com.gianghv.utils.Utils.isNetworkConnected
import com.nekosoft.brokenglass.App
import com.nekosoft.brokenglass.base.BaseActivity
import com.nekosoft.brokenglass.customView.dialog.DialogLoadingLanguage
import com.nekosoft.brokenglass.extention.ContextExt.changeStatusBarColor
import com.nekosoft.brokenglass.extention.ContextExt.getAdsNetworking
import com.nekosoft.brokenglass.extention.ContextExt.isOnline
import com.nekosoft.brokenglass.extention.LoadNativeAds
import com.nekosoft.brokenglass.extention.requestAndShowInterHaveNativeFull
import com.nekosoft.brokenglass.extention.requestAndShowNative
import com.nekosoft.brokenglass.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroActivity : BaseActivity<ActivityIntroBinding>() {
    private var name = ""
    private var currentPos: Int? = null
    private var dialogLoadingAds: DialogLoadingAds? = null
    private var loadNativeAds: LoadNativeAds? = null
    private var oldPosition = -1
    private var dialogLoadingLanguage: DialogLoadingLanguage? = null
    private var showFragment4 = true

    override fun setScreen() {
        changeStatusBarColor(R.color.black)
        fullScreencall()
    }

    override fun setupData() {
        dialogLoadingLanguage = DialogLoadingLanguage(this)
        dialogLoadingLanguage?.showDialogLoading()

    }

    override fun setupViews() {
        setupViewPager()
    }

    override fun setupEvent() {

    }

    private var handler: Handler? = null
    private var runable: Runnable? = null
    private fun handle() {
        handler = Handler(Looper.getMainLooper())
        runable = Runnable {
            if (handler != null) {
                runable?.let { handler?.removeCallbacks(it) }
                handler = null
                goToHomeFragment()
            }
        }
        handler?.postDelayed(runable!!, 15000)
    }

    override fun onNetworkStateChanged(isConnected: Boolean) {
        super.onNetworkStateChanged(isConnected)
        setupAds()
    }

    override fun setupUiWhenDisconnect() {
        if (!isNetworkConnected(this)) {
            return
        }
    }

    override fun setupAds() {}

    private fun setupViewPager() {
        showFragment4 = if (!isOnline() || getAdsNetworking(native_intro_status) == OFF) {
            false
        } else {
            true
        }

        val viewPagerAdapter = IntroAdapter(showFragment4, this) {
            name = it.toString()
            when (it) {
                0 -> {
                    binding.viewPager.currentItem = binding.viewPager.currentItem.plus(1)
                }

                3 -> {
                    requestAndShowInterHaveNativeFull(
                        this,
                        inter_getstart_status,
                        inter_getstart_id,
                        true,
                        null,
                        true,
                        actionDone = {
                            goToHomeFragment()
                        },
                        actionFail = {
                            goToHomeFragment()
                        }
                    )
                }

                else -> {
                    goToHomeFragment()
                }
            }
        }


        val vpCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPos = position
                if (oldPosition == -1 && currentPos == 0) {
                    App.getInstance().nativeIntro3.postValue(0)
                    requestNative(native_intro_status, BuildConfig.native_intro_id, event = {
                        App.getInstance().nativeIntro3.postValue(it)
                    })
                    App.getInstance().bannerIntro012.postValue(null)

                    Handler(Looper.getMainLooper()).postDelayed(
                        { dialogLoadingLanguage?.dismissDialog() },
                        1000
                    )
                }
//                else if (oldPosition == 2 && currentPos == 3 && showFragment4) {
//                    requestNative(native_intro_status, native_full_id, event = {
//                        App.getInstance().nativeIntro4.postValue(it)
//                    })
//                }

                oldPosition = position
            }
        }

        binding.viewPager.apply {
            adapter = viewPagerAdapter
            isSaveEnabled = false
            offscreenPageLimit = 1
            isUserInputEnabled = false
            registerOnPageChangeCallback(vpCallback as ViewPager2.OnPageChangeCallback)
        }
    }

    private fun requestNative(status: String, id: String, event: (Any?) -> Unit) {
        requestAndShowNative(
            status,
            id,
            actionDone = {
                event.invoke(it)
            },
            actionFail = {
                event.invoke(null)
            }
        )
    }

    private fun goToHomeFragment() {
        if (handler != null) {
            runable?.let { handler?.removeCallbacks(it) }
            handler = null
        }
        App.getInstance().enableShowOpenAds = true
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("fromto", "fromIntro")
        startActivity(intent)
        finish()
    }

    override fun getViewBinding(): ActivityIntroBinding {
        return ActivityIntroBinding.inflate(layoutInflater)
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
    }
}