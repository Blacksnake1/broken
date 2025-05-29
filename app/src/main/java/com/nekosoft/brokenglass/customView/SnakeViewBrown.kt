package com.nekosoft.crazynake.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.LottieResult
import com.snake.screen.snake.`in`.hand.R
import com.nekosoft.crazynake.data.local.AppSharedPreferences
import com.nekosoft.crazynake.service.ScreenSnakeService
import com.nekosoft.crazynake.utils.isServiceRunning
import java.util.concurrent.atomic.AtomicReference


class SnakeViewBrown @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private val TRANSLATE_SPEED = 0.008f
    private val SCALE = 0.65f

    var values: List<SnakeDirection> = mutableListOf(
        SnakeDirection.LeftTop_to_RightBottom,
        SnakeDirection.RightTop_to_LeftBottom,
        SnakeDirection.LeftCenter_to_RightCenter,
        SnakeDirection.RightCenter_To_LeftCenter,
        SnakeDirection.LeftBottom_to_RightTop,
        SnakeDirection.RightBottom_to_LeftTop,
    )
    private var snakeDrawable: LottieDrawable = LottieDrawable()
    private val snakeRect = Rect()
    private val matrix = Matrix()

    private var randomValue: SnakeDirection? = null
        set(value) {
            dx = AtomicReference(0f)
            dy = AtomicReference(0f)
            field = value
            resetSnakePosition()
        }

    private var dx: AtomicReference<Float> = AtomicReference(0f)
    private var dy: AtomicReference<Float> = AtomicReference(0f)
    private var szWidth: Int? = null
    private var szHeight: Int? = null

    init {
        //list ảnh nguyên bản
        val sharedPreferences = AppSharedPreferences.getInstance(context)
        szWidth = sharedPreferences?.getInt("szWidth", 0) //1080
        szHeight = sharedPreferences?.getInt("szHeight", 0) //2046

        snakeDrawable.enableMergePathsForKitKatAndAbove(true)
        snakeDrawable.callback = this

        randomValue = SnakeDirection.RightTop_to_LeftBottom // values[(values.indices).random()]
        val result: LottieResult<LottieComposition> = LottieCompositionFactory.fromRawResSync(
            context.applicationContext, R.raw.snake_brown
        )

        snakeDrawable.composition = result.value
        snakeDrawable.alpha = 255
        snakeDrawable.speed = 1f
        snakeDrawable.repeatCount = LottieDrawable.INFINITE
        snakeDrawable.addAnimatorUpdateListener {
            val left = (width - snakeDrawable.intrinsicWidth) / 2
            val top = 0
            val right = left + snakeDrawable.intrinsicWidth
            val bot = top + snakeDrawable.intrinsicHeight
            snakeRect.set(left, top, right, bot)
            snakeDrawable.bounds = snakeRect
            slide()
            invalidate()
        }
        snakeDrawable.start()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.concat(matrix)
        snakeDrawable.draw(canvas)
        canvas.restore()
    }

    private fun slide() {
        if (!isServiceRunning(context, ScreenSnakeService::class.java)) {
            snakeDrawable.cancelAnimation();
            snakeDrawable.clearComposition();
        }
        when (randomValue) {
            SnakeDirection.LeftTop_to_RightBottom -> {
                matrix.postTranslate(addAndGetDx(TRANSLATE_SPEED), addAndGetDy(TRANSLATE_SPEED))
                if (getTranslateY(matrix) > szHeight!! + snakeDrawable.intrinsicHeight) { // Cái này sẽ tính cái điểm cuối đường khi mà nó đi hết rồi thì reset
                    randomValue = values[(values.indices).random()]
                }
            }

            SnakeDirection.RightTop_to_LeftBottom -> {
                matrix.postTranslate(
                    addAndGetDx(-TRANSLATE_SPEED),
                    addAndGetDy(TRANSLATE_SPEED)
                )
                if (getTranslateY(matrix) > szHeight!! + snakeDrawable.intrinsicHeight - 800f) { // Cái này sẽ tính cái điểm cuối đường khi mà nó đi hết rồi thì reset
                    randomValue = values[(values.indices).random()]
                }
            }

            SnakeDirection.LeftCenter_to_RightCenter -> {
                matrix.postTranslate(addAndGetDx(TRANSLATE_SPEED), 0f)
                if (getTranslateX(matrix) > szWidth!! + snakeDrawable.intrinsicHeight - 900f) { // Cái này sẽ tính cái điểm cuối đường khi mà nó đi hết rồi thì reset
                    randomValue = values[(values.indices).random()]

                }
            }

            SnakeDirection.RightCenter_To_LeftCenter -> {
                matrix.postTranslate(addAndGetDx(-TRANSLATE_SPEED), 0f)
                if (getTranslateX(matrix) < -snakeDrawable.intrinsicHeight + 900f) { // Cái này sẽ tính cái điểm cuối đường khi mà nó đi hết rồi thì reset
                    randomValue = values[(values.indices).random()]
                }
            }

            SnakeDirection.LeftBottom_to_RightTop -> {
                matrix.postTranslate(
                    addAndGetDx(TRANSLATE_SPEED),
                    addAndGetDy(-TRANSLATE_SPEED)
                )

                if (getTranslateX(matrix) > snakeDrawable.intrinsicWidth + szWidth!! + 250) {// Cái này sẽ tính cái điểm cuối đường khi mà nó đi hết rồi thì reset
                    randomValue = values[(values.indices).random()]
                }
            }

            else -> {
                matrix.postTranslate(
                    addAndGetDx(-TRANSLATE_SPEED),
                    addAndGetDy(-TRANSLATE_SPEED)
                )
                if (szWidth!! < -getTranslateY(matrix)) { // Cái này sẽ tính cái điểm cuối đường khi mà nó đi hết rồi thì reset
                    randomValue = values[(values.indices).random()]
                }
            }
        }
    }

    private fun resetSnakePosition() {
        dx.set(0f)
        dy.set(0f)
        matrix.reset()
        when (randomValue) {
            SnakeDirection.LeftTop_to_RightBottom -> {
                matrix.postScale(
                    SCALE,
                    SCALE,
                    snakeDrawable.intrinsicWidth / 2f,
                    snakeDrawable.intrinsicHeight / 2f
                )
                matrix.postRotate(
                    135f,
                    snakeDrawable.intrinsicWidth / 2f,
                    snakeDrawable.intrinsicHeight / 2f
                )
                matrix.postTranslate(
                    0f - snakeDrawable.intrinsicWidth - 700,
                    0f - snakeDrawable.intrinsicHeight + 700
                )
            }

            SnakeDirection.RightTop_to_LeftBottom -> {
                matrix.postScale(
                    SCALE,
                    SCALE,
                    snakeDrawable.intrinsicWidth / 2f,
                    snakeDrawable.intrinsicHeight / 2f
                )
                matrix.postRotate(
                    -135f,
                    snakeDrawable.intrinsicWidth / 2f,
                    snakeDrawable.intrinsicHeight / 2f
                )
                matrix.postTranslate(
                    szWidth!! * 1f + 200,
                    280f
                )
            }

            SnakeDirection.LeftCenter_to_RightCenter -> {
                matrix.postScale(
                    SCALE,
                    SCALE,
                    snakeDrawable.intrinsicWidth / 2f,
                    snakeDrawable.intrinsicHeight / 2f
                )
                matrix.postRotate(
                    90f,
                    snakeDrawable.intrinsicWidth / 2f,
                    snakeDrawable.intrinsicHeight / 2f
                )
                matrix.postTranslate(
                    -snakeDrawable.intrinsicHeight * 1f + 900f,
                    -szWidth!! / 2f
                )
            }

            SnakeDirection.RightCenter_To_LeftCenter -> {
                matrix.postScale(
                    SCALE,
                    SCALE,
                    snakeDrawable.intrinsicWidth / 2f,
                    snakeDrawable.intrinsicHeight / 2f
                )
                matrix.postRotate(
                    -90f,
                    snakeDrawable.intrinsicWidth / 2f,
                    snakeDrawable.intrinsicHeight / 2f
                )
                matrix.postTranslate(
                    snakeDrawable.intrinsicHeight * 1f - 1500f,
                    -szWidth!! / 2f
                )
            }

            SnakeDirection.LeftBottom_to_RightTop -> {

                matrix.postScale(
                    SCALE,
                    SCALE,
                    snakeDrawable.intrinsicWidth / 2f,
                    snakeDrawable.intrinsicHeight / 2f
                )
                matrix.postRotate(
                    45f,
                    snakeDrawable.intrinsicWidth / 2f,
                    snakeDrawable.intrinsicHeight / 2f
                )
                matrix.postTranslate(
                    -snakeDrawable.intrinsicWidth * 1f - 500f,
                    szWidth!! / 2f + 500f
                )
            }

            else -> {
                matrix.postScale(
                    SCALE,
                    SCALE,
                    snakeDrawable.intrinsicWidth / 2f,
                    snakeDrawable.intrinsicHeight / 2f
                )
                matrix.postRotate(
                    -45f,
                    snakeDrawable.intrinsicWidth / 2f,
                    snakeDrawable.intrinsicHeight / 2f
                )
//
                matrix.postTranslate(
                    szWidth!! + snakeDrawable.intrinsicWidth * 1f - 800f,
                    szWidth!! / 2f + 500f
                )
            }
        }
    }

    private fun addAndGetDx(addValue: Float): Float {
        dx.set(dx.get() + addValue)
        return dx.get()
    }

    private fun addAndGetDy(addValue: Float): Float {
        dy.set(dy.get() + addValue)
        return dy.get()
    }

    private fun getTranslateX(matrix: Matrix): Float {
        val v = FloatArray(9)
        matrix.getValues(v)
        return v[Matrix.MTRANS_X]
    }

    private fun getTranslateY(matrix: Matrix): Float {
        val v = FloatArray(9)
        matrix.getValues(v)
        return v[Matrix.MTRANS_Y]
    }

}