package com.nekosoft.brokenglass.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.nekosoft.brokenglass.alarm.AlarmUtils.ACTION_SET_REPETITIVE_EXACT
import com.nekosoft.brokenglass.alarm.AlarmUtils.EXTRA_EXACT_ALARM_TIME
import java.util.Calendar

class AlarmService(private val context: Context) {
    private val alarmManager: AlarmManager? =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
    private val NOTIFI_REQUEST_CODE = 1945

    fun setRepetitiveAlarm(timeInMillis: Long) {
        setAlarm(
            timeInMillis,
            getPendingIntent(
                getIntent().apply {
                    action = ACTION_SET_REPETITIVE_EXACT
                    putExtra(EXTRA_EXACT_ALARM_TIME, timeInMillis)
                }
            )
        )
    }
    fun setRepeatAlarm(time:Calendar){
        alarmManager?.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            time.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            getPendingIntent(
                getIntent().apply {
                    action = ACTION_SET_REPETITIVE_EXACT
                    putExtra(EXTRA_EXACT_ALARM_TIME, time.timeInMillis)
                },
            )
        )
    }


    private fun getPendingIntent(intent: Intent) =
        PendingIntent.getBroadcast(
            context,
            NOTIFI_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
//    fun setAlarm(
//        message: String, timeInMillis: Long, title: String
//    ) {
//        setAlarm(
//            timeInMillis,
//            getPendingIntent(
//                getIntent().apply {
//                    action = ACTION_SET_NOTIFICATION
//                    putExtra("EXTRA_MESSAGE", message)
//                    putExtra("EXTRA_TITLE", title)
//                }, title
//            )
//        )
//    }
//
//    fun cancelAlarm(message: String, title: String) {
//        alarmManager?.cancel(
//            getPendingIntent(
//                getIntent().apply {
//                    action =
//                        ACTION_SET_NOTIFICATION
//                    putExtra("EXTRA_MESSAGE", message)
//                    putExtra("EXTRA_TITLE", title)
//                }, title
//            )
//        )
//    }

    private fun setAlarm(timeInMillis: Long, pendingIntent: PendingIntent) {
        alarmManager?.let {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
        }
    }



    private fun getPendingIntent(intent: Intent, title: String) =
        PendingIntent.getBroadcast(
            context,
            NOTIFI_REQUEST_CODE,
            Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )




    private fun getIntent() = Intent(context, AlarmReceiver::class.java)

}