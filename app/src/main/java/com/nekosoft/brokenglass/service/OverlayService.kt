package com.nekosoft.brokenglass.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.brokenscreen.prankapp.crack.screen.R
import com.nekosoft.brokenglass.customView.BrokenView
import com.nekosoft.brokenglass.customView.OverlayView
import com.nekosoft.brokenglass.data.local.AppPreference
import com.nekosoft.brokenglass.data.model.EffectBrokenModel
import com.nekosoft.brokenglass.ui.SplashActivity
import com.nekosoft.brokenglass.utils.ConstantsApp
import com.nekosoft.brokenglass.utils.ConstantsApp.SELLECT_SHAKE
import com.nekosoft.brokenglass.utils.canDrawOverlay
import dagger.hilt.android.AndroidEntryPoint
import java.util.Objects
import javax.inject.Inject

@AndroidEntryPoint
class OverlayService : Service() {
    @Inject
    lateinit var sharedPreferences: AppPreference

    //    private var newWindowManager: WindowManager? = null
    private var screenEffect: String? = null
    private var mView: View? = null
    private var newView: View? = null
    private var screenModel: EffectBrokenModel? = null

    private lateinit var mWindowManager: WindowManager
    private var tranparentParam: WindowManager.LayoutParams? = null
    private var brokenParam: WindowManager.LayoutParams? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f
    private var sensorManager: SensorManager? = null

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = AppPreference(applicationContext)
        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        screenEffect =
            sharedPreferences.getString(ConstantsApp.SCREEN_EFFECT, ConstantsApp.SELLECT_TOUCH)
        if (intent != null) {
            if (!canDrawOverlay(applicationContext)) {
                stopSelf()
                if (newView?.isShown == true) {
                    mWindowManager?.removeView(newView)
                }
//                NotificationControllerService.turnOffNotify(applicationContext)
                if (screenEffect == SELLECT_SHAKE && sensorManager != null) {
                    sensorManager!!.unregisterListener(sensorListener)
                }
                return START_NOT_STICKY
            }

            when (intent.action) {
                ACTION_STOP_SERVICE_ACCESS -> {
                    notificationManager?.cancel(NOTIFICATION_ID)
                    if (newView != null) {
                        if (newView!!.isShown) {
                            mWindowManager.removeView(newView)
                        }
                    }

                    if (screenEffect == SELLECT_SHAKE && sensorManager != null) {
                        sensorManager!!.unregisterListener(sensorListener)
                    }
                    stopSelf()
                }

                ACTION_START_SERVICE_ACCESS -> {
                    enableWindowManager()
                }
            }
        } else {
            Log.d("intent", "onStartCommand 103: deo co intent service roi ")
        }
        return START_STICKY

    }

    private fun enableWindowManager() {
        val typeOverlay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        if (mView == null) {
            tranparentParam = if (screenEffect == ConstantsApp.SELLECT_TOUCH) {
                WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    0,
                    0,
                    typeOverlay,
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                            or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                            or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                            or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    PixelFormat.TRANSLUCENT
                )
            } else {
                WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    0,
                    0,
                    typeOverlay,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                            or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                            or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                            or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    PixelFormat.TRANSLUCENT
                )
            }
//            mView = LayoutInflater.from(this).inflate(R.layout.none_view_overlay, null, false)
            mView = OverlayView(this)
            // Add the view as screen overlay
            try {
                mWindowManager.addView(mView, tranparentParam)
                if (screenEffect == ConstantsApp.SELLECT_TOUCH) {
                    mView?.let { eventClick(it) }
                } else {
                    evenShake()
                }
            } catch (ex: Exception) {
                Log.e("ACCSVC", "adding view failed", ex)
            }
        } else {
            mWindowManager.removeView(mView)
            brokenParam = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                0,
                0,
                typeOverlay,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                        or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                PixelFormat.TRANSLUCENT
            )
                .apply {
                    gravity = Gravity.TOP or Gravity.START
                }

            newView = BrokenView(this)
            try {
                mWindowManager.addView(newView, brokenParam)
                creatNotifi()
            } catch (ex: Exception) {
                Log.e("ACCSVC", "adding view failed", ex)
            }
        }
    }


    private fun evenShake() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        // Getting the Sensor Manager instance

        Objects.requireNonNull(sensorManager)?.registerListener(
            sensorListener, sensorManager!!
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
        )

        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH
    }

    private val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {

            // Fetching x,y,z values
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration

            // Getting current accelerations
            // with the help of fetched x,y,z values
            currentAcceleration = kotlin.math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta

            // Display a Toast message if
            // acceleration value is over 12
            if (acceleration > 10) {
                if (clickCount == 0) {
                    clickCount = 1

                    val song = MediaPlayer.create(applicationContext, R.raw.glassbroken)
                    song.start()
                    enableWindowManager()
                }

            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        }
    }


    private var clickCount = 0

    /** xử lý event khi người dùng touch hay shake*/
    @SuppressLint("ClickableViewAccessibility")
    private fun eventClick(view: View) {
        view.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.performClick() // Gọi performClick để giữ chức năng click
                    if (clickCount == 0) {
                        val song = MediaPlayer.create(applicationContext, R.raw.glassbroken)
                        song.start()
                        clickCount = 1
                        enableWindowManager()
                    }
                    true
                }
                // Các trường hợp khác
                else -> false
            }
        }
    }

    var notificationManager: NotificationManager? = null
    var channel: NotificationChannel? = null
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        notificationManager = getSystemService(
            NotificationManager::class.java
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Allow putting screen overlay",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager?.createNotificationChannel(channel!!)
        }
    }


    @SuppressLint("RestrictedApi")
    private fun creatNotifi() {
        val remoteViews = RemoteViews(packageName, R.layout.remote_view)
        val logo = if (Build.MANUFACTURER == "Google") {
            R.drawable.logo_black
        } else {
            R.mipmap.ic_launcher
        }

        val notifiBuilder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(logo)
                .setColor(this.getColor(R.color.puper_27ff7c05))
                .setCustomBigContentView(remoteViews)
                .setCustomContentView(remoteViews)
                .setAutoCancel(false)
                .setOngoing(true)
                .setShowWhen(false)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(eventOffBroken())
                .build()

        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(136, notifiBuilder, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(136, notifiBuilder)
        }
    }

    private fun eventOffBroken(): PendingIntent {
        val notifyIntent = Intent(this, SplashActivity::class.java)
        notifyIntent.putExtra(TURN_OFF_BROKEN, true)
        return PendingIntent.getActivity(
            this, 358, notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mView != null) {
            try {
                (getSystemService(WINDOW_SERVICE) as WindowManager).removeView(mView)
                mView = null
            } catch (ex: Exception) {
            }
        }
        stopForeground(true)
    }


    companion object {
        const val ACTION_STOP_SERVICE_ACCESS = "stop_service_accs"
        const val ACTION_START_SERVICE_ACCESS = "start_overlay_service"
        const val TURN_ON_BROKEN = "TURN_ON_BROKEN "
        const val ACTION_OFF_NOTIFI = "turn off notification"
        const val ACTION_ON_NOTIFI = "turn on notification"
        const val TURN_OFF_BROKEN = "TURN_OFF_BROKEN"
        const val ENABLE_NOTIFI = "enable notify"
        private const val NOTIFICATION_ID = 68688965
        private const val NOTIFICATION_CHANNEL_ID = "overlay_service_broken_glass"

        fun turnOnService(context: Context?) {
            val i = Intent(context, OverlayService::class.java)
            i.action = ACTION_START_SERVICE_ACCESS
            context?.startService(i)
        }

        fun turnOffService(context: Context?) {
            val i = Intent(context, OverlayService::class.java)
            i.action = ACTION_STOP_SERVICE_ACCESS
            context?.startService(i)
        }
    }
}