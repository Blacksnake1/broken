package com.nekosoft.brokenglass.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "ScreenModel")
data class HomeModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    var name: String?,
    var drawble: Int?,
) : Parcelable {
    var isloadSuccess = false
}