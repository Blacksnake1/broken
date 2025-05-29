package com.nekosoft.brokenglass.base


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding

import com.nekosoft.brokenglass.utils.hideKeyword

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    open val TAG = this::class.simpleName
    private var _binding: VB? = null
    open val binding get() = _binding!!
    private var mRootView: View? = null
    private var hasInitializedRootView = false
    private var progressDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {
        if (mRootView == null) {
            initViewBinding(inflater, container)
        }

        hideKeyword(binding.root, activity)
        initView(binding.root)
        // using when fragment transition animation 300ms
        binding.root.postDelayed(
            { initDataWithAnimation() }, 300
        )


        return mRootView
    }

    private fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?) {
        _binding = getViewBinding(inflater, container)
        mRootView = binding.root

    }

    abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    abstract fun setupData()

    abstract fun setupViews()

    open fun setupObservers() {}

    abstract fun setupEvent()

    open fun setupAds() {}


    override fun onResume() {
        super.onResume()
        registerListeners()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onPause() {
        unRegisterListeners()
        super.onPause()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use LifecycleObserver instead of override lifecycle methods such as onResume
        viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                onFragmentResume()
            }

            override fun onPause(owner: LifecycleOwner) {
                onFragmentPause()
            }
        })
        setupAds()
        if (!hasInitializedRootView) {
            getFragmentArguments()
            observeAPICall()
            setupData()
            setupViews()
            hasInitializedRootView = true
        }
        view.setOnTouchListener { _, _ -> true }
        setupObservers()
        setupEvent()
    }

    override fun onDestroyView() {
        if (view?.parent != null) {
            (view?.parent as? ViewGroup)?.endViewTransition(view)
        }
        super.onDestroyView()

    }

    open fun initView(view: View) {}

    open fun initDataWithAnimation() {}

    open fun registerListeners() {}

    open fun unRegisterListeners() {}

    open fun getFragmentArguments() {}

    open fun onFragmentResume() {}

    open fun onFragmentPause() {}

    open fun observeAPICall() {}


    protected open fun reloadData() {}

    open fun onLeft(view: View) {
        if (parentFragmentManager.backStackEntryCount > 0) {
            parentFragmentManager.popBackStack()
        } else {
            activity?.onBackPressed()
        }
    }


    protected open fun getChildLayoutReplace(): Int {
        return 0
    }

    open fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun addFragment(
        container: Int,
        fraManager: FragmentManager,
        targetFragment: Fragment,
        targetName: String? = null,
        addToBackStack: Boolean = true,
        addEnterAnimation: Boolean = true,
    ) {
        fraManager.commit {
//            setCustomAnimations(
//                if (addEnterAnimation) R.anim.slide_in_2 else 0,
//                R.anim.fade_out_2,
//                R.anim.fade_in_2,
//                R.anim.slide_out_2
//            )
            add(container, targetFragment, targetName)
            if (addToBackStack) addToBackStack(targetName)
        }
    }

//    fun getDefNetwork(context: Context): Int {
//        return AdsConfigUtils(context).getDef(AdsConfigUtils.DEF_NETWORK)
//    }


    @SuppressLint("CommitTransaction")
    fun replaceFragment(
        container: Int,
        targetFragment: Fragment,
        addToBackStack: Boolean = false,
        TAG: String?,
        transit: Int = FragmentTransaction.TRANSIT_UNSET,
    ) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(container, targetFragment, TAG)
            ?.apply {
                if (addToBackStack) {
                    addToBackStack(null)
                }
            }
            ?.commit()

//        activity?.supportFragmentManager?.beginTransaction()
//            ?.replace(container, targetFragment, TAG)
//            ?.apply {
//                commitTransaction(this, addToBackStack, transit)
//            }
    }

    fun replaceChildFragment(
        parentFragment: Fragment = this, containerViewId: Int,
        fragment: Fragment, TAG: String?, addToBackStack: Boolean = false, transit: Int = -1,
    ) {
        val transaction = parentFragment.childFragmentManager.beginTransaction().replace(
            containerViewId, fragment, TAG
        )
        commitTransaction(transaction, addToBackStack, transit)
    }

    fun addChildFragment(
        parentFragment: Fragment = this, containerViewId: Int,
        fragment: Fragment, TAG: String?, addToBackStack: Boolean = false, transit: Int = -1,
    ) {
        val transaction = parentFragment.childFragmentManager.beginTransaction().add(
            containerViewId, fragment, TAG
        )
        commitTransaction(transaction, addToBackStack, transit)
    }

    private fun commitTransaction(
        transaction: FragmentTransaction,
        addToBackStack: Boolean = false,
        transit: Int = -1,
    ) {
        if (addToBackStack) transaction.addToBackStack(null)
        if (transit != -1) transaction.setTransition(transit)
        transaction.commit()
    }

    fun popBackChildFragment(parentFragment: Fragment = this) {
        parentFragment.childFragmentManager.popBackStack()
    }

    /** loại bỏ fragment hiện tại ra khỏi ngăn xếp*/
    fun popBackFragment(fragment: Fragment = this) {
        activity?.supportFragmentManager?.popBackStack()
    }


}