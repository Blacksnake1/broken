package com.nekosoft.brokenglass.ui.fireScreen

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.ActivityTransparentBinding
import com.nekosoft.brokenglass.App
import com.nekosoft.brokenglass.base.BaseActivityNotFullScreen
import com.nekosoft.brokenglass.customView.LightningView
import com.nekosoft.brokenglass.extention.disableExt
import com.nekosoft.brokenglass.extention.enableViewsExt
import com.nekosoft.brokenglass.extention.goneExt
import com.nekosoft.brokenglass.extention.visibleExt
import com.nekosoft.brokenglass.utils.ConstantsApp.FROM_FIRE_TO

class ElectricActivity : BaseActivityNotFullScreen<ActivityTransparentBinding>() {
    private var mediaPlayer: MediaPlayer? = null
    private var mediaPlayer2: MediaPlayer? = null
    private var audioManager: AudioManager? = null
    private lateinit var vibrator: Vibrator

    //    private val handler = Handler(Looper.getMainLooper())


    override fun setUpData() {
//        Constants.isShowOpenAds = true
        initializeMediaPlayers()
        audioManager =
            this.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    private fun initializeMediaPlayers() {
        mediaPlayer = MediaPlayer.create(this, R.raw.elecsound)
        mediaPlayer2 = MediaPlayer.create(this, R.raw.elecsound)
    }

    private val completionListener = MediaPlayer.OnCompletionListener {
//        Handler(Looper.getMainLooper()).postDelayed({
        mediaPlayer?.start()
//        }, 1)

    }

    override fun setUpViews() {
        mediaPlayer2?.setOnErrorListener { v1, v2, v3 ->
            // Xử lý lỗi
            false // Trả về false để không ghi nhận lỗi
        }
    }



    @SuppressLint("ClickableViewAccessibility")
    override fun setUpEvent() {
        binding.root.setOnTouchListener { v, event ->
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_MOVE -> {
                    (v as? ViewGroup)?.removeAllViews()
                    val x = event.x // vị trí x của điểm chạm
                    val y = event.y // vị trí y của điểm chạm
                    val lp = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    )
                    val iv = LightningView(applicationContext)
                    lp.setMargins(
                        x.toInt(),
                        y.toInt(),
                        0,
                        0
                    ) // set hình ảnh iv ở đúng vị trí điểm chạm
                    iv.layoutParams = lp
                    (v as ViewGroup).addView(iv)

                    if (event.pointerCount == 2) {
                        fingerUp = false
                        iv.setData(
                            v,
                            event.getX(0),
                            event.getY(0),
                            event.getX(1),
                            event.getY(1),
                            true
                        )
                        effect()
                    } else if (event.pointerCount == 1) {
                        fingerUp = false
                        iv.setData(v, event.getX(0), event.getY(0), null, null, true)
                        effect()
                    } else {
                        fingerUp = true
                        mediaPlayer?.pause()
                        mediaPlayer2?.pause()
                    }
                }

                MotionEvent.ACTION_DOWN -> {
                    fingerUp = false
                }

                MotionEvent.ACTION_UP -> {
                    fingerUp = true
                    (v as ViewGroup).removeAllViews()
                    if (mediaPlayer?.isPlaying == false && mediaPlayer2?.isPlaying == true) {
                        mediaPlayer2?.pause()
                    } else if (mediaPlayer2?.isPlaying == false && mediaPlayer?.isPlaying == true) {
                        mediaPlayer?.pause()
                    } else if (mediaPlayer2?.isPlaying == true && mediaPlayer?.isPlaying == true) {
                        mediaPlayer?.pause()
                        mediaPlayer2?.pause()
                    }
                }
            }
            true
        }
    }

    private fun effect() {
        if (soundModeAction(audioManager!!)) {
            if (mediaPlayer?.isPlaying == false) {
                mediaPlayer?.start()
            }
        }
        vibrate()
    }

    private var fingerUp = false

    private fun runMediaToDie() {
//        handler.postDelayed({
        if (mediaPlayer?.isPlaying == false && mediaPlayer2?.isPlaying == true && !fingerUp) {
            mediaPlayer?.start()
            runMediaToDie()
        } else if (mediaPlayer?.isPlaying == true && mediaPlayer2?.isPlaying == false && !fingerUp) {
            mediaPlayer2?.start()
            runMediaToDie()
        } else if (!fingerUp) {
            mediaPlayer?.start()
            runMediaToDie()
        }
//        }, 1000)
    }


    var a = false
    fun soundModeAction(audioManager: AudioManager): Boolean {
        try {
            when (audioManager.ringerMode) {
                0 -> {
                    // đang tắt âm không rung
                    a = false
                }

                1 -> {
                    a = false
                    //đang rung không âm thanh
                }

                2 -> {
                    a = true
//             đang bật âm
                }
            }
        } catch (unused: Exception) {
        }
        return a
    }

    private fun vibrate() {
        // Kiểm tra nếu thiết bị hỗ trợ rung
        if (vibrator.hasVibrator()) {
            // Tạo một hiệu ứng rung (ví dụ: rung trong 300 mili giây)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        300,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                vibrator.vibrate(300)
            }
        }
    }


    override fun onStop() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
//            mediaPlayer?.reset()
        }

//        if (mediaPlayer2?.isPlaying == true) {
//            mediaPlayer2?.stop()
////            mediaPlayer2?.reset()
//        }
        binding.root.removeAllViews()
        super.onStop()
    }


    override fun onRestart() {
        super.onRestart()
        initializeMediaPlayers()
    }

    override fun onDestroy() {
        // Hủy bỏ công việc chờ đợi nếu có
//        handler.removeCallbacksAndMessages(null)
        mediaPlayer?.release()
//        mediaPlayer2?.release()
        binding.root.removeAllViews()
        super.onDestroy()
    }

    private var clickBack = false

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        backToFireActivity()
    }


    private fun backToFireActivity() {
        clickBack = true
        App.getInstance().backToFireActivity = 1
        val intent = Intent(this, FireScreenActivity::class.java)
        intent.putExtra(FROM_FIRE_TO, true)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
    }

    override fun getViewBinding(): ActivityTransparentBinding {
        return ActivityTransparentBinding.inflate(layoutInflater)
    }
}