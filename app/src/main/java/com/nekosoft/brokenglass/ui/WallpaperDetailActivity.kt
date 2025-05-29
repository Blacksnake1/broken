package com.nekosoft.brokenglass.ui


import android.animation.Animator
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.brokenscreen.prankapp.crack.screen.BuildConfig
import com.brokenscreen.prankapp.crack.screen.BuildConfig.native_function_id
import com.brokenscreen.prankapp.crack.screen.databinding.FragmentWallpaperDetailBinding
import com.bumptech.glide.Glide
import com.gianghv.DialogLoadingAds
import com.gianghv.utils.AdsConfigUtils
import com.gianghv.utils.AdsConfigUtils.Companion.native_detail_wallpaper_status
import com.gianghv.utils.AdsConfigUtils.Companion.native_downloading_status
import com.nekosoft.brokenglass.App
import com.nekosoft.brokenglass.base.BaseActivity
import com.nekosoft.brokenglass.customView.dialog.DialogFakeDownloading3
import com.nekosoft.brokenglass.customView.dialog.DialogReadyToUse
import com.nekosoft.brokenglass.customView.dialog.DialogSetWallpaper
import com.nekosoft.brokenglass.customView.dialog.ProgressDialog
import com.nekosoft.brokenglass.data.model.ScreenModel
import com.nekosoft.brokenglass.extention.disableExt
import com.nekosoft.brokenglass.extention.enableViewsExt
import com.nekosoft.brokenglass.extention.goneExt
import com.nekosoft.brokenglass.extention.requestAndShowInterHaveNativeFull
import com.nekosoft.brokenglass.extention.requestAndShowNative
import com.nekosoft.brokenglass.extention.showHideGoneViewsExt
import com.nekosoft.brokenglass.extention.visibleExt
import com.nekosoft.brokenglass.ui.home.HomeViewmodel
import com.nekosoft.brokenglass.ui.wallpaper.WALLPAPER_REVIEW
import com.nekosoft.brokenglass.utils.ConstantsApp.SET_DOUBLE_SCREEN
import com.nekosoft.brokenglass.utils.ConstantsApp.SET_HOME_SCREEN
import com.nekosoft.brokenglass.utils.ConstantsApp.SET_LOCK_SRCEEN
import com.nekosoft.brokenglass.utils.DownloadUtils
import com.nekosoft.brokenglass.utils.clickSafe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WallpaperDetailActivity : BaseActivity<FragmentWallpaperDetailBinding>() {
    private var key: String? = null
    private var clicked = false
    private val viewModel: HomeViewmodel by viewModels()
    private var wallpaperBroken: ScreenModel? = null
    private var dialogSetWallpaper: DialogSetWallpaper? = null
    private var progressDialog: ProgressDialog? = null
    private var dialogLoadingInterAds: DialogLoadingAds? = null
    private var isSetWallpaper = false
    private var dialogReadyToUse: DialogReadyToUse? = null
    private var bitmap: Bitmap? = null
    private var isPreviewMode = false
    private var isLoading = true
    private var dialogFakeDownloading: DialogFakeDownloading3? = null

    init {
        onBackPressedAction = {
            if (isPreviewMode) {
                isPreviewMode = false
                binding.apply {
                    showHideGoneViewsExt(
                        viewVisible = arrayOf(
                            btnApply,
                            btnPreview
                        )
                    )
                }
            } else {
                if (!isLoading) {
//                    val resultIntent = Intent()
//                    resultIntent.putExtra("action_a_performed", isSetWallpaper)
//                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }

            }

        }
    }

    override fun setScreen() {
        fullScreencall()
    }

    override fun setupAds() {
        dialogLoadingInterAds = DialogLoadingAds(this)

//        requestAndShowNative(
//            native_downloading_status,
//            BuildConfig.native_download_2_id,
//            admobView = binding.nativeAdMediumBottom,
//            maxView = binding.maxNativeAdsBottom,
//            actionDone = {
//                Handler(Looper.getMainLooper()).postDelayed({
//                    binding.nativeAdMediumBottom.goneExt()
//                }, 5000)
//            },
//            actionFail = {}
//        )
//        binding.nativeAdMediumMiddle.initCollapseEvent {
//            binding.vgNativeAdsMiddle.goneExt()
//        }

        requestAndShowNative(
            native_detail_wallpaper_status,
            BuildConfig.native_function_id,
            admobView = binding.nativeAdMediumBottom,
            maxView = binding.maxNativeAdsBottom,
            actionDone = {},
            actionFail = {}
        )
    }

    override fun setupData() {
        wallpaperBroken = intent?.getParcelableExtra(WALLPAPER_REVIEW)
    }

    override fun setupViews() {
        if (wallpaperBroken?.name == "wp1" || wallpaperBroken?.name == "wp2") {
            Glide.with(this).asBitmap().load(wallpaperBroken?.drawble)
                .into(binding.imgDetail)
        } else {
            Glide.with(this).asBitmap().load(wallpaperBroken?.url)
                .into(binding.imgDetail)
        }
//        setFakeDownloading()
        setupDialogReadyToUse()
        setupDialogSetWallpaper()
        setupDialogFakeDownloading()
        progressDialog = ProgressDialog(this)
    }

    private fun setupDialogFakeDownloading() {
        dialogFakeDownloading =
            DialogFakeDownloading3(
                this,
                native_downloading_status,
                native_function_id
            ) {
                isLoading = false
                binding.imgDetail.visibleExt()
            }.apply { showDialog() }
    }

    private fun setupDialogSetWallpaper() {
        dialogSetWallpaper = DialogSetWallpaper(
            this,
            setBothScreen = {
                clicked = true
                key = SET_DOUBLE_SCREEN
                setWallpaper(key!!)
            }, setHomeScreen = {
                clicked = true
                key = SET_HOME_SCREEN
                setWallpaper(key!!)
            }, setLockScreen = {
                clicked = true
                key = SET_LOCK_SRCEEN
                setWallpaper(key!!)
            }, onDismissListener = null
        )
    }

    private fun setupDialogReadyToUse() {
        dialogReadyToUse = DialogReadyToUse(this, "wallpaper") {
            eventDismissDialogReadyUse()
        }
    }


    private fun eventDismissDialogReadyUse() {
        binding.imgDetail.visibleExt()
    }

//    private fun setFakeDownloading() {
//        binding.loadingView.addAnimatorListener(object : Animator.AnimatorListener {
//            override fun onAnimationStart(animation: Animator) {
//                binding.btnApply.disableExt(1f)
//                binding.btnBack.disableExt(0.3f)
//            }
//
//            override fun onAnimationEnd(animation: Animator) {
//                showHideGoneViewsExt(
//                    viewVisible = arrayOf(
//                        binding.vgNativeAdsMiddle,
//                        binding.imgDetail,
//                        binding.btnPreview
//                    ),
//                    viewGone = arrayOf(
//                        binding.loadingView,
//                        binding.nativeAdMediumBottom
//                    )
//                )
//                enableViewsExt(binding.btnApply, binding.btnBack)
//                isLoading = false
//            }
//
//            override fun onAnimationCancel(animation: Animator) {}
//
//            override fun onAnimationRepeat(animation: Animator) {}
//        })
//    }

    // phương thức xử lý khi user chọn  vị trí set wallpaper
    private fun setWallpaper(placeSet: String) {
        requestAndShowInterHaveNativeFull(
            this,
            AdsConfigUtils.inter_wallpaper_status,
            BuildConfig.inter_function_id,
            actionDone = {
                progressDialog?.showDialogLoading()
                viewModel.setWallpaperBitmap(this, wallpaperBroken, bitmap, placeSet)
                App.getInstance().SET_WALLPAPER_SUCCESSFUL = true
                dialogSetWallpaper?.dismiss()
            }
        )
    }

    override fun setupObservers() {
        viewModel.result.observe(this) {
            sharedPreferences?.setBoolean("fromHome", false)
            progressDialog?.dismissDialog()
            isSetWallpaper = true
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun setupEvent() {
        binding.btnApply.clickSafe {
            lifecycleScope.launch() {
                if (wallpaperBroken?.name == "wp1" || wallpaperBroken?.name == "wp2") {
                    bitmap = DownloadUtils.getBitmapFromDrawble(
                        context = this@WallpaperDetailActivity,
                        wallpaperBroken?.drawble,
                    )
                } else {
                    bitmap = DownloadUtils.getBitmapDirectly(
                        context = this@WallpaperDetailActivity,
                        wallpaperBroken?.url,
                    )
                }
            }
            dialogSetWallpaper?.show(supportFragmentManager, "")
        }

        binding.btnBack.clickSafe {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.apply {
            btnPreview.clickSafe {
                isPreviewMode = true
                showHideGoneViewsExt(
                    viewGone = arrayOf(
                        btnApply,
                        vgNativeAds,
                        btnPreview
                    )
                )
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        progressDialog?.dismissDialog()
    }


    override fun getViewBinding(): FragmentWallpaperDetailBinding {
        return FragmentWallpaperDetailBinding.inflate(layoutInflater)
    }
}