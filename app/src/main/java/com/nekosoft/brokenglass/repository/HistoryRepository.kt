package com.nekosoft.brokenglass.repository

import androidx.lifecycle.LiveData
import com.nekosoft.brokenglass.data.model.HistoryModel

interface HistoryRepository {

    fun getListDatabase(): LiveData<List<HistoryModel>>

    fun addScreen(screenModel: HistoryModel): Long


    fun deleteScreen(name: String)

    fun deleteAll()

    fun deleteHistory(list: MutableList<HistoryModel>)

}

