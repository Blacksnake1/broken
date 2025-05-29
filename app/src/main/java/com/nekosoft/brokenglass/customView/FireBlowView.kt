package com.nekosoft.brokenglass.customView

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.LottieResult
import com.brokenscreen.prankapp.crack.screen.R
import com.nekosoft.brokenglass.extention.invisibleExt

class FireBlowView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private var dickDrawable: LottieDrawable = LottieDrawable()
    private val dickRect = Rect()
    private val dickMatrix = Matrix()

    init {
        dickDrawable.enableMergePathsForKitKatAndAbove(true)
        dickDrawable.callback = this
        val result: LottieResult<LottieComposition> = LottieCompositionFactory.fromRawResSync(
            context.applicationContext, R.raw.fire_blow
        )
        dickDrawable.setComposition(result.value)
        dickDrawable.repeatCount = LottieDrawable.INFINITE
        dickDrawable.addAnimatorUpdateListener {
            val left = (width - dickDrawable.intrinsicWidth) / 2
            val top = height - dickDrawable.intrinsicHeight
            val right = left + dickDrawable.intrinsicWidth
            val bot = top + dickDrawable.intrinsicHeight
            dickRect.set(left, top, right, bot)
            dickDrawable.bounds = dickRect
            invalidate()
        }
        dickDrawable.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.concat(dickMatrix)
        dickDrawable.draw(canvas)
        canvas.restore()
    }


    var mScale = false

    fun scaleImage(scale: Float) {
        if (animRunning) {
            clearAnimation()
            this.animator?.end()
        }
        dickMatrix.reset()
        dickMatrix.setScale(scale, scale)
        dickMatrix.postTranslate(width * (1 - scale) / 2, height * (1 - scale))
        mScale = true
        invalidate()
    }

    private var animator: ValueAnimator? = null
    private var animRunning = false

    fun hideImage(currentScale: Float, animationRuning: (Float) -> Unit, animationEnd: () -> Unit) {
        if (mScale) {
            mScale = false
            animator = ValueAnimator.ofFloat(currentScale, 0f)
            animator?.duration = 8000
            var aV = 0f
            animator?.addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Float
                aV = animatedValue
                dickMatrix.reset()
                dickMatrix.postScale(animatedValue, animatedValue)
                dickMatrix.postTranslate(
                    width * (1 - animatedValue) / 2,
                    height * (1 - animatedValue)
                )
                invalidate()
            }
            animator?.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator) {
                    animationRuning.invoke(aV)
                    animRunning = true
                }

                override fun onAnimationEnd(p0: Animator) {
                    animRunning = false
                    if (!mScale) {
                        this@FireBlowView.invisibleExt()
                        animationEnd.invoke()
                    }
                }

                override fun onAnimationCancel(p0: Animator) {
                    animRunning = false

                }

                override fun onAnimationRepeat(p0: Animator) {
                }

            })
            if (!animRunning) {
                animator?.start()
            }
        }

    }
}