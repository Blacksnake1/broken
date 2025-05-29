package com.nekosoft.brokenglass.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import com.nekosoft.brokenglass.data.model.HistoryModel

@Dao
interface HistoryDao : BaseDao<HistoryModel> {

    @Query("SELECT * FROM HistoryModel")
    fun getAllWallpaperDatabase(): LiveData<List<HistoryModel>>

    @Query("DELETE FROM HistoryModel WHERE name IN (:name)")
    fun deleteScreen(name: String)

    @Query("DELETE FROM HistoryModel")
    fun deleteAll()

    @Delete
    fun deleteList(list: List<HistoryModel>)
}