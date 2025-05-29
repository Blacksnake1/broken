package com.nekosoft.brokenglass.data.db

import android.util.Log
import androidx.room.*

interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
     fun insertIgnore(any: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReplace(any: T): Long
    @Update
     fun update(any: T)

//    @Transaction
//     fun insertOrUpdate(any: T) {
//        if (insertIgnore(any) == -1L) {
//            update(any)
//        }
//    }
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(any: T)

    @Delete
     fun delete(any: T)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
     fun insertIgnoreList(list: List<T>): List<Long>
}