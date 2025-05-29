package com.nekosoft.brokenglass.service

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import com.nekosoft.brokenglass.ui.fireScreen.FireScreenActivity
import com.nekosoft.brokenglass.utils.ConstantsApp

class DetectResumeService: Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "goback"){
            val mIntent = Intent(this, FireScreenActivity::class.java)
            val bundle = Bundle()
            bundle.putBoolean(ConstantsApp.FROM_FIRE_TO, true)
            mIntent.putExtras(bundle)
            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(mIntent)
        }
        return START_STICKY
    }
}