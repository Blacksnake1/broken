package com.nekosoft.brokenglass.data.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeaponModel(
    var name: String,
    var image: Int,
    var shootEffect: MutableList<Int>,
    var sound: Int,
    var selected: Boolean = false
) : Parcelable {
    var isloadSuccess = false
}