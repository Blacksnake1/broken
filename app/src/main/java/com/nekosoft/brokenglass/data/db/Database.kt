package com.nekosoft.brokenglass.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nekosoft.brokenglass.data.model.HistoryModel
import com.nekosoft.brokenglass.data.model.ScreenModel


@Database(
    entities = [ScreenModel::class, HistoryModel::class],
    version = 1,
    exportSchema = true
)
abstract class Database : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun wallpaperDao(): WallpaperDao
}