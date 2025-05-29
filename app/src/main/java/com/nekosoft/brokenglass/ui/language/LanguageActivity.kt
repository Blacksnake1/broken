package com.nekosoft.brokenglass.ui.language

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.brokenscreen.prankapp.crack.screen.BuildConfig
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.ActivityLanguageBinding
import com.bumptech.glide.Glide
import com.gianghv.DialogLoadingAds
import com.gianghv.utils.AdsConfigUtils
import com.gianghv.utils.Utils.isNetworkConnected
import com.nekosoft.brokenglass.base.BaseActivity
import com.nekosoft.brokenglass.extention.ContextExt.changeStatusBarColor
import com.nekosoft.brokenglass.extention.LoadNativeAds
import com.nekosoft.brokenglass.extention.goneExt
import com.nekosoft.brokenglass.extention.requestAndShowNative
import com.nekosoft.brokenglass.extention.visibleExt
import com.nekosoft.brokenglass.ui.home.HomeActivity
import com.nekosoft.brokenglass.ui.intro.IntroActivity
import com.nekosoft.brokenglass.ui.language.LanguageUtils.saved
import com.nekosoft.brokenglass.utils.clickSafe
import com.nekosoft.brokenglass.utils.logEventFirebase
import com.nekosoft.brokenglass.utils.setGradientTextColor


class LanguageActivity : BaseActivity<ActivityLanguageBinding>() {
    private var countryCode: String? = null
    private var countryName: String? = null
    private val fromWhere by lazy {
        intent.getStringExtra("fromWhere")
    }
    private var languageAdapter: LanguageAdapter? = null
    private var loadNativeAds: LoadNativeAds? = null
    private var clickLanguage = false

    override fun setScreen() {
        fullScreencall()
        changeStatusBarColor(R.color.black)
    }
    init {
        onBackPressedAction = {
            if (fromWhere == "fromSetting") {
                finish()
            }
        }
    }

    override fun setupData() {
        countryCode = countryCodeSelected
    }

    override fun setupAds() {
        loadAds()
    }

    override fun onNetworkStateChanged(isConnected: Boolean) {
        super.onNetworkStateChanged(isConnected)
        loadAds()
    }

    private fun loadAds() {
        requestAndShowNative(
            AdsConfigUtils.native_language_status,
            BuildConfig.native_language_id,
            admobView = binding.nativeAdMedium,
            maxView = binding.maxNativeAds,
            actionDone = {},
            actionFail = {}
        )
    }

    override fun setupViews() {
        setUiItemSelectd(languageModelList[itemIndex])
        if (!isNetworkConnected(this)) {
            binding.vgNative.goneExt()

        }
        setGradientTextColor(
            true,
            resources.getString(R.string.current_language),
            binding.tvCurrentLanguage,
            resources.getColor(R.color.yellow_fdb818, null),
            resources.getColor(R.color.yellow_ea7103, null)
        )
        setGradientTextColor(
            true,
            resources.getString(R.string.all_languages),
            binding.tvAllLanguage,
            resources.getColor(R.color.yellow_fdb818),
            resources.getColor(R.color.yellow_ea7103)
        )
        setListLanguage()
    }

    private fun setUiItemSelectd(currentLanguageModel: LanguageModel) {
        Glide.with(this).load(currentLanguageModel.flag).into(binding.ivFlag)
        binding.tvLanguage.text = currentLanguageModel.national
    }

    private fun setListLanguage() {
        languageModelList.removeAt(itemIndex)
        initAdapter()
    }

    private fun initAdapter() {
        languageAdapter = LanguageAdapter(this) {
            clickLanguage = true
            countryCode = it.nameCode
            countryName = it.national
            binding.cbSelected.isChecked = false
            binding.lnItem.setBackgroundResource(R.color.gray_35383f)
            binding.btnAccept.visibleExt()
        }
        languageAdapter?.setData(languageModelList)
        binding.rcvLanguage.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcvLanguage.hasFixedSize()
        binding.rcvLanguage.adapter = languageAdapter
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun setupEvent() {
        binding.btnAccept.clickSafe {
            if (!clickLanguage) {
                Toast.makeText(this, R.string.please_choose_language, Toast.LENGTH_SHORT).show()
                return@clickSafe
            }
            sharedPreferences?.setBoolean(saved, true)
            // set language for app
            sharedPreferences?.setString(LanguageUtils.LANGUAGE_CURRENT_CODE, countryCode)

            // intent activity
            if (fromWhere == "fromSetting") {
                intentMainActivity()
            } else {
                intentIntroActivity()
            }

            if (sharedPreferences?.getBoolean(saved, false) == false) {
                logEventFirebase(
                    this,
                    "click_save_language"
                )
            }
        }
        binding.lnItem.setOnClickListener {
            clickLanguage = true
            countryCode = countryCodeSelected
//            countryName = languageModelList[itemIndex].national
            binding.cbSelected.isChecked = true
            languageAdapter?.selectPosition = -1
            languageAdapter?.notifyDataSetChanged()
            binding.lnItem.setBackgroundResource(R.drawable.bg_stroke_solid_8)
            binding.btnAccept.visibleExt()
        }
    }


    private fun intentIntroActivity() {
        val intent = Intent(this, IntroActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out)
    }

    private fun intentMainActivity() {

        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("fromto", "fromLanguage")
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out)
    }

    override fun onPause() {
        super.onPause()
        loadNativeAds?.stopLoadAds("onpause_language")
    }

    override fun onResume() {
        super.onResume()
        loadNativeAds?.resumeLoadAds("onresume_language")
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    override fun getViewBinding(): ActivityLanguageBinding {
        return ActivityLanguageBinding.inflate(layoutInflater)
    }





}


