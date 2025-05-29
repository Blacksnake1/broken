package com.nekosoft.brokenglass.ui.crankEffect

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.brokenscreen.prankapp.crack.screen.BuildConfig
import com.brokenscreen.prankapp.crack.screen.BuildConfig.native_function_id
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.FragmentCrankEffectBinding
import com.gianghv.utils.AdsConfigUtils
import com.gianghv.utils.AdsConfigUtils.Companion.native_crank_status
import com.nekosoft.brokenglass.base.BaseActivity
import com.nekosoft.brokenglass.customView.dialog.DialogHowToTurnOff
import com.nekosoft.brokenglass.data.model.CrankEffectItem
import com.nekosoft.brokenglass.data.model.EffectBrokenModel
import com.nekosoft.brokenglass.extention.ContextExt.changeStatusBarColor
import com.nekosoft.brokenglass.extention.goneExt
import com.nekosoft.brokenglass.extention.requestAndShowInterHaveNativeFull
import com.nekosoft.brokenglass.extention.requestAndShowNative
import com.nekosoft.brokenglass.service.OverlayService
import com.nekosoft.brokenglass.ui.BrokenScreenActivity
import com.nekosoft.brokenglass.utils.ListUtils
import com.nekosoft.brokenglass.utils.clickSafe
import com.nekosoft.brokenglass.utils.isServiceRunning
import dagger.hilt.android.AndroidEntryPoint


const val BROKEN_REVIEW = "BROKEN_REVIEW"

@AndroidEntryPoint
class CrankEffectActivity : BaseActivity<FragmentCrankEffectBinding>() {
    private lateinit var listCrankEffect: MutableList<EffectBrokenModel>
    private var adapterCrank: CrankEffectAdapter? = null

    //        private var dialogRequestAppearOnTopPermission: DialogRequestAppearOnTopPermission? = null
    private var dialogHowToTurnOff: DialogHowToTurnOff? = null
//    private var dialogLoadingAds: DialogLoadingAds? = null

    //    private var loadNativeAds: LoadNativeAds? = null
    private var adsData: Any? = null
    override fun setScreen() {
        fullScreencall()
        changeStatusBarColor(R.color.black)
    }

    init {
        onBackPressedAction = {
            finish()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setupAds() {
        super.setupAds()
//        dialogLoadingAds = DialogLoadingAds(this)
//        dialogLoadingAds?.showDialog(false)
        requestAndShowNative(
            native_crank_status,
            native_function_id,
            admobView = binding.nativeAmob,
            actionDone = {},
            actionFail = {}
        )

    }


    override fun onNetworkStateChanged(isConnected: Boolean) {}
    override fun setupData() {

//        if (DialogRateUtil().canShowRate(this, true)) {
//            DialogRateUtil().showDialogRateShort(this) {}
//        }

    }

    fun generateCalendarItems(): MutableList<CrankEffectItem> {
        val items = mutableListOf<CrankEffectItem>()
        items.addAll(ListUtils.crankEffectList)
        return items
    }


    var screen: EffectBrokenModel? = null
    override fun setupViews() {
        dialogHowToTurnOff = DialogHowToTurnOff.Builder()
            .setOnClickListener(object : DialogHowToTurnOff.Builder.OnClickDialog {
                override fun onOK() {
                }

            }).build(this)

        adapterCrank = CrankEffectAdapter(
            this,
            generateCalendarItems(),
            onClick = {
                screen = it
                if (isServiceRunning(this, OverlayService::class.java)) {
                    Toast.makeText(
                        this,
                        getString(R.string.please_turn_off_the_current_broken_screen),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@CrankEffectAdapter
                }
                val intent = Intent(this, BrokenScreenActivity::class.java)
                intent.putExtra(BROKEN_REVIEW, it)
                startActivity(intent)
            }
        )

        binding.rcvBroken.apply {
            adapter = adapterCrank
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(this@CrankEffectActivity, 2)
        }
    }


    override fun setupEvent() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.btnSwitch.clickSafe {
            if (binding.switchCompat.isChecked) {
                OverlayService.turnOffService(this)
                binding.switchCompat.isChecked = false
                binding.vgTurnOff.goneExt()
            }
        }
        binding.btnInfor.clickSafe {
            dialogHowToTurnOff?.show()
        }
    }


    init {
        onBackPressedAction = {
            requestAndShowInterHaveNativeFull(
                this,
                AdsConfigUtils.inter_crankeffect_back_status,
                BuildConfig.inter_function_id,
                null,
                actionDone = {
                    finish()
                }
            )
        }
    }

    override fun onPause() {
        super.onPause()
//        loadNativeAds?.stopLoadAds("onPause_broken")
    }

    override fun getViewBinding(): FragmentCrankEffectBinding {
        return FragmentCrankEffectBinding.inflate(layoutInflater)
    }

    override fun onResume() {
        super.onResume()
        if (isServiceRunning(this, OverlayService::class.java)) {
            OverlayService.turnOffService(this)
            binding.vgTurnOff.goneExt()
            binding.switchCompat.isChecked = false
        }

//        loadNativeAds?.resumeLoadAds("onresume_broken")
    }
}