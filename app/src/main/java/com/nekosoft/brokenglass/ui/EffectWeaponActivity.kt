package com.nekosoft.brokenglass.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Point
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.animation.addListener
import androidx.fragment.app.FragmentManager
import com.brokenscreen.prankapp.crack.screen.BuildConfig
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.ActivityEffectWeaponBinding
import com.gianghv.utils.AdsConfigUtils
import com.gianghv.utils.AdsConfigUtils.Companion.inter_weapon_status
import com.gianghv.utils.AdsConfigUtils.Companion.native_downloading_status
import com.gianghv.utils.ConstantsAds.isShowAdsFull
import com.nekosoft.brokenglass.base.BaseActivity
import com.nekosoft.brokenglass.customView.dialog.DialogFakeDownloading
import com.nekosoft.brokenglass.customView.dialog.DialogFakeDownloading3
import com.nekosoft.brokenglass.customView.dialog.DialogReadyToUse
import com.nekosoft.brokenglass.data.model.WeaponModel
import com.nekosoft.brokenglass.extention.ContextExt.changeStatusBarColor
import com.nekosoft.brokenglass.extention.disableExt
import com.nekosoft.brokenglass.extention.enableExt
import com.nekosoft.brokenglass.extention.requestAndShowInterHaveNativeFull
import com.nekosoft.brokenglass.ui.home.HomeActivity
import com.nekosoft.brokenglass.ui.selectWeapon.KindWeapon
import com.nekosoft.brokenglass.ui.selectWeapon.SelectWeaponDialog
import com.nekosoft.brokenglass.utils.clickSafe
import kotlin.math.min


class EffectWeaponActivity : BaseActivity<ActivityEffectWeaponBinding>() {
    private var expandedState = false
    private var collaped = true
    private lateinit var selectWeaponDialog: SelectWeaponDialog
    private lateinit var dialogFakeDownloading: DialogFakeDownloading3
    private lateinit var dialogReadyToUse: DialogReadyToUse
    private lateinit var fm: FragmentManager
    private var mediaPlayer: MediaPlayer? = null
    private var audioManager: AudioManager? = null
    private var sound: Int? = null
    private var currentWeapon: WeaponModel? = null
    private var currentPos: Int? = null
    private var effectList = mutableListOf<Int>()
    private var isLoading = false
    private var dialogReadyToUseIsShowed = false
    private var isDestroyClicked = false
    private var isClickFab = false

    override fun setScreen() {
        fullScreencall()
        changeStatusBarColor(R.color.black)
    }

    init {
        onBackPressedAction = {
            if (!selectWeaponDialog.isShowing) {
                requestAndShowInter("eventBack")
            } else {

                val intent = Intent(this@EffectWeaponActivity, HomeActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.slide_out)
                finish()
            }
        }
    }

    override fun setupData() {
//        enableEdgeToEdge()
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        audioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    override fun setupViews() {
        selectWeaponDialog = SelectWeaponDialog(this, object : KindWeapon {
            override fun weapon(weaponModel: WeaponModel) {
                currentWeapon = weaponModel
                addModel(weaponModel)
            }

            override fun backToHome() {
                onBackPressedDispatcher.onBackPressed()
            }

            override fun eventBtnDestroy() {
                dialogFakeDownloading.showDialog()
            }
        })

        showSelectWeaponDialog()
        dialogFakeDownloading =
            DialogFakeDownloading3(this, native_downloading_status, BuildConfig.native_function_id) {
                if (!isShowAdsFull) {
                    isDestroyClicked = true
                    dialogReadyToUseIsShowed = true
                    dialogReadyToUse.showDialog(currentWeapon,"weapon")
                }
            }

        dialogReadyToUse = DialogReadyToUse(this, "weapon") {
            requestAndShowInterHaveNativeFull(
                this,
                AdsConfigUtils.inter_done_status,
                BuildConfig.inter_function_id,
                actionDone = {
                    isDestroyClicked = false
                    dialogFakeDownloading.dismissDialog()
                }
            )

        }
        showView()
    }

    private fun showSelectWeaponDialog() {
        dialogReadyToUseIsShowed = false
        selectWeaponDialog.showDialog()
    }

    override fun eventAfferNativeFullDismiss() {
        if (!isLoading && !dialogReadyToUseIsShowed && isDestroyClicked) {
            dialogReadyToUseIsShowed = true
            dialogReadyToUse.showDialog(currentWeapon,"weapon")
        }
    }

    private fun addModel(weapon: WeaponModel) {
        effectList.clear()
        effectList.addAll(weapon.shootEffect)
        sound = weapon.sound
        when (weapon.name) {
            "sniper" -> {
                currentPos = 0
            }

            "rifle" -> {
                currentPos = 1
            }

            "revolver" -> {
                currentPos = 2
            }

            "shotgun" -> {
                currentPos = 3
            }

            "pistol" -> {
                currentPos = 4
            }

            "knife" -> {
                currentPos = 5
            }

            "hammer" -> {
                currentPos = 6
            }

            "claws" -> {
                currentPos = 7
            }

            "bat" -> {
                currentPos = 8
            }
        }

    }

    private var startX = 0
    private var startY = 0

    @SuppressLint("ClickableViewAccessibility")
    private fun showView() {
        binding.paintview.setOnTouchListener { view, event ->
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_MOVE -> {

                }

                MotionEvent.ACTION_DOWN -> {
                    startX = event.x.toInt()
                    startY = event.y.toInt()
                    if (currentPos != 5 && currentPos != 7) {
                        val lp = RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                        )
                        val iv = ImageView(applicationContext)
                        lp.setMargins(
                            startX - 150,
                            startY - 150,
                            view.width - startX - 150,
                            view.height - startY - 150
                        )
                        iv.layoutParams = lp
                        val effect = effectList[(effectList.indices).random()]
                        iv.setImageResource(effect)
                        onSound()
                        (view as ViewGroup).addView(iv)
                    }

                }

                MotionEvent.ACTION_UP -> {
                    val endX = event.x.toInt()
                    val endY = event.y.toInt()

                    val deltaX = endX - startX
                    val deltaY = endY - startY

                    val centerX = (startX + endX) / 2
                    val centerY = (startY + endY) / 2

                    val distance = Math.sqrt((deltaX * deltaX + deltaY * deltaY).toDouble()).toInt()

                    val midPoint = Point(centerX, centerY)
                    val startPoint = Point(startX, startY)
                    val endPoint = Point(endX, endY)

//                    val angle = Math.toDegrees(Math.atan2(deltaY.toDouble(), deltaX.toDouble())).toFloat()

                    if (currentPos == 5 || currentPos == 7) {
//                        tinhGoc(startX.toFloat(), startY.toFloat(), endX.toFloat(), endY.toFloat())
                        val degrees = calculate_angle(
                            startX.toDouble(),
                            startY.toDouble(),
                            endX.toDouble(),
                            endY.toDouble()
                        )

                        val lp = RelativeLayout.LayoutParams(
                            distance,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                        )
                        val iv = ImageView(applicationContext)

                        lp.setMargins(
                            startX - 220,
                            startY - 220,
                            0,
                            0
                        )
                        iv.layoutParams = lp
                        val effect = effectList[(effectList.indices).random()]
                        iv.setImageResource(effect)
                        iv.rotation = (degrees - 90f)

                        onSound()
                        (view as ViewGroup).addView(iv)
                    }
                }
            }
            true
        }
    }

    private fun tinhGoc(startX: Float, startY: Float, endX: Float, endY: Float) {

        val centerX = (startX + endX) / 2
        val centerY = (startY + endY) / 2

        val deltaX = centerX - min(startX, endX)
        val deltaY = centerY

        val canhK = centerX - deltaX
        val canhH =
            Math.sqrt(((startX - startY) * (startX - startY) + (endX - endY) * (endX - endY)).toDouble())
                .toFloat() / 2

        val radians = Math.acos((canhK / canhH).toDouble())

        val degrees = Math.toDegrees(radians)


    }

    private fun calculate_angle(x1: Double, y1: Double, x2: Double, y2: Double): Float {
//       # Tính độ dốc (slope)
        val slope = (y2 - y1) / (x2 - x1)

//       # Tính góc bằng hàm arctan
        val theta_radians = Math.atan(slope)

//       # Chuyển đổi từ radian sang độ
        val theta_degrees = Math.toDegrees(theta_radians).toFloat()
        if (theta_degrees < 90) {

        }

        return theta_degrees
    }


    private fun onSound() {
        if (soundModeAction(audioManager!!)) {
            try {
                val mediaPlayer = sound?.let { MediaPlayer.create(this, it) }
                mediaPlayer?.start()
                mediaPlayer?.setVolume(1f, 1f)
                mediaPlayer?.setOnCompletionListener {
                    mediaPlayer.release()
                }
            } catch (e: Exception) {
                Log.e("tag123", "exeption sound gun: $e")
            }

        }
    }


    override fun setupEvent() {
        binding.fab.clickSafe {
            if (collaped) {
                animExpand()
            } else if (expandedState) {
                animCollapsed()
            }
        }

        binding.fabReset.clickSafe(1000) {
            binding.paintview.removeAllViews()
        }

        binding.fabBack.clickSafe(1000) {
            if (isClickFab) return@clickSafe
            isClickFab = true
            requestAndShowInter("fabBack")
        }

        binding.fabClose.clickSafe(1000) {
            if (isClickFab) return@clickSafe
            isClickFab = true
            requestAndShowInter("fabClose")
        }
    }

    private fun requestAndShowInter(key: String) {
        requestAndShowInterHaveNativeFull(
            this,
            inter_weapon_status,
            BuildConfig.inter_function_id,
            actionDone = {
                isClickFab = false
                if (key == "fabClose") {
                    eventBtnClose()
                } else {
                    eventBack()
                }
            },
        )
    }


    private fun eventBack() {
        showSelectWeaponDialog()
        animCollapsed()
    }

    private fun eventBtnClose() {
        showSelectWeaponDialog()
        binding.paintview.removeAllViews()
        animCollapsed()
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

    fun calculateDistance(p1: Point, p2: Point): Double {
        return Math.sqrt(
            Math.pow((p2.x - p1.x).toDouble(), 2.0) +
                    Math.pow((p2.y - p1.y).toDouble(), 2.0)
        )
    }

    private fun animExpand() {
        binding.fab.disableExt(null)
        expandedState = true
        collaped = false
        val transBtnReset = ObjectAnimator.ofFloat(binding.fabReset, "translationY", dpToPx(-225f))
        val transBtnShowDialog =
            ObjectAnimator.ofFloat(binding.fabBack, "translationY", dpToPx(-150f))
        val transBtnExit = ObjectAnimator.ofFloat(binding.fabClose, "translationY", dpToPx(-80f))
        val rotateA = ObjectAnimator.ofFloat(binding.fab, "rotation", 0f, 90f)
        val animatorSet = AnimatorSet()
        animatorSet.duration = 100
        animatorSet.playSequentially(transBtnExit, transBtnShowDialog, transBtnReset)
        animatorSet.playTogether(rotateA)
        animatorSet.start()
        animatorSet.addListener(
            onEnd = {
                binding.fab.enableExt()
            }
        )
    }

    private fun animCollapsed() {
        binding.fab.disableExt(null)
        expandedState = false
        collaped = true
        val transBtnReset = ObjectAnimator.ofFloat(binding.fabReset, "translationY", 0f)
        val transBtnShowDialog = ObjectAnimator.ofFloat(binding.fabBack, "translationY", 0f)
        val transBtnExit = ObjectAnimator.ofFloat(binding.fabClose, "translationY", 0f)
        val rotateA = ObjectAnimator.ofFloat(binding.fab, "rotation", 90f, 0f)
        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(transBtnExit, transBtnShowDialog, transBtnReset)
        animatorSet.playTogether(rotateA)
        animatorSet.start()
        animatorSet.addListener(
            onEnd = {
                binding.fab.enableExt()
            }
        )
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
    }

    override fun getViewBinding(): ActivityEffectWeaponBinding {
        return ActivityEffectWeaponBinding.inflate(layoutInflater)
    }


    private fun rotateBitmap(bitmap: Bitmap, degrees: Float, pivotX: Float, pivotY: Float): Bitmap {
        val matrix: Matrix = Matrix()
        matrix.postRotate(degrees, pivotX, pivotY)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }


    override fun onDestroy() {
        super.onDestroy()
        selectWeaponDialog.dismissDialog()

    }
}