package com.nekosoft.brokenglass.base

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.brokenscreen.prankapp.crack.screen.R

abstract class BaseDialogFragment<VB : ViewBinding> :
    DialogFragment() {

    private var _binding: VB? = null
    open val binding get() = _binding!!
    private var mRootView: View? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogFragment);
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
//        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
//            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
//        }

        if (mRootView == null) {
            initViewBinding(inflater, container)
        }
        initView(binding.root)

        return mRootView
    }

    fun softResize() {
//        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            dialog?.window?.setDecorFitsSystemWindows(false)
        } else {
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        }
    }

    private fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?) {
        _binding = getViewBinding(inflater, container)
        mRootView = binding.root

    }

    open fun initView(view: View) {}
    abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onResume() {
        super.onResume()
        val width = resources.getDimensionPixelSize(R.dimen.popup_width)
        val height = resources.getDimensionPixelSize(R.dimen.popup_width)
        dialog?.window?.setLayout(width, height)
        dialog?.window?.setGravity(Gravity.BOTTOM)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadAds()
        setupData()
        setupViews()
        setupObserve()
        setupEvent()
    }

    open fun setupEvent() {}
    open fun setupData() {}
    open fun setupViews() {}
    open fun setupObserve() {}
    open fun loadAds() {}


}