package com.nekosoft.brokenglass.ui.intro

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.FragmentNativeSlideBinding
import com.google.android.gms.ads.nativead.NativeAd
import com.nekosoft.brokenglass.App
import com.nekosoft.brokenglass.base.BaseFragment
import com.nekosoft.brokenglass.extention.ContextExt.isOnline
import com.nekosoft.brokenglass.extention.enableExt
import com.nekosoft.brokenglass.extention.goneExt
import com.nekosoft.brokenglass.extention.visibleExt
import com.nekosoft.brokenglass.utils.clickSafe

class Intro4Fragment : BaseFragment<FragmentNativeSlideBinding>() {
    private lateinit var listener: (Int) -> Unit

    //Companion object used to adapter call fragment and don't need to create object , just call the object
    companion object {
        private const val ARG_PARAM1 = "param1"

        @JvmStatic
        fun newInstance(listener: (Int) -> Unit) =
            Intro4Fragment().apply {
                this.listener = listener
            }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNativeSlideBinding {
        return FragmentNativeSlideBinding.inflate(inflater, container, false)
    }

    override fun setupData() {
        loadAdNative()
    }


    fun loadAdNative() {
        binding.nativeFull.showShimmer(true)
        App.getInstance().nativeIntro4.observe(viewLifecycleOwner) {
            if (it is NativeAd) {
                binding.nativeFull.showShimmer(false)
                binding.nativeFull.setNativeAd(it)
            }
        }
    }


    override fun setupViews() {}

    override fun setupEvent() {
    }

    override fun onResume() {
        super.onResume()
        binding.nativeFull.initNativeEvent { listener.invoke(1) }
    }
}