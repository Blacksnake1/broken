package com.nekosoft.brokenglass.ui.recent

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.brokenscreen.prankapp.crack.screen.BuildConfig
import com.brokenscreen.prankapp.crack.screen.BuildConfig.native_history_id
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.FragmentRecentBinding
import com.gianghv.utils.AdsConfigUtils
import com.gianghv.utils.AdsConfigUtils.Companion.native_history_status
import com.gianghv.utils.ConstantsAds.isShowAdsFull
import com.google.android.gms.ads.nativead.NativeAd
import com.nekosoft.brokenglass.base.BaseActivity
import com.nekosoft.brokenglass.customView.dialog.DialogRequestDelete
import com.nekosoft.brokenglass.data.model.HistoryModel
import com.nekosoft.brokenglass.data.model.ScreenModel
import com.nekosoft.brokenglass.extention.ContextExt.changeStatusBarColor
import com.nekosoft.brokenglass.extention.LoadNativeAds
import com.nekosoft.brokenglass.extention.goneExt
import com.nekosoft.brokenglass.extention.requestAndShowInterHaveNativeFull
import com.nekosoft.brokenglass.extention.requestAndShowNative
import com.nekosoft.brokenglass.extention.visibleExt
import com.nekosoft.brokenglass.ui.WallpaperDetailActivity
import com.nekosoft.brokenglass.ui.home.HomeViewmodel
import com.nekosoft.brokenglass.ui.wallpaper.WALLPAPER_REVIEW
import com.nekosoft.brokenglass.utils.clickSafe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecentActivity : BaseActivity<FragmentRecentBinding>() {
    private val viewModel: HomeViewmodel by viewModels()
    private var adapter: RecentAdapter? = null
    private var listScreen = mutableListOf<HistoryModel>()
    private var listDelete = mutableListOf<HistoryModel>()
    private var dialogRequestDelete: DialogRequestDelete? = null
    private var loadNativeAds: LoadNativeAds? = null
    private var fromWhere: Boolean? = null

    override fun setScreen() {
        fullScreencall()
        changeStatusBarColor(R.color.black)
    }

    init {
        onBackPressedAction = {
            customOnBackPressed()
        }
    }

    override fun setupAds() {
        fromWhere = sharedPreferences?.getBoolean("fromHome", true)
        if (fromWhere == false) {
            binding.adsFull.goneExt()
        }else{
            binding.adsFull.visibleExt()
            isShowAdsFull = true
            binding.adsFull.showShimmer(true)
            binding.adsFull.initCollapseEvent(onCloseAdsAction = {
                binding.adsFull.goneExt()
                isShowAdsFull = false
            })
        }


        // 1. Click lịch sử hiện native full
        //2. Click back vật lý bị hiện inter ads đè lên,
        // => isShowAdsFull để chặn show inter trong case này

        requestAndShowNative(
            native_history_status,
            native_history_id,
            admobView = binding.nativeAdMedium,
            maxView = binding.maxNativeAds,
            actionDone = {
                if (it is NativeAd && fromWhere == true) {
                    binding.adsFull.showShimmer(false)
                    binding.adsFull.setNativeAd(it)
                }
            },
            actionFail = {
                isShowAdsFull = false
                binding.adsFull.goneExt()
            }
        )
    }


    override fun setupData() {

    }

    override fun setupViews() {
        dialogRequestDelete = DialogRequestDelete.Builder()
            .setOnClickListener(object : DialogRequestDelete.Builder.OnClickDialog {
                override fun onCancel() {

                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onOK() {
                    viewModel.deleteListSelectedHistory(listDelete)
                }

            }).build(this)
        setupAdapter()
    }

    private fun setupAdapter() {
        adapter = listScreen.let { it ->
            RecentAdapter(
                this, it,
                onClick = {

                    val wallpaperDetailActivity = WallpaperDetailActivity()
                    val intent = Intent(this, wallpaperDetailActivity::class.java)
                    intent.putExtra(
                        WALLPAPER_REVIEW,
                        ScreenModel(it.id, it.name, it.url, it.drawble, it.isCheck, false)
                    )
                    startActivity(intent)

//                    findNavControllerSafely()?.navigate(
//                        RecentFragmentDirections.actionRecentFragmentToWallpaperDetailFragment(
//                            ScreenModel(it.id,it.name,it.url,it.drawble,it.isCheck,false)
//                        )
//                    )

                }, onLongClick = { list1 ->
                    binding.vgDelete.visibleExt()
                    binding.btnCancel.visibleExt()
                    binding.btnBack.goneExt()
                    binding.tvRecent.goneExt()
                    uiVgDelete(list1)
                },
                onLongClickMode = { list ->
                    uiVgDelete(list)
                })
        }
        binding.rcvRecent.adapter = adapter
        binding.rcvRecent.layoutManager =
            GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        binding.rcvRecent.hasFixedSize()
    }

    private fun uiVgDelete(list: MutableList<HistoryModel>) {
        if (list.size == listScreen.size) {
            binding.btnCheckAll.setImageResource(R.drawable.ic_checked)
        } else {
            binding.btnCheckAll.setImageResource(R.drawable.ic_empty_circle)
        }
        listDelete.clear()
        listDelete.addAll(list)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setupEvent() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.btnCancel.clickSafe {
            eventCancel()
        }
        binding.btnTrash.clickSafe {
            if (!listDelete.isNullOrEmpty()) {
                dialogRequestDelete?.show()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.you_need_to_select_at_least_one_photo), Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.btnCheckAll.clickSafe {
            adapter?.deleteAll()
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun eventCancel() {
        adapter?.isLongClickMode = false
        adapter?.listDelete?.clear()
        listDelete.clear()
        listScreen.forEach {
            it.isCheck = false
        }
        binding.btnBack.visibleExt()
        binding.btnCancel.goneExt()
        binding.tvRecent.visibleExt()
        binding.vgDelete.goneExt()
        adapter?.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setupObservers() {
        viewModel.listHistory.observe(this) {
            if (it.isNullOrEmpty()) {
                binding.rcvRecent.goneExt()
                binding.vgEmpty.visibleExt()
                binding.vgDelete.goneExt()
                binding.btnBack.visibleExt()
                binding.tvRecent.visibleExt()
                return@observe
            }
            listScreen.clear()
            listScreen.addAll(it)
            adapter?.notifyDataSetChanged()
            eventCancel()
        }
        viewModel.deleteSuccess.observe(this) {
            Toast.makeText(
                this,
                getString(R.string.delete_wallpaper_succesfull),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun customOnBackPressed() {
        if (adapter?.isLongClickMode == false) {
            if (!isShowAdsFull) {
                requestAndShowInterHaveNativeFull(
                    this,
                    AdsConfigUtils.inter_history_back_status,
                    BuildConfig.inter_function_id,
                    null,
                    actionDone = {
                        finish()
                    }
                )
            }

        } else {
            eventCancel()
        }
    }


    override fun getViewBinding(): FragmentRecentBinding {
        return FragmentRecentBinding.inflate(layoutInflater)
    }
}

