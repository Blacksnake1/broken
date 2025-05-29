package com.nekosoft.brokenglass.ui.wallpaper

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.brokenscreen.prankapp.crack.screen.R
import com.gianghv.utils.Utils
import com.kdownloader.KDownloader
import com.nekosoft.brokenglass.base.SingleLiveEvent
import com.nekosoft.brokenglass.data.model.ScreenModel
import com.nekosoft.brokenglass.repository.WallpaperRepositoryImp
import com.nekosoft.brokenglass.response.ResponseWallpaper
import com.nekosoft.brokenglass.utils.GetLocalUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URI
import javax.inject.Inject

@HiltViewModel
class WallpaperViewmodel @Inject constructor(
    var wallpaperRepository: WallpaperRepositoryImp,
) : AndroidViewModel(Application()) {

    private var list = mutableListOf<ScreenModel>()
    private val _listServer = SingleLiveEvent<MutableList<ScreenModel>?>()
    val listServer: SingleLiveEvent<MutableList<ScreenModel>?> = _listServer


    fun getWallpaperOnServer(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (Utils.isNetworkConnected(context)) {
                    wallpaperRepository.getWallpaperOnServer()
                        .enqueue(object : Callback<ResponseWallpaper> {
                            override fun onResponse(
                                call: Call<ResponseWallpaper>,
                                response: Response<ResponseWallpaper>,
                            ) {
                                if (response.isSuccessful) {
                                    list.clear()
                                    response.body()?.listUrl?.forEachIndexed { index, url ->
                                        if (index > 1) {
                                            list.add(
                                                ScreenModel(
                                                    index + 1,
                                                    "wp${index + 1}",
                                                    url,
                                                    null,
                                                    true,
                                                    false
                                                )
                                            )
                                        }
                                    }
                                    _listServer.postValue(list)
                                }
                            }

                            override fun onFailure(call: Call<ResponseWallpaper>, t: Throwable) {
                                _listServer.postValue(null)
                            }
                        })
                } else {
                    _listServer.postValue(null)
                }

            }
        }
    }

    private val _listDb = SingleLiveEvent<MutableList<ScreenModel>>()
    val listDb: SingleLiveEvent<MutableList<ScreenModel>> = _listDb

    /** lây toàn bộ list wallpaper đã lưu trong database*/
    fun getWallpaperOnDevice() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _listDb.postValue(wallpaperRepository.getWallpaperOnDevice())
            }
        }
    }


    val onDownloadError = SingleLiveEvent<Unit?>()
    val onDownloadDone = SingleLiveEvent<Unit?>()
    var kDownloader: KDownloader? = null
    fun downloadWallpaper(context: Context, screenModel: ScreenModel, progress: (() -> Unit)) {
        val dirPath = GetLocalUtils.createInternalDirPath(
            context,
            "Broken_Screen",
            context.getString(R.string.wallpaper)
        )
        val fileName = getFileNameFromUrl(screenModel.url!!)
        kDownloader = KDownloader.create(context)
        val request = kDownloader!!
            .newRequestBuilder(screenModel.url!!, dirPath, fileName)
            .tag(screenModel.id.toString())
            .readTimeout(5000)
            .connectTimeout(10000)
            .build()
        kDownloader?.enqueue(request,
            onStart = {
            },
            onProgress = {
                progress.invoke()
            },
            onCompleted = {
                onDownloadDone.call()
            },
            onError = {
                onDownloadError.call()
                kDownloader?.cancelAll()
            },
            onPause = {
            }
        )
    }

    fun cancelDownload() {
        kDownloader?.cancelAll()
    }

    /** lấy file name từ chuỗi string url*/
    fun getFileNameFromUrl(url: String): String {
        val uri = URI(url)
        val path = uri.path
        val segments = path.split("/")
        return segments.last()
    }


//    /** lưu trạng thái premium choScreenModel */
//    fun updateWallpaper(screenModel: ScreenModel) {
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                wallpaperRepository.updateScreen(screenModel)
//            }
//        }
//    }

    private val _updateScreen = SingleLiveEvent<Long?>()
    val updateScreen: SingleLiveEvent<Long?> = _updateScreen
    fun updateWallpaper(screenModel: ScreenModel) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _updateScreen.postValue(wallpaperRepository.updateScreen(screenModel))
            }
        }
    }
}