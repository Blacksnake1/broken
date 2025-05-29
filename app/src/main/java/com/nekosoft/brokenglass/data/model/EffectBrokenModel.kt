package com.nekosoft.brokenglass.data.model

import android.os.Parcelable
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class EffectBrokenModel(
    @PrimaryKey(autoGenerate = true)
    var image: Int?,
    var name: String?,
) : Parcelable {
}