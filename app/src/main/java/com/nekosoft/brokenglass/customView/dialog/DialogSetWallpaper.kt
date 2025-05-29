package com.nekosoft.brokenglass.customView.dialog

import android.app.Activity
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.Gravity.BOTTOM
import androidx.fragment.app.DialogFragment
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.DialogSetWallpaperBinding
import com.gianghv.DialogLoadingAds

class DialogSetWallpaper(
    var activity: Activity,
    var setBothScreen: () -> Unit,
    var setHomeScreen: () -> Unit,
    var setLockScreen: () -> Unit,
    var onDismissListener: (() -> Unit)? = null,
) : DialogFragment() {
    private lateinit var binding: DialogSetWallpaperBinding
    private var firstClick = false
    private val dialogAds = DialogLoadingAds(activity)


    companion object {
        const val DOUBLESCREEN = "DOUBLESCREEN"
        const val HOMESCREEN = "HOMESCREEN"
        const val LOCKSCREEN = "LOCKSCREEN"

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogSetWallpaperBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setStyle(STYLE_NO_TITLE, R.style.Dialog);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadAds()
        setEvent()
    }

    private fun loadAds() {

    }


    override fun onResume() {
        super.onResume()
        val width = resources.getDimensionPixelSize(R.dimen.popup_width)
        val height = resources.getDimensionPixelSize(R.dimen.popup_width)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(width, height)
        dialog?.window?.setGravity(BOTTOM)
    }

    private fun setEvent() {
        binding.choiceLockLayoutCld.setOnClickListener {
            this.dismiss()
        }

        binding.ivCancelCld.setOnClickListener {
            this.dismiss()
        }


        binding.btnAcceptNow.setOnClickListener {
            if (!firstClick) {
                firstClick = true
                when {
                    cbLockAndHome -> {
                        setBothScreen.invoke()
                    }

                    cbLockScreen -> {
                        setLockScreen.invoke()
                    }

                    cbHomeScreen -> {
                        setHomeScreen.invoke()
                    }
                }
                this.dismiss()
            }
        }
        setCheckbox()
    }

    private var cbLockScreen = false
    private var cbHomeScreen = false
    private var cbLockAndHome = true
    private fun setCheckbox() {
        binding.clLockScreen.setOnClickListener {
            cbLockScreen = true
            cbHomeScreen = false
            cbLockAndHome = false
            binding.cbLockScreen.setImageDrawable(resources.getDrawable(R.drawable.ic_checked))
            binding.cbHomeScreen.setImageDrawable(resources.getDrawable(R.drawable.ic_uncheck))
            binding.cbLockAndHome.setImageDrawable(resources.getDrawable(R.drawable.ic_uncheck))
        }

        binding.clHomeScreen.setOnClickListener {
            cbLockScreen = false
            cbHomeScreen = true
            cbLockAndHome = false
            binding.cbLockScreen.setImageDrawable(resources.getDrawable(R.drawable.ic_uncheck))
            binding.cbHomeScreen.setImageDrawable(resources.getDrawable(R.drawable.ic_checked))
            binding.cbLockAndHome.setImageDrawable(resources.getDrawable(R.drawable.ic_uncheck))
        }
        binding.clDoubleScreen.setOnClickListener {
            cbLockScreen = false
            cbHomeScreen = false
            cbLockAndHome = true
            binding.cbLockScreen.setImageDrawable(resources.getDrawable(R.drawable.ic_uncheck))
            binding.cbHomeScreen.setImageDrawable(resources.getDrawable(R.drawable.ic_uncheck))
            binding.cbLockAndHome.setImageDrawable(resources.getDrawable(R.drawable.ic_checked))
        }
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.invoke()
        firstClick = false
    }

}