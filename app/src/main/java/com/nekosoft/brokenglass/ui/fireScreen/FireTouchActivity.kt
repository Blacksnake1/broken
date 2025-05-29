package com.nekosoft.brokenglass.ui.fireScreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.ActivityFireTouchBinding
import com.nekosoft.brokenglass.App
import com.nekosoft.brokenglass.base.BaseActivityNotFullScreen
import com.nekosoft.brokenglass.utils.ConstantsApp.FROM_FIRE_TO
import pl.droidsonroids.gif.GifImageView


class FireTouchActivity : BaseActivityNotFullScreen<ActivityFireTouchBinding>() {
    private var count = 1
    private var mediaPlayer: MediaPlayer? = null
    private var mediaPlayer2: MediaPlayer? = null
    private var audioManager: AudioManager? = null
    private var cleanScreen = true

    override fun setUpData() {
//        Constants.isShowOpenAds = true
        destroy = false
        // Lắng nghe sự kiện chạm
        initializeMediaPlayers()
        audioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    private fun initializeMediaPlayers() {
        mediaPlayer = MediaPlayer.create(this, R.raw.firesound)
        mediaPlayer2 = MediaPlayer.create(this, R.raw.firesound)
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

    override fun setUpViews() {


    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setUpEvent() {
        binding.root.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    count++
                    if (count % 15 == 0) {
                        val x = event.x.toInt()
                        val y = event.y.toInt()

                        val lp = RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                        )
                        val iv = GifImageView(applicationContext)
                        lp.setMargins(x - 150, y - 150, v.width - x - 150, v.height - y - 150)
                        iv.layoutParams = lp
                        iv.setImageResource(R.drawable.smallfile)
                        if (soundModeAction(audioManager!!)) {
                            if (mediaPlayer?.isPlaying == false && mediaPlayer2?.isPlaying == false) {
                                mediaPlayer?.start()
                                runMediaToDie()
                            }
                        }

                        (v as ViewGroup).addView(iv)
                        cleanScreen = false
                        fireShow++
                        Handler(Looper.getMainLooper()).postDelayed({
                            (v as ViewGroup).removeView(iv)
                            fireHide++
                            if (fireHide == fireShow && !destroy) {
                                cleanScreen = true
                                if (mediaPlayer?.isPlaying == false && mediaPlayer2?.isPlaying == true) {
                                    mediaPlayer2?.pause()
                                } else if (mediaPlayer2?.isPlaying == false && mediaPlayer?.isPlaying == true) {
                                    mediaPlayer?.pause()
                                } else if (mediaPlayer2?.isPlaying == true && mediaPlayer?.isPlaying == true) {
                                    mediaPlayer?.pause()
                                    mediaPlayer2?.pause()

                                }
                            }
                        }, 4000)
                    }
                }

                MotionEvent.ACTION_DOWN -> {
                    val x = event.x.toInt()
                    val y = event.y.toInt()

                    val lp = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    )
                    val iv = GifImageView(applicationContext)
                    lp.setMargins(x - 150, y - 150, v.width - x - 150, v.height - y - 150)
                    iv.layoutParams = lp
                    iv.setImageResource(R.drawable.smallfile)

                    (v as ViewGroup).addView(iv)
                    cleanScreen = false
                    fireShow++
                    if (soundModeAction(audioManager!!)) {
                        mediaPlayer?.start()
                        runMediaToDie()
                    }
                    Handler(Looper.getMainLooper()).postDelayed({
                        (v as ViewGroup).removeView(iv)
                        fireHide++
                        if (fireShow == fireHide && !destroy) {
                            cleanScreen = true
                            if (mediaPlayer?.isPlaying == false && mediaPlayer2?.isPlaying == true) {
                                mediaPlayer2?.pause()
                            } else if (mediaPlayer2?.isPlaying == false && mediaPlayer?.isPlaying == true) {
                                mediaPlayer?.pause()
                            } else if (mediaPlayer2?.isPlaying == true && mediaPlayer?.isPlaying == true) {
                                mediaPlayer?.pause()
                                mediaPlayer2?.pause()
                            }
                        }
                    }, 4000)
                }

                MotionEvent.ACTION_UP -> {
                }

            }
            true
        }

    }

    private val handler = Handler(Looper.getMainLooper())
    private var fireShow = 0
    private var fireHide = 0
    private var destroy = false

    private fun runMediaToDie() {
        handler.postDelayed({
            if (mediaPlayer?.isPlaying == false && mediaPlayer2?.isPlaying == true) {
                if (stateStop)return@postDelayed
                mediaPlayer?.start()
                runMediaToDie()
            } else if (mediaPlayer2?.isPlaying == false && mediaPlayer?.isPlaying == true) {
                if (stateStop)return@postDelayed
                mediaPlayer2?.start()
                runMediaToDie()
            } else if (fireHide != fireShow) {
                if (stateStop)return@postDelayed
                mediaPlayer?.start()
                runMediaToDie()
            }
        }, 1000)
    }

    override fun onDestroy() {
        destroy = true
        mediaPlayer?.release()
        mediaPlayer2?.release()
        super.onDestroy()
    }

    /** biến để kiểm tra trạng thái activity ở vòng đời stop hay không */
    private var stateStop = false

    override fun onStop() {
        stateStop= true
        if (mediaPlayer?.isPlaying == true) mediaPlayer?.stop()
        if (mediaPlayer2?.isPlaying == true) mediaPlayer2?.stop()
        super.onStop()
    }

    override fun onRestart() {
        super.onRestart()
//        backToFireActivity()
        stateStop= false
        initializeMediaPlayers()
        if (!cleanScreen){
            mediaPlayer?.start()
        }
    }

    override fun getViewBinding(): ActivityFireTouchBinding {
        return ActivityFireTouchBinding.inflate(layoutInflater)
    }

    private var clickBack = false

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        backToFireActivity()
    }

    private fun backToFireActivity() {
        App.getInstance().backToFireActivity = 1
        handler.removeCallbacksAndMessages(null)    // Hủy bỏ công việc chờ đợi nếu có
        clickBack = true
        val intent = Intent(this, FireScreenActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra(FROM_FIRE_TO, true)
        startActivity(intent)
        finish()
    }
}