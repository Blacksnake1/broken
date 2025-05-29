package com.nekosoft.brokenglass.customView.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.brokenscreen.prankapp.crack.screen.BuildConfig.native_full_id
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.DialogAdsFullScreenBinding
import com.gianghv.admob.NativeAdsManager
import com.gianghv.utils.AdsConfigUtils
import com.gianghv.utils.ConstantsAds
import com.gianghv.utils.ConstantsAds.isShowOpenAds

@RequiresApi(Build.VERSION_CODES.M)
class DialogNativeFull(
    private val context: Activity,
    private val action: (Any?) -> Unit,
) {

    companion object {
        private const val LOADING = 0
        private const val SUCCESS = 1
        private const val FAIL = 2
        private const val SHOWING = 3


        const val INTER_SHOW_FULL = 4
        const val INTER_FAIL = 5
        const val INTER_CLOSE = 6
        const val INTER_FAIL_NON_ACTION = 7
        const val INTER_LOADING = 7

        /**biến để kiểm tra xem có đang hiện nativefull không.
         *Nếu không hiện thì mới cho show dialogReadyToUse.
         * còn đang hiện thì sẽ cho hiện dialogReadyToUse sau khi tắt nativefull*/
        var nativeFullShowing = false
    }

    private var clicked = false

    private val binding by lazy {
        DialogAdsFullScreenBinding.inflate(LayoutInflater.from(context))
    }

    private val dialog: Dialog by lazy {
        Dialog(context)
    }

    private var statusNative = -1
    private var statusInter = -1
    private var item: Any? = null

    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        val window = dialog.window
        val wlp = window?.attributes
        wlp?.gravity = Gravity.CENTER
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.decorView?.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        window?.attributes = wlp
        dialog.window?.setBackgroundDrawable(ColorDrawable(context.getColor(R.color.transparent)))
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.setOnDismissListener {
            isShowOpenAds = false
            ConstantsAds.isShowAdsFull = false
        }
    }

    fun isShowing(): Boolean {
        return dialog.isShowing
    }

    fun hide() {
        if (dialog.isShowing) {
            dialog.dismiss()
            nativeFullShowing = false
        }
    }

    fun loadAdNative() {
        if (AdsConfigUtils(context).getStatusAdsNetworking(AdsConfigUtils.native_full_status) != AdsConfigUtils.ADMOB) {
            onLoadFail()
            return
        }

        statusNative = LOADING

        val nativeAdsManager = NativeAdsManager(context, native_full_id)

        binding.itemAdmob.showShimmer(true)
        nativeAdsManager.loadAds(onLoadSuccess = {
            if (!context.isFinishing) {
                binding.itemAdmob.showShimmer(false)
                binding.itemAdmob.setNativeAd(it)
                if (!clicked) {
                    onLoadSuccess()
                }
                clicked = false
            }
        }, onLoadFail = {
            if (!context.isFinishing) {
//                binding.itemAdmob.hideAdsAndShimmer()
                onLoadFail()
//                binding.itemAdmob.goneExt()
            }
        }, onAdsClicked = {
            clicked = true
        })
    }

    @SuppressLint("SetTextI18n")
    fun show() {
        if (!dialog.isShowing) {
            ConstantsAds.isShowAdsFull = true
            nativeFullShowing = true
            dialog.show()
        }
    }

    fun interPing(state: Int, item: Any?) {
        this.item = item
        statusInter = state
        when (statusInter) {
            INTER_SHOW_FULL -> {
                if (statusNative != FAIL) {
                    show()
                }
            }

            INTER_FAIL -> {
                if (statusNative == FAIL) {
                    hide()
                    action.invoke(item)
                } else if (statusNative == SUCCESS) {
                    binding.itemAdmob.initCollapseEvent(onCloseAdsAction = {
                        hide()
                        action.invoke(item)
                    })

                } else {
                    show()
                }
            }

            INTER_CLOSE -> {
                if (statusNative == LOADING) {
                    show()
                } else {
                    if (statusNative == FAIL) {
                        hide()
                        action.invoke(item)
                    } else if (statusNative == SUCCESS) {
                        show()
                        binding.itemAdmob.initCollapseEvent(onCloseAdsAction = {
                            hide()
                            action.invoke(item)
                        })
                    }
                }
            }
        }
    }

    private fun onLoadSuccess() {
        statusNative = SUCCESS
        if (statusInter == INTER_CLOSE || statusInter == INTER_FAIL) {
            binding.itemAdmob.initCollapseEvent(onCloseAdsAction = {
                hide()
                action.invoke(item)
            })
        }
    }

    private fun onLoadFail() {
        statusNative = FAIL
        hide()
        if (statusInter == INTER_CLOSE || statusInter == INTER_FAIL) {
            hide()
            action.invoke(item)
        }
    }
}