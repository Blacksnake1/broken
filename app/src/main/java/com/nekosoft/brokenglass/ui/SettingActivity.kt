package com.nekosoft.brokenglass.ui

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.brokenscreen.prankapp.crack.screen.BuildConfig
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.FragmentSettingBinding
import com.gianghv.DialogLoadingAds
import com.gianghv.utils.AdsConfigUtils.Companion.native_setting_status
import com.nekosoft.brokenglass.base.BaseActivity
import com.nekosoft.brokenglass.customView.dialog.DialogFeedback
import com.nekosoft.brokenglass.customView.dialog.DialogRateUtil
import com.nekosoft.brokenglass.extention.ContextExt.changeStatusBarColor
import com.nekosoft.brokenglass.extention.LoadNativeAds
import com.nekosoft.brokenglass.extention.requestAndShowNative
import com.nekosoft.brokenglass.ui.language.LanguageActivity
import com.nekosoft.brokenglass.utils.clickSafe


class SettingActivity : BaseActivity<FragmentSettingBinding>() {
    private var dialogFeedback: DialogFeedback? = null
    private var isClicked = false


    override fun setScreen() {
        fullScreencall()
        changeStatusBarColor(R.color.black)
    }

    override fun setupData() {}

    override fun setupViews() {
        dialogFeedback = DialogFeedback.Builder()
            .setOnClickListener(object : DialogFeedback.Builder.OnClickDialog {
                override fun onCancel() {
                }

                override fun onOK() {
                }

            }).build(this)

        dialogFeedback!!.setOnDismissListener(DialogInterface.OnDismissListener {
            isClicked = false
        })
    }

    override fun setupAds() {
        requestAndShowNative(
            native_setting_status,
            BuildConfig.native_function_id,
            admobView = binding.nativeAdMedium,
            maxView = binding.maxNativeAds
        )
    }

    @SuppressLint("IntentReset")
    override fun setupEvent() {
        binding.btnLanguage.clickSafe(1000) {
            if (isClicked) return@clickSafe
            isClicked = true
            val intent = Intent(this, LanguageActivity::class.java)
            intent.putExtra("fromWhere", "fromSetting")
            startActivity(intent)
        }

        binding.btnFeedback.clickSafe(1000) {
            if (isClicked) return@clickSafe
            isClicked = true
            dialogFeedback?.show()
        }

        binding.btnShare.clickSafe(1000) {
            if (isClicked) return@clickSafe
            isClicked = true
            val intentInvite = Intent(Intent.ACTION_SEND)
            intentInvite.type = "text/plain"
            val body =
                "https://play.google.com/store/apps/details?id=com.crack.screen.broken.screen.prank"
            val subject = "Broken Glass"
            intentInvite.putExtra(Intent.EXTRA_SUBJECT, subject)
            intentInvite.putExtra(Intent.EXTRA_TEXT, body)
            startActivity(Intent.createChooser(intentInvite, "Share using"))
        }

        binding.btnPolicy.clickSafe(1000) {
            if (isClicked) return@clickSafe
            isClicked = true
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://sites.google.com/view/nksoftpolicy/home")
            )
            startActivity(browserIntent)

        }
        binding.btnRate.clickSafe(1000) {
            if (isClicked) return@clickSafe
            isClicked = true
            DialogRateUtil().showDialogRate(this) {
                isClicked = false
            }
        }

        binding.btnBack.clickSafe(1000) {
            if (isClicked) return@clickSafe
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun getViewBinding(): FragmentSettingBinding {
        return FragmentSettingBinding.inflate(layoutInflater)
    }

    init {
        onBackPressedAction = {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        isClicked = false
    }
}



