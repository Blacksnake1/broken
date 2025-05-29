package com.nekosoft.brokenglass.repository

import android.util.Log
import com.nekosoft.brokenglass.data.model.ScreenModel
import com.nekosoft.brokenglass.data.db.HistoryDao
import com.nekosoft.brokenglass.data.db.WallpaperDao
import com.nekosoft.brokenglass.network.ApiService
import com.nekosoft.brokenglass.network.RetrofitClient
import com.nekosoft.brokenglass.response.ResponseWallpaper
import retrofit2.Call
import javax.inject.Inject

class WallpaperRepositoryImp @Inject constructor(
    private var apiService: ApiService,
    private val wallpaperDao: WallpaperDao,
) : WallpaperRepository {


    override fun getWallpaperOnServer(): Call<ResponseWallpaper> {
        return apiService.getImage()
    }

    override fun updateScreen(screenModel: ScreenModel): Long {
        return wallpaperDao.insertReplace(screenModel)
    }

    override fun getWallpaperOnDevice(): MutableList<ScreenModel> {
        return wallpaperDao.getAllWallpaperDatabase()

    }

}
