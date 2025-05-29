package com.nekosoft.brokenglass.repository

import com.nekosoft.brokenglass.data.model.HistoryModel
import com.nekosoft.brokenglass.data.db.HistoryDao
import javax.inject.Inject

class HistoryRepositoryImp @Inject constructor(
    private val historyDao: HistoryDao
) : HistoryRepository {
    override fun getListDatabase() = historyDao.getAllWallpaperDatabase()

    override fun addScreen(screen: HistoryModel): Long {
        return historyDao.insertIgnore(screen)
    }
//
    override fun deleteScreen(name: String) {
        historyDao.deleteScreen(name)
    }

    override fun deleteAll() {
        historyDao.deleteAll()
    }

    override fun deleteHistory(list: MutableList<HistoryModel>) {
        historyDao.deleteList(list)
    }

}