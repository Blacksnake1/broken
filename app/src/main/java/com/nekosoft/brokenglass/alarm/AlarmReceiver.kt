package com.nekosoft.brokenglass.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_TIME_CHANGED
import android.util.Log
import com.nekosoft.brokenglass.alarm.AlarmUtils.ACTION_SET_REPETITIVE_EXACT


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ACTION_SET_REPETITIVE_EXACT -> {
                context?.let {
//                    AlarmUtils.setNoti(it)
                    NotificationUtils.setNotification(context)
                }
            }
            ACTION_TIME_CHANGED ->{
                context?.let {
                    AlarmUtils.setNoti(context)
                }
            }


        }
    }

}