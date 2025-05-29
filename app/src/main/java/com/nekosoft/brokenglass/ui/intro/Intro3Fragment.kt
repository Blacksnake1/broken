package com.nekosoft.brokenglass.ui.intro

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.ItemIntro3Binding
import com.bumptech.glide.Glide
import com.google.android.gms.ads.nativead.NativeAd
import com.nekosoft.brokenglass.App
import com.nekosoft.brokenglass.base.BaseFragment
import com.nekosoft.brokenglass.extention.disableExt
import com.nekosoft.brokenglass.extention.enableExt
import com.nekosoft.brokenglass.extention.invisibleExt
import com.nekosoft.brokenglass.extention.visibleExt
import com.nekosoft.brokenglass.utils.clickSafe


class Intro3Fragment : BaseFragment<ItemIntro3Binding>() {
    private var ads = false
    private lateinit var listener: (Int) -> Unit
    private var stopped = false
    private var clicked = false

    companion object {
        @JvmStatic
        fun newInstance(listener: (Int) -> Unit) =
            Intro3Fragment().apply {
                this.listener = listener
            }
    }

    override fun setupData() {}

    override fun setupViews() {
        textPos()
//        binding.nestedScrollView.post {
//            binding.nestedScrollView.fullScroll(View.FOCUS_DOWN)
//        }
//        Log.d("Tag123", "Intro3Fragment_setupViews_42: ")

    }

    private fun enableBtnStartNow() {
        if (ads) {
            binding.btnStartNow.enableExt()
            binding.btnStartNow.setBackgroundResource(R.drawable.bg_ra8_gra)
        } else {
            binding.btnStartNow.disableExt(0.3f)
            binding.btnStartNow.setBackgroundResource(R.drawable.bg_ra16_gra)
        }
    }

    override fun setupObservers() {
        App.getInstance().nativeIntro3.observe(viewLifecycleOwner) {
            ads = true
            if (it is NativeAd) {
                showAdmobNative(it)
            } else if (it == 0) {
                showShimmer()
            } else {
                nativeNull()
            }
        }
    }


    private fun showAdmobNative(nativeAd: NativeAd) {
        binding.adMobNativeAds.visibleExt()
        binding.adMobNativeAds.showShimmer(false)
        binding.adMobNativeAds.setNativeAd(nativeAd)
    }

    private fun nativeNull() {
        binding.adMobNativeAds.invisibleExt()
    }

    private fun showShimmer() {
        ads = false
        binding.adMobNativeAds.showShimmer(true)
    }

    override fun setupEvent() {
        binding.btnStartNow.clickSafe {
            if (clicked) return@clickSafe
            clicked = true
            listener.invoke(3)
        }
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            binding.btnStartNow.visibleExt()
        }, 2000)
        clicked = false
    }

    private fun textPos() {
        Glide.with(requireContext()).load(R.drawable.intro_3).into(binding.imgView)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): ItemIntro3Binding {
        return ItemIntro3Binding.inflate(inflater, container, false)
    }
}