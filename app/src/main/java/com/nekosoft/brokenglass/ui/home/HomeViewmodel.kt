package com.nekosoft.brokenglass.ui.home

import android.app.Application
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.applovin.sdk.AppLovinSdkUtils.runOnUiThread
import com.brokenscreen.prankapp.crack.screen.R
import com.nekosoft.brokenglass.base.SingleLiveEvent
import com.nekosoft.brokenglass.data.local.AppPreference
import com.nekosoft.brokenglass.data.model.HistoryModel
import com.nekosoft.brokenglass.data.model.ScreenModel
import com.nekosoft.brokenglass.repository.HistoryRepository
import com.nekosoft.brokenglass.utils.ConstantsApp
import com.nekosoft.brokenglass.utils.DownloadUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewmodel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val sharedPreferences: AppPreference,
) : AndroidViewModel(Application()) {

    val listHistory = historyRepository.getListDatabase()
    val result = SingleLiveEvent<Long>()


    fun setWallpaperBitmap(
        context: Context,
        screenModel: ScreenModel?,
        bitmap: Bitmap?,
        placeSet: String,
    ) {
        viewModelScope.launch() {
            withContext(Dispatchers.IO) {
                val wallpaperManager = WallpaperManager.getInstance(context)
                when (placeSet) {
                    ConstantsApp.SET_DOUBLE_SCREEN -> {
                        try {
                            wallpaperManager?.setBitmap(
                                bitmap,
                                null,
                                false,
                                WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                            )
                            runOnUiThread {
                                Toast.makeText(
                                    context,
                                    R.string.set_wallpaper_succesfull,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            runOnUiThread {
                                Toast.makeText(
                                    context,
                                    R.string.set_wallpaper_error,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }

                            Log.e("Exception", "doWork_60:" + e.message)
                        }
                    }

                    ConstantsApp.SET_HOME_SCREEN -> {
                        try {
                            wallpaperManager?.setBitmap(
                                bitmap,
                                null,
                                false,
                                WallpaperManager.FLAG_SYSTEM
                            )
                            runOnUiThread {
                                Toast.makeText(
                                    context,
                                    R.string.set_wallpaper_succesfull,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            runOnUiThread {
                                Toast.makeText(
                                    context,
                                    R.string.set_wallpaper_error,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                Log.e("Exception", "doWork_75:" + e.message)
                            }
                        }
                    }

                    else -> {
                        try {
                            wallpaperManager?.setBitmap(
                                bitmap,
                                null,
                                false,
                                WallpaperManager.FLAG_LOCK
                            )
                            runOnUiThread {
                                Toast.makeText(
                                    context,
                                    R.string.set_wallpaper_succesfull,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            runOnUiThread {
                                Toast.makeText(
                                    context,
                                    R.string.set_wallpaper_error,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }

                            Log.e("Exception", "doWork_90:" + e.message)
                        }
                    }
                }

                if (screenModel != null) {
                    result.postValue(
                        historyRepository.addScreen(
                            HistoryModel(
                                screenModel.id!!,
                                screenModel.name,
                                screenModel.url,
                                screenModel.drawble,
                                false
                            )
                        )
                    )
                } else {
                    runOnUiThread {
                        Toast.makeText(context, "screenModel is null", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }


    fun deleteAllHistory() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                historyRepository.deleteAll()
            }
        }
    }

    private val _deleteSuccess = SingleLiveEvent<Boolean>()
    val deleteSuccess: SingleLiveEvent<Boolean> = _deleteSuccess
    fun deleteListSelectedHistory(list: MutableList<HistoryModel>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                historyRepository.deleteHistory(list)
                _deleteSuccess.postValue(true)
            }
        }
    }


}