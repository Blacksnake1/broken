package com.nekosoft.brokenglass.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize

data class ResponseWallpaper(
    @SerializedName("picture") var listUrl: ArrayList<String> = arrayListOf()
) : Parcelable