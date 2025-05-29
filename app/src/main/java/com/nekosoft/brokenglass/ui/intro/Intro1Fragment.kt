package com.nekosoft.brokenglass.ui.intro

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brokenscreen.prankapp.crack.screen.BuildConfig.banner_intro_id
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.ItemIntro012Binding
import com.bumptech.glide.Glide
import com.gianghv.utils.AdsConfigUtils.Companion.banner_intro_status
import com.nekosoft.brokenglass.App
import com.nekosoft.brokenglass.base.BaseFragment
import com.nekosoft.brokenglass.extention.enableExt
import com.nekosoft.brokenglass.extention.goneExt
import com.nekosoft.brokenglass.extention.requestAndShowBanner
import com.nekosoft.brokenglass.extention.visibleExt
import com.nekosoft.brokenglass.utils.clickSafe


class Intro1Fragment : BaseFragment<ItemIntro012Binding>() {

    private lateinit var listener: (Int) -> Unit

    companion object {
        private const val ARG_PARAM1 = "param1"

        @JvmStatic
        fun newInstance(listener: (Int) -> Unit) =
            Intro1Fragment().apply {
                this.listener = listener
            }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): ItemIntro012Binding {
        return ItemIntro012Binding.inflate(inflater, container, false)
    }

    override fun setupData() {
    }


    override fun setupViews() {
//        if (Build.MANUFACTURER == "Google"){
//            val params = binding.clView.layoutParams as ViewGroup.MarginLayoutParams
//            params.topMargin = TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_DIP,
//                30f,
//                binding.root.resources.displayMetrics
//            ).toInt()
//            binding.clView.layoutParams = params
//        }

        textPos()
        binding.btnNext.visibleExt()
        binding.btnStartNow.goneExt()
        binding.nestedScrollView.post {
            binding.nestedScrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    override fun setupObservers() {
        App.getInstance().bannerIntro012.observe(viewLifecycleOwner) {
            requireActivity().requestAndShowBanner(
                banner_intro_status,
                banner_intro_id,
                false,
                binding.vgNative,
                actionDone = {},
                actionFail = {
                })
        }
    }

    override fun setupEvent() {
        binding.btnNext.clickSafe {
            listener.invoke(0)
        }
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            binding.btnNext.enableExt()
            binding.btnNext.setBackgroundResource(R.drawable.bg_ra8_gra)
        }, 1000)
    }


    private fun textPos() {
        Glide.with(requireContext()).load(R.drawable.intro_1).into(binding.imgView)
        binding.tvDes.text = getString(R.string.des_intro_2)
        binding.btnStartNow.goneExt()
    }
}