package com.nekosoft.brokenglass.customView

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

class FireWindView1 : SurfaceView, SurfaceHolder.Callback {
    private val bitmapList = mutableListOf<Bitmap>()
    private var currentBitmapIndex = 0
    private var paint: Paint = Paint()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    @SuppressLint("DiscouragedApi")
    private fun init() {
        if (holder != null) {
            holder.addCallback(this)
            setZOrderOnTop(true)
            holder.setFormat(-3)
        }
        paint.isAntiAlias = true
        for (i in 5..16) {
            bitmapList.add(
                BitmapFactory.decodeResource(
                    resources,
                    resources.getIdentifier("firewind$i", "drawable", context.packageName)
                )
            )
        }
        val defaultDisplay = (context as Activity).windowManager.defaultDisplay
        matrix.postTranslate(
            (defaultDisplay.width / 2 - bitmapList[0].width / 2).toFloat(),
            (defaultDisplay.height - bitmapList[0].height).toFloat()
        )
//        val fireHandler = SetRunnable(defaultDisplay)
//        fireHandler.sendEmptyMessageDelayed(0, 100)
        currentBitmapIndex = 0
    }

    override fun onDraw(canvas: Canvas) {
        // Vẽ ảnh hiện tại tại vị trí (x, y) trên canvas
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        canvas.drawBitmap(bitmapList[currentBitmapIndex], matrix, paint)

        holder.unlockCanvasAndPost(canvas)
    }

    private var isDrawing = false

    override fun surfaceCreated(p0: SurfaceHolder) {
        isDrawing = true
        Thread(SetRunnable()).start()
    }


    inner class SetRunnable : Runnable {
        override fun run() {
            // Tăng index để chuyển đến bitmap kế tiếp
            if (isDrawing) {
                val i: Int = currentBitmapIndex++
                currentBitmapIndex = i
                if (i >= 12) {
                    currentBitmapIndex = 0
                }
                // Tính toán góc tăng và alpha tương ứng
                var angleIncrement: Float = (70.0f - 45.0f) * 1.0f / 39.0f
                var alpha = 1f
                if (angleIncrement < 0.0f) {
                    angleIncrement = 0.0f
                }

                // Tính toán và thiết lập ma trận biến đổi
                val scale: Float = 7.0f * angleIncrement / 0.5f
                matrix.setScale(scale, scale)
                matrix.postTranslate(
                    (display.width / 2).toFloat() - bitmapList[0].getWidth().toFloat() * scale / 2.0f,
                    display.height.toFloat() - bitmapList[0].getHeight().toFloat() * scale
                )

                // Tính toán alpha và thiết lập alpha của Paint
                val alphaValue = angleIncrement * 255.0f
                if (alphaValue >= 45.0f) {
                    alpha = if (alphaValue > 215.0f) 255.0f else alphaValue + 255.0f - 215.0f
                }
                paint.setAlpha(alpha.toInt())

                // Vẽ lại view
                invalidate()
            }



        }

    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
//        w1.a = true;
        isDrawing = false
    }
}