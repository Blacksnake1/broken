package com.nekosoft.brokenglass.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import com.nekosoft.brokenglass.data.model.HistoryModel
import com.nekosoft.brokenglass.data.model.ScreenModel

@Dao
interface WallpaperDao : BaseDao<ScreenModel> {

    @Query("SELECT * FROM ScreenModel")
    fun getAllWallpaperDatabase(): MutableList<ScreenModel>

}