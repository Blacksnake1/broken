package com.nekosoft.brokenglass.ui.fireScreen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.ActivityFireBlowBinding
import com.nekosoft.brokenglass.App
import com.nekosoft.brokenglass.base.BaseActivityNotFullScreen
import com.nekosoft.brokenglass.di.recoder.MyMediaRecorder
import com.nekosoft.brokenglass.di.recoder.World
import com.nekosoft.brokenglass.extention.goneExt
import com.nekosoft.brokenglass.utils.ConstantsApp
import com.nekosoft.brokenglass.extention.visibleExt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class FireBlowActicity : BaseActivityNotFullScreen<ActivityFireBlowBinding>() {
    private var mRecorder: MyMediaRecorder? = null
    private var isrun = true
    private var mediaPlayer: MediaPlayer? = null
    private var audioManager: AudioManager? = null
    private var audioRecord: AudioRecord? = null
    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)


    override fun setUpData() {
//        Constants.isShowOpenAds = true
        mediaPlayer = MediaPlayer.create(this, R.raw.firewind)
        audioManager =
            this.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        mRecorder = MyMediaRecorder()
        val file = createFile(this)
        startRecord(file)
    }

    override fun setUpViews() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.imgNotice.goneExt()
        }, 10000)
    }

    override fun setUpEvent() {

    }

    private fun startRecord(fFile: File?) {
        try {
            mRecorder?.setMyRecAudioFile(fFile)
            val b =try {
                mRecorder?.startRecorder()
            }catch (e:Exception){

            }
            
            if (b == true) {
                startListenAudio()
            } else {
                Toast.makeText(
                    this, getString(R.string.activity_recStartErr), Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(
                this, getString(R.string.activity_recBusyErr), Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        }
    }

    fun createFile(context: Context) = File(
        getOutputDirectory(context), "temp.amr"
    )

    fun getOutputDirectory(context: Context): File {
        val appContext = context.applicationContext
        val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
            File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        if (mediaDir == null || !mediaDir.exists()) {
            mediaDir?.mkdirs()
        }
//            return mediaDir!!
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else appContext.filesDir

    }


    private val avgDbCount = 70
    private val maxDbCount = 100
    private var dbCount: Float? = null
    private fun startListenAudio() {
        lifecycleScope.launch(Dispatchers.IO) {
            while (isrun) {
                val volume = try {
                    mRecorder?.maxAmplitude ?: 5f
                } catch (e: Exception) {
                    5f
                }
                if (volume > 0 && volume < 1000000) {
                    World.setDbCount(
                        20 * Math.log10(volume.toDouble()).toFloat()
                    )
                    withContext(Dispatchers.Main) {
                        dbCount = World.dbCount / maxDbCount
                        if (dbCount!! > 0.7) {
                            binding.imageFrame.visibleExt()
                            binding.imageFrame.scaleImage(dbCount!!)
                            if (soundModeAction(audioManager!!)) {
                                mediaPlayer?.start()
                            }
                        } else {
                            binding.imageFrame.hideImage(dbCount!!,
                                animationRuning = {
                                }, animationEnd = {
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
    }

    private var mute = 0

    override fun onDestroy() {
        isrun = false
        mediaPlayer?.release()
        stopListening()
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()

        mRecorder?.stopRecording()
        mediaPlayer?.stop()
        isrun = false
    }

    override fun onRestart() {
        super.onRestart()
        isrun = true
        setUpData()
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

    override fun getViewBinding(): ActivityFireBlowBinding {
        return ActivityFireBlowBinding.inflate(layoutInflater)
    }


    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        backToFireActivity()
    }

    private fun backToFireActivity() {
        App.getInstance().backToFireActivity = 1
        val intent = Intent(this, FireScreenActivity::class.java)
        intent.putExtra(ConstantsApp.FROM_FIRE_TO, true)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
    }

    fun startListening() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        audioRecord?.startRecording()

        val buffer = ShortArray(bufferSize)
        var readBytes: Int

        while (true) {
            readBytes = audioRecord?.read(buffer, 0, bufferSize) ?: 0
            // Phân tích buffer để lấy cường độ âm thanh
            val amplitude = calculateAmplitude(buffer, readBytes)
            // Sử dụng amplitude ở đây cho mục đích của bạn
            // Ví dụ: gửi amplitude tới UI để hiển thị đồ thị hoặc thực hiện các hành động khác
        }
    }

    private fun calculateAmplitude(buffer: ShortArray, bufferSize: Int): Double {
        var sum = 0.0
        for (i in 0 until bufferSize) {
            sum += buffer[i] * buffer[i]
        }
        return Math.sqrt(sum / bufferSize)
    }

    fun stopListening() {
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }


}