package com.nekosoft.brokenglass.ui.selectWeapon

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.brokenscreen.prankapp.crack.screen.BuildConfig
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.ActivitySelectWeaponBinding
import com.gianghv.utils.AdsConfigUtils.Companion.native_select_weapon_status
import com.nekosoft.brokenglass.data.model.WeaponModel
import com.nekosoft.brokenglass.extention.LoadNativeAds
import com.nekosoft.brokenglass.extention.disableExt
import com.nekosoft.brokenglass.extention.enableExt
import com.nekosoft.brokenglass.utils.ListUtils
import com.nekosoft.brokenglass.utils.clickSafe
import com.nekosoft.brokenglass.utils.setOnTouchScale


class SelectWeaponDialog(
    var activity: Activity,
    var kindWeapon: KindWeapon
) {
    private var currentPos: Int? = null
    private var selectedAnyWeapon = false
    private var dialog: Dialog? = null
    private var binding: ActivitySelectWeaponBinding =
        ActivitySelectWeaponBinding.inflate(LayoutInflater.from(activity))
    var isShowing = false

    init {
        dialog = Dialog(activity)
//        val window = dialog?.window
//        val wlp = window!!.attributes
//        wlp.gravity = Gravity.CENTER
//        window.attributes = wlp
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.window!!.apply {
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT
            )
            setBackgroundDrawable(ColorDrawable(activity.getColor(R.color.transparent)))
            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
        dialog?.setContentView(binding.root)
        setupAdapter()
        setupEvent()
    }

    private var loadNativeAds: LoadNativeAds? = null

    fun setupData() {
        loadNativeAds = LoadNativeAds(activity,
            native_select_weapon_status,
            BuildConfig.native_function_id,
            binding.nativeAdMedium,
            binding.maxNativeAds,
            onLoadSuccess = {},
            onLoadFail = {}
        )

        binding.btnDestroy.disableExt(1f)
    }

    var viewPagerAdapter: SelectWeaponAdapter? = null
    private var posSelected: Int? = null
    private fun setupAdapter() {
        viewPagerAdapter = SelectWeaponAdapter(activity) { weaponModel, pos ->
            selectedAnyWeapon = true
            posSelected = pos
            kindWeapon.weapon(weaponModel)
            binding.btnDestroy.apply {
                enableExt()
                setBackgroundResource(R.drawable.bg_ra8_gra)
            }
        }

        binding.rcvWeapon.layoutManager =
            GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
        binding.rcvWeapon.hasFixedSize()
        binding.rcvWeapon.adapter = viewPagerAdapter
        viewPagerAdapter?.setData(ListUtils.weaponList)
    }


    fun setupEvent() {
        binding.btnBack.clickSafe {
            kindWeapon.backToHome()
        }
        binding.btnDestroy.setOnTouchScale {
            if (!selectedAnyWeapon) {
                Toast.makeText(
                    activity,
                    activity.getString(R.string.please_select_any_weapon), Toast.LENGTH_SHORT
                ).show()
                return@setOnTouchScale
            }
            resetRecycleView()
            kindWeapon.eventBtnDestroy()
            dismissDialog()
        }

        dialog?.setOnDismissListener {
            loadNativeAds?.stopLoadAds("dialogs_select_weapon")
        }

        dialog?.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                // Xử lý sự kiện khi nhấn nút Back
                kindWeapon.backToHome()
                true // Trả về true để chặn hành động mặc định
            } else {
                false // Trả về false để thực hiện hành động mặc định
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun resetRecycleView() {
        binding.rcvWeapon.scrollToPosition(0)
        viewPagerAdapter?.selectPosition = -1
        viewPagerAdapter?.notifyDataSetChanged()
        selectedAnyWeapon = false
        binding.btnDestroy.apply {
            disableExt(1f)
            setBackgroundResource(R.drawable.bg_ra8_gray)
        }
    }


    @SuppressLint("SetTextI18n")
    fun showDialog() {
        try {
            if (dialog != null && dialog?.isShowing == false) {
                dialog?.show()
                isShowing = true
                setupData()
                loadNativeAds?.showAds("wallpaperactivity")
            }
        } catch (e: Exception) {

        }
    }

    fun dismissDialog() {
        try {
            if (dialog != null && !activity.isFinishing) {
                isShowing = false
                dialog?.dismiss()
            }
        } catch (e: Exception) {

        }
    }

}

interface KindWeapon {
    fun weapon(weaponModel: WeaponModel)
    fun backToHome()
    fun eventBtnDestroy()
}


