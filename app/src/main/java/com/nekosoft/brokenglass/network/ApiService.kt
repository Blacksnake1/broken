package com.nekosoft.brokenglass.network

import com.nekosoft.brokenglass.response.ResponseWallpaper
import retrofit2.Call
import retrofit2.http.GET


interface ApiService {
    @GET("brokenscreenapi.json")
    fun getImage( ): Call<ResponseWallpaper>
}

