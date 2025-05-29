package com.nekosoft.brokenglass.alarm

import android.content.Context
import android.util.Log
import com.brokenscreen.prankapp.crack.screen.R
import java.util.*
import kotlin.collections.ArrayList

object AlarmUtils {
        fun setNoti(context: Context) {
        val calendar19: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 19)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val alarmService = AlarmService(context)

        if (Calendar.getInstance().before(calendar19)) {
            alarmService.setRepetitiveAlarm(calendar19.timeInMillis)
        } else if (Calendar.getInstance().after(calendar19)) {
            calendar19.add(Calendar.DATE, 1)
            alarmService.setRepetitiveAlarm(calendar19.timeInMillis)
        }
    }
//    fun setNoti(context: Context) {
//        val calendar: Calendar = Calendar.getInstance().apply {
//            timeInMillis = System.currentTimeMillis()
//            set(Calendar.HOUR_OF_DAY, 19)
//            set(Calendar.MINUTE, 0)
//            set(Calendar.SECOND, 0)
//            set(Calendar.MILLISECOND, 0)
//
//        }
//        val alarmService = AlarmService(context)
//        alarmService.setRepeatAlarm(calendar)
//    }


    fun listContentNotification(context: Context): ArrayList<Pair<String, String>> {
        val list = arrayListOf<Pair<String, String>>()
        list.add(
            Pair(
                context.getString(R.string.title_noti_1),
                context.getString(R.string.desc_noti_1)
            )
        )
        list.add(
            Pair(
                context.getString(R.string.title_noti_2),
                context.getString(R.string.desc_noti_2)
            )
        )
        list.add(
            Pair(
                context.getString(R.string.title_noti_3),
                context.getString(R.string.desc_noti_3)
            )
        )
        return list
    }

    const val ACTION_SET_REPETITIVE_EXACT = "ACTION_SET_REPETITIVE_EXACT"
    const val EXTRA_EXACT_ALARM_TIME = "EXTRA_EXACT_ALARM_TIME"
    const val POS_NOTIFI = "POS_NOTIFI "
}