package com.nekosoft.brokenglass.ui.wallpaper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.brokenscreen.prankapp.crack.screen.BuildConfig
import com.brokenscreen.prankapp.crack.screen.BuildConfig.native_history_id
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.FragmentWallpaperBinding
import com.gianghv.DialogLoadingAds
import com.gianghv.utils.AdsConfigUtils
import com.gianghv.utils.AdsConfigUtils.Companion.native_wallpaper_status
import com.gianghv.utils.ConstantsAds
import com.gianghv.utils.Utils
import com.nekosoft.brokenglass.App
import com.nekosoft.brokenglass.base.BaseActivity
import com.nekosoft.brokenglass.customView.dialog.DialogDowloading
import com.nekosoft.brokenglass.customView.dialog.DialogReadyToUse
import com.nekosoft.brokenglass.customView.dialog.DialogRequestInternet
import com.nekosoft.brokenglass.customView.dialog.DialogRequestReward
import com.nekosoft.brokenglass.data.model.NativeAdsModel
import com.nekosoft.brokenglass.data.model.ScreenModel
import com.nekosoft.brokenglass.extention.ContextExt.isOnline
import com.nekosoft.brokenglass.extention.goneExt
import com.nekosoft.brokenglass.extention.requestAndShowInterHaveNativeFull
import com.nekosoft.brokenglass.extention.requestAndShowNative
import com.nekosoft.brokenglass.ui.WallpaperDetailActivity
import com.nekosoft.brokenglass.utils.ConstantsApp
import com.nekosoft.brokenglass.utils.GetLocalUtils
import com.nekosoft.brokenglass.utils.GetLocalUtils.checkFileExists
import com.nekosoft.brokenglass.utils.ListUtils
import com.nekosoft.brokenglass.utils.getCurrentPosition
import com.nekosoft.brokenglass.utils.orZero
import com.nekosoft.brokenglass.utils.saveCurrentPosition
import dagger.hilt.android.AndroidEntryPoint

const val WALLPAPER_REVIEW = "WALLPAPER_REVIEW"

@AndroidEntryPoint
class WallpaperActivity : BaseActivity<FragmentWallpaperBinding>() {
    private var wallpaperAdapter: WallpaperAdapter? = null
    private var listWallpaperOnServer = mutableListOf<ScreenModel>()
    private var listShow = mutableListOf<Any>()
    private val wallpaperViewModel: WallpaperViewmodel by viewModels()
    private var firstOpen: Boolean? = null
    private var dialogRequestReward: DialogRequestReward? = null
    private var dialogRequestInternet: DialogRequestInternet? = null
    private var dialogDowloading: DialogDowloading? = null
    private var screenModelSelected: ScreenModel? = null
    private var canGoReceive = false
    private var currentPosition = 0
    private var clickedBtnShowAds = true // chặn không cho click lien tục vào btnShowAds
    private var clickFromBtnWatchAds =
        false // biến thể hiện trạng thái đã click vào BtnWatchAds của dialogRequestReward hay chưa.
    private var currentScreen: ScreenModel? = null
    private var fromSetting = false
    private var clicked = false
    private var count = 0
    private var dialogReadyToUse: DialogReadyToUse? = null
    private var isRefreshing = false
    private var isRequestRewardSuccess =
        false // biến để thể hiện trạng thái là đã hoàn thành viec request reward lúc đầu rồi
    private var dialogLoadingAds: DialogLoadingAds? = null
    private var rewardWatched = false

    override fun setScreen() {
        fullScreencall()
    }

    override fun setupData() {
        firstOpen = sharedPreferences?.getBoolean(ConstantsApp.OPEN_WALLPAPER, true)
        wallpaperViewModel.getWallpaperOnServer(this)
    }

    override fun setupViews() {
        dialogLoadingAds = DialogLoadingAds(this)
        setDialogRequestInternet()
        setDialogRequestReward()
        setupDialogDownloading()
        setupAdapter()

        binding.splLayout.isEnabled = isOnline()
    }


    private fun setupDialogDownloading() {
        dialogDowloading = DialogDowloading.Builder()
            .setOnClickListener(object : DialogDowloading.Builder.OnClickDialog {
                override fun onDissmisListener() {}
            }).build(this)
    }

    private fun setupAdapter() {
        wallpaperAdapter = WallpaperAdapter(
            this,
            onClick = { it, pos ->
                if (!clicked) {
                    screenModelSelected = it
                    clicked = true
                    currentScreen = it
                    currentPosition = pos
                    if (it.isloadSuccess) {
                        if (it.isPremium == true) {
                            if (Utils.isNetworkConnected(this)) {
                                downloadWallpaper()
                                dialogRequestReward?.screenModel = it
                                dialogRequestReward?.showAdwardDialog()
                            } else {
                                dialogRequestInternet?.show()
                            }

                        } else {
                            goToWallpaperDetail(it)
                        }
                    } else {
                        clicked = false
                    }
                }
            }, isShowing = {
                binding.loading.goneExt()
            })
        with(binding.rcvWallpaper) {
            adapter = wallpaperAdapter
            layoutManager = GridLayoutManager(this@WallpaperActivity, 2)
            setHasFixedSize(true)
        }
    }

    //    var nativeAds: NativeAd? = null
    var itemNativeAds: NativeAdsModel? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun setupAds() {
//        requestAndShowBanner(
//            banner_splash_mrec_status,
//            BuildConfig.native_full_id,
//            true,
//            binding.vgNativeAds,
//            actionDone = {},
//            actionFail = {})

        requestAndShowNative(
            native_wallpaper_status,
            native_history_id,
            admobView = binding.adMobNativeAds,
            maxView = binding.maxNativeAds,
            actionDone = { it ->
//                itemNativeAds = NativeAdsModel(it)
//                if (!listShow.isNullOrEmpty()) {
//                    listShow.removeAll { it is NativeAdsModel }
//                    if (listShow.size > 1) listShow.add(1, itemNativeAds!!)
//                    if (listShow.size > 5) listShow.add(5, itemNativeAds!!)
//                }
//                wallpaperAdapter?.setData(listShow)
//                wallpaperAdapter?.notifyDataSetChanged()
            },
            actionFail = {
//                itemNativeAds = NativeAdsModel(null)
            }
        )

    }

    override fun onNetworkStateChanged(isConnected: Boolean) {
        super.onNetworkStateChanged(isConnected)
        setupAds()
        Log.d("Tag123", "WallpaperActivity_onNetworkStateChanged_184:$isConnected")
        binding.splLayout.isEnabled = isConnected
    }

    override fun setupUiWhenDisconnect() {
        eventWhenRewardAdsOnLoadFail()
    }


//    private fun eventWhenRewardOnLoader() {
//        isRequestRewardSuccess = true
//        if (clickFromBtnWatchAds) {
//            clickFromBtnWatchAds = false
//            showAdmobRewardAds()
//        }
//    }

    private fun eventWhenRewardAdsOnLoadFail() {
        isRequestRewardSuccess = true
        clickFromBtnWatchAds = false
        dialogLoadingAds?.dismissDialog()
        Toast.makeText(this, R.string.please_check_your_internet, Toast.LENGTH_SHORT).show()
    }


    private fun downloadWallpaper() {
        if (screenModelSelected?.url != null) {
            val dirPath = GetLocalUtils.createInternalDirPath(
                this,
                "Broken_Screen",
                getString(R.string.wallpaper)
            )
            val fileName = wallpaperViewModel.getFileNameFromUrl(screenModelSelected?.url!!)
            if (checkFileExists("$dirPath/$fileName")) {
                isDownloaded = true
                return
            }
            screenModelSelected?.let {
                wallpaperViewModel.downloadWallpaper(
                    this@WallpaperActivity,
                    it
                ) {
                    isDownloaded = false
                }
            }
        }

    }

    private fun rewardAdsClosed() {
        clicked = false
        dialogLoadingAds?.dismissDialog()
        ConstantsAds.isShowAdsFull = false
        App.getInstance().newTurnOpenWallpaper = false
        rewardWatched = true

        screenModelSelected?.isPremium = false
        currentScreen?.let { wallpaperViewModel.updateWallpaper(it) }
        if (isDownloaded) {
            wallpaperAdapter?.notifyItemChanged(currentPosition)
            goToWallpaperDetail(screenModelSelected)
        } else {
            if (!Utils.isNetworkConnected(this)) {
                Toast.makeText(this, R.string.please_check_your_internet, Toast.LENGTH_SHORT).show()
                wallpaperViewModel.cancelDownload()
            } else {
                if (!this.isFinishing && dialogLoadingAds?.isShowing() == false) {
                    dialogLoadingAds?.showDialog(false)
                }
            }
        }
    }

    private fun failRewardAds() {
        clicked = false
        App.getInstance().newTurnOpenWallpaper = false
        dialogLoadingAds?.dismissDialog()
        ConstantsAds.isShowAdsFull = false
    }


    private fun setDialogRequestInternet() {
        dialogRequestInternet = DialogRequestInternet.Builder()
            .setOnClickListener(object : DialogRequestInternet.Builder.OnClickDialog {
                override fun onSwitchWifi() {
                    fromSetting = true
                    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                    openWifiActivityResult.launch(intent)
                }

                override fun onCancel3g() {
                    try {
                        fromSetting = true
                        val intent = Intent()
                        intent.component = ComponentName(
                            "com.android.settings",
                            "com.android.settings.Settings\$DataUsageSummaryActivity"
                        )
                        open3GActivityResult.launch(intent)

                    } catch (e: Exception) {
                        val intent = Intent(Settings.ACTION_DATA_ROAMING_SETTINGS)
                        open3GActivityResult.launch(intent)
                    }
                }

                override fun onCancel() {
                    clicked = false
                }
            }).build(this)

        dialogRequestInternet?.setOnDismissListener {
            clicked = false
        }
    }


    private fun goToWallpaperDetail(model: ScreenModel?) {
        clicked = false
        rewardWatched = false
        saveCurrentPosition(this, currentPosition)
        val wallpaperDetailActivity = WallpaperDetailActivity()
        val intent = Intent(this, wallpaperDetailActivity::class.java)
        intent.putExtra(WALLPAPER_REVIEW, model)
        startActivity(intent)
//        openDetailActivityResult.launch(intent)
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun setupObservers() {
        //phương thức phản hồi khi có lấy được ảnh từ server
        wallpaperViewModel.listServer.observe(this) { listServer ->
            listShow.clear()
            if (listServer != null) {   // phương thức phản hồi khi lấy được ảnh từ server
                listWallpaperOnServer.clear()
                if (firstOpen == true) {
                    listShow.addAll(ListUtils.wallpaperList)
                    listShow.addAll(listServer)
                    if (itemNativeAds?.nativeAds != null) {
                        if (listShow.size > 1) listShow.add(1, itemNativeAds!!)
                        if (listShow.size > 5) listShow.add(5, itemNativeAds!!)
                    }

                    wallpaperAdapter?.setData(listShow)
                    firstOpen = false
                    sharedPreferences?.setBoolean(ConstantsApp.OPEN_WALLPAPER, firstOpen!!)
                    listWallpaperOnServer.addAll(listServer)
                    Log.d("Tag123", "WallpaperActivity_setupObservers_331: ")
//                    binding.splLayout.isRefreshing = false
                } else {
                    listWallpaperOnServer.addAll(listServer)
                    wallpaperViewModel.getWallpaperOnDevice()
                }
            } else {   // phương thức phản hồi khi không lấy được ảnh từ server
                if (firstOpen == true) {
                    listShow.addAll(ListUtils.wallpaperList)
                    if (itemNativeAds?.nativeAds != null) {
                        if (listShow.size > 1) listShow.add(1, itemNativeAds!!)
                    }
                    wallpaperAdapter?.setData(listShow)
                } else {
                    listWallpaperOnServer.clear()
                    wallpaperViewModel.getWallpaperOnDevice()
                }
            }
            Log.d(
                "Tag123",
                "WallpaperActivity_setupObservers_348: " + binding.splLayout.isRefreshing
            )
            binding.splLayout.apply {
                isEnabled = true
                isRefreshing = false
            }

        }


        // phương thức này sẽ kiểm tra
        // 1. nếu list trên mạng mà != null thì sẽ add lần lượt 4 ảnh trong drawble, còn những ảnh sau sẽ check nếu đã có trên thiết bị thì
        //    lấy ở list thiết bị, còn ảnh chưa có trên thiết bị  thì lấy ở trên server.
        // 2. nếu list trên server mà null thì chỉ add 4 ảnh drawble và ảnh trên máy
        wallpaperViewModel.listDb.observe(this) { listWallpaperOnDevice ->
            listShow.clear()
            if (listWallpaperOnServer.isNotEmpty()) {
                val mapB = listWallpaperOnDevice.associateBy { it.id }
                listShow.addAll(ListUtils.wallpaperList)
                listShow.addAll(
                    listWallpaperOnServer.map {
                        if (it.id in mapB) {
                            mapB[it.id]!!
                        } else {
                            it
                        }
                    }.toMutableList()
                )
            } else {
                listShow.addAll(ListUtils.wallpaperList)
                listShow.addAll(listWallpaperOnDevice)
            }
            if (itemNativeAds?.nativeAds != null) {
                if (listShow.size > 1) listShow.add(1, itemNativeAds!!)
                if (listShow.size > 5) listShow.add(5, itemNativeAds!!)
            }
            wallpaperAdapter?.setData(listShow)
            Log.d("Tag123", "WallpaperActivity_setupObservers_380: ")
            binding.splLayout.isRefreshing = false
            binding.rcvWallpaper.scrollToPosition(getCurrentPosition(this).orZero())
        }

        //sau khi update trạng thái premium cho ScreenModel ở trong database thành công, sẽ phản hồi thời gian thực vào phương thức này.
        wallpaperViewModel.updateScreen.observe(this) {
            wallpaperAdapter?.notifyItemChanged(currentPosition)
        }

        wallpaperViewModel.onDownloadDone.observe(this) {
            isDownloaded = true
            dialogLoadingAds?.dismissDialog()
            if (rewardWatched) goToWallpaperDetail(screenModelSelected)
        }

        wallpaperViewModel.onDownloadError.observe(this) {
            dialogLoadingAds?.dismissDialog()
        }
    }

    private var isDownloaded = false

    override fun setupEvent() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.splLayout.setOnRefreshListener {
            Log.d("Tag123", "WallpaperActivity_setupEvent_417: ")
            if (isOnline()) {
                binding.splLayout.isEnabled = false
                binding.rcvWallpaper.recycledViewPool.clear()
                wallpaperViewModel.getWallpaperOnServer(this)
            } else {
                binding.splLayout.isRefreshing = false
            }
        }
    }


    init {
        onBackPressedAction = {
            saveCurrentPosition(this, 0)
            finish()
        }
    }

    private fun setDialogRequestReward() {
        dialogRequestReward = DialogRequestReward.Builder()
            .setOnClickListener(object : DialogRequestReward.Builder.OnClickDialog {
                override fun onOK() {
                    clickFromBtnWatchAds = true
                    if (clickedBtnShowAds)
                        if (isOnline()) {
                            clickedBtnShowAds = false
                            requestAndShowInterHaveNativeFull(
                                this@WallpaperActivity,
                                AdsConfigUtils.inter_unlock_status,
                                BuildConfig.inter_unlock_id,
                                actionDone = {
                                    rewardAdsClosed()
                                },
                                actionFail = {
                                    failRewardAds()
                                }
                            )
                        } else {
                            clicked = false
                            clickedBtnShowAds = false
                            dialogRequestInternet?.show()
                        }
                }

                override fun onCancel() {
                    clicked = false
                }
            }).build(this)

        dialogRequestReward?.setOnDismissListener {
            clickedBtnShowAds = true
        }
    }


    private var intentFilter: IntentFilter? = null

    /** do khi bật wifi hay 3g thì cần sau 1 khoảng thời gian mạng mới dc kết nối.
     *
     * Do đó cần reiveiver để bắt được sự kiện vào lúc internet được kêt nối
     * Khi để trong view như thế này thì khi bắt đầu mở view receiver cũng sẽ bắt được sự kiện đồng thời luôn.
     * */
    private var reiceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Handler(Looper.getMainLooper()).postDelayed({
                if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
                    if (isOnline() && canGoReceive) {
                        dialogRequestInternet?.dismissRequestInternetDialog()
                        wallpaperViewModel.getWallpaperOnServer(this@WallpaperActivity)
                        canGoReceive = false
//                    showRewardAds()
                    }
                }
            }, 1000)

        }
    }

    private fun registerReceiver() {
        if (intentFilter == null) {
            intentFilter = IntentFilter()
            intentFilter?.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
            this.registerReceiver(reiceiver, intentFilter)
        }
    }

    /** dùng để cho phép recieve xử lý logic bên trong nó khi từ màn wifi setting quay về app*/
    private var openWifiActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            fromSetting = false
            canGoReceive = true
        }

    /** dùng để cho phép recieve xử lý logic bên trong nó khi từ màn wifi setting quay về app*/
    private var open3GActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            fromSetting = false
            canGoReceive = true
        }

    /** dùng để lắng nghe sự kiện được trả về từ activity Detail đã set wallpaper hay chưa */
    private var openDetailActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val actionAPerformed = data?.getBooleanExtra("action_a_performed", false) ?: false
                if (actionAPerformed) { // set wallpaper
                    App.getInstance().SET_WALLPAPER_SUCCESSFUL = true
                }
            }
        }


    override fun onStart() {
        super.onStart()
        App.getInstance().enableShowOpenAds = !fromSetting
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(reiceiver)
        dialogLoadingAds?.dismissDialog()
        dialogRequestInternet?.dismissRequestInternetDialog()
        dialogRequestReward?.dismissAdwardDialog()
    }

    override fun getViewBinding(): FragmentWallpaperBinding {
        return FragmentWallpaperBinding.inflate(layoutInflater)

    }

    override fun onResume() {
        super.onResume()
        clickedBtnShowAds = true
        registerReceiver()
    }


    companion object {
        const val ACTION_REQUEST_CODE = 25121989
    }
}