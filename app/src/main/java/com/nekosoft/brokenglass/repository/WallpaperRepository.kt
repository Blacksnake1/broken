package com.nekosoft.brokenglass.repository

import com.nekosoft.brokenglass.data.model.ScreenModel
import com.nekosoft.brokenglass.response.ResponseWallpaper
import retrofit2.Call

interface WallpaperRepository {
    fun getWallpaperOnServer(): Call<ResponseWallpaper>

    fun updateScreen(screenModel: ScreenModel):Long

    fun getWallpaperOnDevice(): MutableList<ScreenModel>


}

