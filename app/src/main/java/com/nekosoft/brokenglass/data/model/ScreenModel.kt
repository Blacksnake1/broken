package com.nekosoft.brokenglass.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "ScreenModel")
data class ScreenModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    var name: String?,
    var url: String?,
    var drawble: Int?,
    var isPremium: Boolean?,
    var isSet: Boolean?
) : Parcelable {
    var isloadSuccess = false
}