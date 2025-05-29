package com.nekosoft.brokenglass.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "HistoryModel")
data class HistoryModel(
    @PrimaryKey
    var id: Int,
    var name: String?,
    var url: String?,
    var drawble: Int?,
    var isCheck: Boolean?,
) : Parcelable {
}