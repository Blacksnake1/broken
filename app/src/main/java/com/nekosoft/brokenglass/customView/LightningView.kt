package com.nekosoft.brokenglass.customView

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.PaintCompat
import java.util.Random


class LightningView : View {
    private val paintBlur = Paint()
    private val paintSimple = Paint()
    private var v: ViewGroup? = null
    private var pos1x0 = 0F
    private var pos1y0 = 0F
    private var pos2x0: Float? = null
    private var pos2y0: Float? = null


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    fun setData(view: View?, x1: Float?, y1: Float?, x2: Float?, y2: Float?, fingercount: Boolean) {
        v = view as ViewGroup
        if (x1 != null && y1 != null) {
            pos1x0 = x1
            pos1y0 = y1
        }

        if (x2 != null && y2 != null) {
            pos2x0 = x2
            pos2y0 = y2
        }

        redraw()

    }

    private fun init() {
        paintSimple.isAntiAlias = true
        paintSimple.isDither = true
        paintSimple.color = Color.argb(248, 255, 255, 255)
        paintSimple.strokeWidth = 1f
        paintSimple.style = Paint.Style.STROKE
        paintSimple.strokeJoin = Paint.Join.ROUND
        paintSimple.strokeCap = Paint.Cap.ROUND
//        
        paintBlur.set(paintSimple)
        paintBlur.color = Color.argb(235, 74, 138, 255)
        paintBlur.strokeWidth = 10f
        paintBlur.maskFilter = BlurMaskFilter(15f, BlurMaskFilter.Blur.NORMAL)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (pos2x0 != null && pos2y0 != null) {
            if (pos2y0!! > pos1y0) {
                electTopStart(canvas, pos1x0, pos1y0)
                electTopEnd(canvas, pos1x0, pos1y0)
                electBottomEnd(canvas, pos2x0!!, pos2y0!!)
                electBottomStart(canvas, pos2x0!!, pos2y0!!)
            } else if (pos2y0!! < pos1y0) {
                electTopStart(canvas, pos2x0!!, pos2y0!!)
                electTopEnd(canvas, pos2x0!!, pos2y0!!)
                electBottomEnd(canvas, pos1x0, pos1y0)
                electBottomStart(canvas, pos1x0, pos1y0)
            }
            electCenter(canvas, pos1x0, pos1y0, pos2x0, pos2y0)
            fromPos1ToPos2(canvas, pos1x0, pos1y0, pos2x0!!, pos2y0!!)
            fromPos1ToPos2Second(canvas, pos1x0, pos1y0, pos2x0!!, pos2y0!!)
            fromPos1ToPos2Third(canvas, pos1x0, pos1y0, pos2x0!!, pos2y0!!)
            fromPos1ToPos2Fourth(canvas, pos1x0, pos1y0, pos2x0!!, pos2y0!!)
        } else if (pos2x0 == null && pos2y0 == null && pos1x0 != 0f && pos1y0 != 0f) {
            electTopStart(canvas, pos1x0, pos1y0)
            electTopEnd(canvas, pos1x0, pos1y0)
            electBottomEnd(canvas, pos1x0, pos1y0)
            electBottomStart(canvas, pos1x0, pos1y0)
            electCenter(canvas, pos1x0, pos1y0, null, null)
        }
    }


    private fun fromPos1ToPos2(
        canvas: Canvas,
        posAx: Float,
        posAy: Float,
        posBx: Float,
        posBy: Float,
    ) {
        val yAToYB =
            if (posBy > posAy) {
                posBy - posAy
            } else if (posBy < posAy) {
                posAy - posBy
            } else 5f

        val xAToXB =
            if (posBx > posAx) {
                posBx - posAx
            } else if (posBx < posAx) {
                posAx - posBx
            } else 5f

        val xx = if (xAToXB > 10f) {
            xAToXB / 5f
        } else {
            1f
        }
        val yy = if (yAToYB > 10f) {
            yAToYB / 5f
        } else {
            1f
        }
        if (yy < 1 || xx < 1) return

        val randX1 = Random().nextInt(xx.toInt()).toFloat() + 100
        val randX2 = Random().nextInt((xx.toInt() * 2) - xx.toInt()).toFloat() + xx + 100
        val randX3 =
            Random().nextInt((xx.toInt() * 3) - (xx.toInt() * 2)).toFloat() + (xx.toInt() * 2) + 100
        val randX4 =
            Random().nextInt((xx.toInt() * 4) - (xx.toInt() * 3)).toFloat() + (xx.toInt() * 3) + 100
        val randX5 =
            Random().nextInt((xx.toInt() * 5) - (xx.toInt() * 4)).toFloat() + (xx.toInt() * 4) + 100

        val randY1 = Random().nextInt(yy.toInt()).toFloat()
        val randY2 = Random().nextInt((yy.toInt() * 2) - yy.toInt()).toFloat() + yy
        val randY3 =
            Random().nextInt((yy.toInt() * 3) - (yy.toInt() * 2)).toFloat() + (yy.toInt() * 2)
        val randY4 =
            Random().nextInt((yy.toInt() * 4) - (yy.toInt() * 3)).toFloat() + (yy.toInt() * 3)
        val randY5 =
            Random().nextInt((yy.toInt() * 5) - (yy.toInt() * 4)).toFloat() + (yy.toInt() * 4)


        val pathC = Path()
        pathC.moveTo(posAx, posAy)
        if (posAx > posBx && posAy < posBy) {
            pathC.lineTo(posAx - randX1, posAy + randY1)
            pathC.lineTo(posAx - randX2, posAy + randY2)
            pathC.lineTo(posAx - randX3, posAy + randY3)
            pathC.lineTo(posAx - randX4, posAy + randY4)
            pathC.lineTo(posAx - randX5, posAy + randY5)

        } else if (posAx < posBx && posAy < posBy) {
            pathC.lineTo(posAx + randX1, posAy + randY1)
            pathC.lineTo(posAx + randX2, posAy + randY2)
            pathC.lineTo(posAx + randX3, posAy + randY3)
            pathC.lineTo(posAx + randX4, posAy + randY4)
            pathC.lineTo(posAx + randX5, posAy + randY5)

        } else if (posAx > posBx && posAy > posBy) {
            pathC.lineTo(posAx - randX1, posAy - randY1)
            pathC.lineTo(posAx - randX2, posAy - randY2)
            pathC.lineTo(posAx - randX3, posAy - randY3)
            pathC.lineTo(posAx - randX4, posAy - randY4)
            pathC.lineTo(posAx - randX5, posAy - randY5)

        } else if (posAx < posBx && posAy > posBy) {
            pathC.lineTo(posAx + randX1, posAy - randY1)
            pathC.lineTo(posAx + randX2, posAy - randY2)
            pathC.lineTo(posAx + randX3, posAy - randY3)
            pathC.lineTo(posAx + randX4, posAy - randY4)
            pathC.lineTo(posAx + randX5, posAy - randY5)

        }

        pathC.lineTo(posBx, posBy)
        canvas.drawPath(pathC, paintSimple)
        canvas.drawPath(pathC, paintBlur)

    }

    private fun fromPos1ToPos2Second(
        canvas: Canvas,
        posAx: Float,
        posAy: Float,
        posBx: Float,
        posBy: Float,
    ) {
        val yAToYB =
            if (posBy > posAy) {
                posBy - posAy
            } else if (posBy < posAy) {
                posAy - posBy
            } else 3f

        val xAToXB =
            if (posBx > posAx) {
                posBx - posAx
            } else if (posBx < posAx) {
                posAx - posBx
            } else 3f

        val xx = if (xAToXB > 3f) {
            xAToXB / 3f
        } else {
            1f
        }
        val yy = if (yAToYB > 3f) {
            yAToYB / 3f
        } else {
            1f
        }
        if (yy < 1 || xx < 1) return

        val randX1 = Random().nextInt(xx.toInt()).toFloat()
        val randX2 = Random().nextInt((xx.toInt() * 2) - xx.toInt()).toFloat() + xx + 50
        val randX3 =
            Random().nextInt((xx.toInt() * 3) - (xx.toInt() * 2)).toFloat() + (xx.toInt() * 2) + 50
        val randX4 =
            Random().nextInt((xx.toInt() * 4) - (xx.toInt() * 3)).toFloat() + (xx.toInt() * 3) + 50


        val randY1 = Random().nextInt(yy.toInt()).toFloat()
        val randY2 = Random().nextInt((yy.toInt() * 2) - yy.toInt()).toFloat() + yy
        val randY3 =
            Random().nextInt((yy.toInt() * 3) - (yy.toInt() * 2)).toFloat() + (yy.toInt() * 2)
        val randY4 =
            Random().nextInt((yy.toInt() * 4) - (yy.toInt() * 3)).toFloat() + (yy.toInt() * 3)


        val pathC = Path()
        pathC.moveTo(posAx, posAy)
        if (posAx > posBx && posAy < posBy) {
            pathC.lineTo(posAx - randX1, posAy + randY1)
            pathC.lineTo(posAx - randX2, posAy + randY2)
            pathC.lineTo(posAx - randX3, posAy + randY3)
            pathC.lineTo(posAx - randX4, posAy + randY4)

        } else if (posAx < posBx && posAy < posBy) {
            pathC.lineTo(posAx + randX1, posAy + randY1)
            pathC.lineTo(posAx + randX2, posAy + randY2)
            pathC.lineTo(posAx + randX3, posAy + randY3)
            pathC.lineTo(posAx + randX4, posAy + randY4)

        } else if (posAx > posBx && posAy > posBy) {
            pathC.lineTo(posAx - randX1, posAy - randY1)
            pathC.lineTo(posAx - randX2, posAy - randY2)
            pathC.lineTo(posAx - randX3, posAy - randY3)
            pathC.lineTo(posAx - randX4, posAy - randY4)

        } else if (posAx < posBx && posAy > posBy) {
            pathC.lineTo(posAx + randX1, posAy - randY1)
            pathC.lineTo(posAx + randX2, posAy - randY2)
            pathC.lineTo(posAx + randX3, posAy - randY3)
            pathC.lineTo(posAx + randX4, posAy - randY4)

        }

        pathC.lineTo(posBx, posBy)
        canvas.drawPath(pathC, paintSimple)
        canvas.drawPath(pathC, paintBlur)

    }

    private fun fromPos1ToPos2Third(
        canvas: Canvas,
        posAx: Float,
        posAy: Float,
        posBx: Float,
        posBy: Float,
    ) {
        val yAToYB =
            if (posBy > posAy) {
                posBy - posAy
            } else if (posBy < posAy) {
                posAy - posBy
            } else 10f

        val xAToXB =
            if (posBx > posAx) {
                posBx - posAx
            } else if (posBx < posAx) {
                posAx - posBx
            } else 10f

        val xx = if (xAToXB > 10f) {
            xAToXB / 10f
        } else {
            1f
        }
        val yy = if (yAToYB > 10f) {
            yAToYB / 10f
        } else {
            1f
        }
        if (yy < 1 || xx < 1) return

        val randX1 = Random().nextInt(xx.toInt()).toFloat()
        val randX2 = Random().nextInt((xx.toInt() * 2) - xx.toInt()).toFloat() + xx - 20
        val randX3 =
            Random().nextInt((xx.toInt() * 3) - (xx.toInt() * 2)).toFloat() + (xx.toInt() * 2) - 20
        val randX4 =
            Random().nextInt((xx.toInt() * 4) - (xx.toInt() * 3)).toFloat() + (xx.toInt() * 3) - 20
        val randX5 =
            Random().nextInt((xx.toInt() * 5) - (xx.toInt() * 4)).toFloat() + (xx.toInt() * 4) - 20
        val randX6 =
            Random().nextInt((xx.toInt() * 6) - (xx.toInt() * 5)).toFloat() + (xx.toInt() * 5) - 20
        val randX7 =
            Random().nextInt((xx.toInt() * 7) - (xx.toInt() * 6)).toFloat() + (xx.toInt() * 6)
        val randX8 =
            Random().nextInt((xx.toInt() * 8) - (xx.toInt() * 7)).toFloat() + (xx.toInt() * 7)
        val randX9 =
            Random().nextInt((xx.toInt() * 9) - (xx.toInt() * 8)).toFloat() + (xx.toInt() * 8)

        val randY1 = Random().nextInt(yy.toInt()).toFloat()
        val randY2 = Random().nextInt((yy.toInt() * 2) - yy.toInt()).toFloat() + yy
        val randY3 =
            Random().nextInt((yy.toInt() * 3) - (yy.toInt() * 2)).toFloat() + (yy.toInt() * 2)
        val randY4 =
            Random().nextInt((yy.toInt() * 4) - (yy.toInt() * 3)).toFloat() + (yy.toInt() * 3)
        val randY5 =
            Random().nextInt((yy.toInt() * 5) - (yy.toInt() * 4)).toFloat() + (yy.toInt() * 4)
        val randY6 =
            Random().nextInt((yy.toInt() * 6) - (yy.toInt() * 5)).toFloat() + (yy.toInt() * 5)
        val randY7 =
            Random().nextInt((yy.toInt() * 7) - (yy.toInt() * 6)).toFloat() + (yy.toInt() * 6)
        val randY8 =
            Random().nextInt((yy.toInt() * 8) - (yy.toInt() * 7)).toFloat() + (yy.toInt() * 7)
        val randY9 =
            Random().nextInt((yy.toInt() * 9) - (yy.toInt() * 8)).toFloat() + (yy.toInt() * 8)

        val pathC = Path()
        pathC.moveTo(posAx, posAy)
        if (posAx > posBx && posAy < posBy) {
            pathC.lineTo(posAx - randX1, posAy + randY1)
            pathC.lineTo(posAx - randX2, posAy + randY2)
            pathC.lineTo(posAx - randX3, posAy + randY3)
            pathC.lineTo(posAx - randX4, posAy + randY4)
            pathC.lineTo(posAx - randX5, posAy + randY5)
            pathC.lineTo(posAx - randX6, posAy + randY6)
            pathC.lineTo(posAx - randX7, posAy + randY7)
            pathC.lineTo(posAx - randX8, posAy + randY8)
            pathC.lineTo(posAx - randX9, posAy + randY9)
        } else if (posAx < posBx && posAy < posBy) {
            pathC.lineTo(posAx + randX1, posAy + randY1)
            pathC.lineTo(posAx + randX2, posAy + randY2)
            pathC.lineTo(posAx + randX3, posAy + randY3)
            pathC.lineTo(posAx + randX4, posAy + randY4)
            pathC.lineTo(posAx + randX5, posAy + randY5)
            pathC.lineTo(posAx + randX6, posAy + randY6)
            pathC.lineTo(posAx + randX7, posAy + randY7)
            pathC.lineTo(posAx + randX8, posAy + randY8)
            pathC.lineTo(posAx + randX9, posAy + randY9)
        } else if (posAx > posBx && posAy > posBy) {
            pathC.lineTo(posAx - randX1, posAy - randY1)
            pathC.lineTo(posAx - randX2, posAy - randY2)
            pathC.lineTo(posAx - randX3, posAy - randY3)
            pathC.lineTo(posAx - randX4, posAy - randY4)
            pathC.lineTo(posAx - randX5, posAy - randY5)
            pathC.lineTo(posAx - randX6, posAy - randY6)
            pathC.lineTo(posAx - randX7, posAy - randY7)
            pathC.lineTo(posAx - randX8, posAy - randY8)
            pathC.lineTo(posAx - randX9, posAy - randY9)
        } else if (posAx < posBx && posAy > posBy) {
            pathC.lineTo(posAx + randX1, posAy - randY1)
            pathC.lineTo(posAx + randX2, posAy - randY2)
            pathC.lineTo(posAx + randX3, posAy - randY3)
            pathC.lineTo(posAx + randX4, posAy - randY4)
            pathC.lineTo(posAx + randX5, posAy - randY5)
            pathC.lineTo(posAx + randX6, posAy - randY6)
            pathC.lineTo(posAx + randX7, posAy - randY7)
            pathC.lineTo(posAx + randX8, posAy - randY8)
            pathC.lineTo(posAx + randX9, posAy - randY9)
        }

        pathC.lineTo(posBx, posBy)
        canvas.drawPath(pathC, paintSimple)
        canvas.drawPath(pathC, paintBlur)

    }

    private fun fromPos1ToPos2Fourth(
        canvas: Canvas,
        posAx: Float,
        posAy: Float,
        posBx: Float,
        posBy: Float,
    ) {
        val yAToYB =
            if (posBy > posAy) {
                posBy - posAy
            } else if (posBy < posAy) {
                posAy - posBy
            } else 10f

        val xAToXB =
            if (posBx > posAx) {
                posBx - posAx
            } else if (posBx < posAx) {
                posAx - posBx
            } else 10f

        val xx = if (xAToXB > 10f) {
            xAToXB / 10f
        } else {
            1f
        }
        val yy = if (yAToYB > 10f) {
            yAToYB / 10f
        } else {
            1f
        }
        if (yy < 1 || xx < 1) return

        val randX1 = Random().nextInt(xx.toInt()).toFloat()
        val randX2 = Random().nextInt((xx.toInt() * 2) - xx.toInt()).toFloat() + xx
        val randX3 =
            Random().nextInt((xx.toInt() * 3) - (xx.toInt() * 2)).toFloat() + (xx.toInt() * 2)
        val randX4 =
            Random().nextInt((xx.toInt() * 4) - (xx.toInt() * 3)).toFloat() + (xx.toInt() * 3)
        val randX5 =
            Random().nextInt((xx.toInt() * 5) - (xx.toInt() * 4)).toFloat() + (xx.toInt() * 4)
        val randX6 =
            Random().nextInt((xx.toInt() * 6) - (xx.toInt() * 5)).toFloat() + (xx.toInt() * 5)
        val randX7 =
            Random().nextInt((xx.toInt() * 7) - (xx.toInt() * 6)).toFloat() + (xx.toInt() * 6)
        val randX8 =
            Random().nextInt((xx.toInt() * 8) - (xx.toInt() * 7)).toFloat() + (xx.toInt() * 7)
        val randX9 =
            Random().nextInt((xx.toInt() * 9) - (xx.toInt() * 8)).toFloat() + (xx.toInt() * 8)

        val randY1 = Random().nextInt(yy.toInt()).toFloat()
        val randY2 = Random().nextInt((yy.toInt() * 2) - yy.toInt()).toFloat() + yy
        val randY3 =
            Random().nextInt((yy.toInt() * 3) - (yy.toInt() * 2)).toFloat() + (yy.toInt() * 2)
        val randY4 =
            Random().nextInt((yy.toInt() * 4) - (yy.toInt() * 3)).toFloat() + (yy.toInt() * 3)
        val randY5 =
            Random().nextInt((yy.toInt() * 5) - (yy.toInt() * 4)).toFloat() + (yy.toInt() * 4)
        val randY6 =
            Random().nextInt((yy.toInt() * 6) - (yy.toInt() * 5)).toFloat() + (yy.toInt() * 5)
        val randY7 =
            Random().nextInt((yy.toInt() * 7) - (yy.toInt() * 6)).toFloat() + (yy.toInt() * 6)
        val randY8 =
            Random().nextInt((yy.toInt() * 8) - (yy.toInt() * 7)).toFloat() + (yy.toInt() * 7)
        val randY9 =
            Random().nextInt((yy.toInt() * 9) - (yy.toInt() * 8)).toFloat() + (yy.toInt() * 8)

        val pathC = Path()
        pathC.moveTo(posAx, posAy)
        if (posAx > posBx && posAy < posBy) {
            pathC.lineTo(posAx - randX1, posAy + randY1)
            pathC.lineTo(posAx - randX2, posAy + randY2)
            pathC.lineTo(posAx - randX3, posAy + randY3)
            pathC.lineTo(posAx - randX4, posAy + randY4)
            pathC.lineTo(posAx - randX5, posAy + randY5)
            pathC.lineTo(posAx - randX6, posAy + randY6)
            pathC.lineTo(posAx - randX7, posAy + randY7)
            pathC.lineTo(posAx - randX8, posAy + randY8)
            pathC.lineTo(posAx - randX9, posAy + randY9)
        } else if (posAx < posBx && posAy < posBy) {
            pathC.lineTo(posAx + randX1, posAy + randY1)
            pathC.lineTo(posAx + randX2, posAy + randY2)
            pathC.lineTo(posAx + randX3, posAy + randY3)
            pathC.lineTo(posAx + randX4, posAy + randY4)
            pathC.lineTo(posAx + randX5, posAy + randY5)
            pathC.lineTo(posAx + randX6, posAy + randY6)
            pathC.lineTo(posAx + randX7, posAy + randY7)
            pathC.lineTo(posAx + randX8, posAy + randY8)
            pathC.lineTo(posAx + randX9, posAy + randY9)
        } else if (posAx > posBx && posAy > posBy) {
            pathC.lineTo(posAx - randX1, posAy - randY1)
            pathC.lineTo(posAx - randX2, posAy - randY2)
            pathC.lineTo(posAx - randX3, posAy - randY3)
            pathC.lineTo(posAx - randX4, posAy - randY4)
            pathC.lineTo(posAx - randX5, posAy - randY5)
            pathC.lineTo(posAx - randX6, posAy - randY6)
            pathC.lineTo(posAx - randX7, posAy - randY7)
            pathC.lineTo(posAx - randX8, posAy - randY8)
            pathC.lineTo(posAx - randX9, posAy - randY9)
        } else if (posAx < posBx && posAy > posBy) {
            pathC.lineTo(posAx + randX1, posAy - randY1)
            pathC.lineTo(posAx + randX2, posAy - randY2)
            pathC.lineTo(posAx + randX3, posAy - randY3)
            pathC.lineTo(posAx + randX4, posAy - randY4)
            pathC.lineTo(posAx + randX5, posAy - randY5)
            pathC.lineTo(posAx + randX6, posAy - randY6)
            pathC.lineTo(posAx + randX7, posAy - randY7)
            pathC.lineTo(posAx + randX8, posAy - randY8)
            pathC.lineTo(posAx + randX9, posAy - randY9)
        }

        pathC.lineTo(posBx, posBy)
        canvas.drawPath(pathC, paintSimple)
        canvas.drawPath(pathC, paintBlur)

    }

    private fun electCenter(canvas: Canvas, xA: Float, yA: Float, xB: Float?, yB: Float?) {
        val randX1 = Random().nextInt(100).toFloat()
        val randX2 = Random().nextInt(100).toFloat()
        val randX3 = Random().nextInt(100).toFloat()
        val randX4 = Random().nextInt(100).toFloat()
        val randX5 = Random().nextInt(100).toFloat()
        val randX6 = Random().nextInt(100).toFloat()
        val randX7 = Random().nextInt(100).toFloat()
        val randX8 = Random().nextInt(100).toFloat()
        val randX9 = Random().nextInt(100).toFloat()
        val randY1 = Random().nextInt(100).toFloat()
        val randY2 = Random().nextInt(100).toFloat()
        val randY3 = Random().nextInt(100).toFloat()
        val randY4 = Random().nextInt(100).toFloat()
        val randY5 = Random().nextInt(100).toFloat()
        val randY6 = Random().nextInt(100).toFloat()
        val randY7 = Random().nextInt(100).toFloat()
        val randY8 = Random().nextInt(100).toFloat()
        val randY9 = Random().nextInt(100).toFloat()

        val pathC = Path()
        pathC.moveTo(xA, yA)
        pathC.lineTo(xA - randX1, yA + randY1)
        pathC.lineTo(xA - randX2, yA + randY2)
        pathC.lineTo(xA + randX3, yA + randY3)
        pathC.lineTo(xA + randX4, yA + randY4)
        pathC.lineTo(xA + randX5, yA - randY5)
        pathC.lineTo(xA + randX6, yA - randY6)
        pathC.lineTo(xA + randX7, yA - randY7)
        pathC.lineTo(xA - randX8, yA - randY8)
        pathC.lineTo(xA - randX9, yA - randX9)
        canvas.drawPath(pathC, paintSimple)
        canvas.drawPath(pathC, paintBlur)

//        /*tia 2*/
        val randXx1 = Random().nextInt(100).toFloat() + 50
        val randXx2 = Random().nextInt(100).toFloat() + 50
        val randXx3 = Random().nextInt(100).toFloat() + 50
        val randXx4 = Random().nextInt(100).toFloat() + 50
        val randXx5 = Random().nextInt(100).toFloat() + 50
        val randXx6 = Random().nextInt(100).toFloat() + 50
        val randXx7 = Random().nextInt(100).toFloat() + 50
        val randXx8 = Random().nextInt(100).toFloat() + 50
        val randXx9 = Random().nextInt(100).toFloat() + 50
        val randYy1 = Random().nextInt(100).toFloat() + 50
        val randYy2 = Random().nextInt(100).toFloat() + 50
        val randYy3 = Random().nextInt(100).toFloat() + 50
        val randYy4 = Random().nextInt(100).toFloat() + 50
        val randYy5 = Random().nextInt(100).toFloat() + 50
        val randYy6 = Random().nextInt(100).toFloat() + 50
        val randYy7 = Random().nextInt(100).toFloat() + 50
        val randYy8 = Random().nextInt(100).toFloat() + 50
        val randYy9 = Random().nextInt(100).toFloat() + 50
//
//        val pathC2 = Path()
        if (xB != null && yB != null) {
            val pathC2 = Path()
            pathC2.moveTo(xB, yB)
            pathC2.lineTo(xB - randX1, yB + randY1)
            pathC2.lineTo(xB - randX2, yB + randY2)
            pathC2.lineTo(xB + randX3, yB + randY3)
            pathC2.lineTo(xB + randX4, yB + randY4)
            pathC2.lineTo(xB + randX5, yB - randY5)
            pathC2.lineTo(xB + randX6, yB - randY6)
            pathC2.lineTo(xB + randX7, yB - randY7)
            pathC2.lineTo(xB - randX8, yB - randY8)
            pathC2.lineTo(xB - randX9, yB - randY9)
            canvas.drawPath(pathC2, paintSimple)
            canvas.drawPath(pathC2, paintBlur)
        }

    }

    private fun electTopStart(canvas: Canvas, x0: Float, y0: Float) {
        val yy = y0 / 10
        val xx = x0 / 10
        if (yy < 1 || xx < 1) return
        val randX1 = Random().nextInt(xx.toInt()).toFloat()
        val randX2 = Random().nextInt((xx.toInt() * 2) - xx.toInt()).toFloat() + xx
        val randX3 =
            Random().nextInt((xx.toInt() * 3) - (xx.toInt() * 2)).toFloat() + (xx.toInt() * 2)
        val randX4 =
            Random().nextInt((xx.toInt() * 4) - (xx.toInt() * 3)).toFloat() + (xx.toInt() * 3)
        val randX5 =
            Random().nextInt((xx.toInt() * 5) - (xx.toInt() * 4)).toFloat() + (xx.toInt() * 4)
        val randX6 =
            Random().nextInt((xx.toInt() * 6) - (xx.toInt() * 5)).toFloat() + (xx.toInt() * 5)
        val randX7 =
            Random().nextInt((xx.toInt() * 7) - (xx.toInt() * 6)).toFloat() + (xx.toInt() * 6)
        val randX8 =
            Random().nextInt((xx.toInt() * 8) - (xx.toInt() * 7)).toFloat() + (xx.toInt() * 7)
        val randX9 =
            Random().nextInt((xx.toInt() * 9) - (xx.toInt() * 8)).toFloat() + (xx.toInt() * 8)
        val randY1 = Random().nextInt(yy.toInt()).toFloat()
        val randY2 = Random().nextInt((yy.toInt() * 2) - yy.toInt()).toFloat() + yy
        val randY3 =
            Random().nextInt((yy.toInt() * 3) - (yy.toInt() * 2)).toFloat() + (yy.toInt() * 2)
        val randY4 =
            Random().nextInt((yy.toInt() * 4) - (yy.toInt() * 3)).toFloat() + (yy.toInt() * 3)
        val randY5 =
            Random().nextInt((yy.toInt() * 5) - (yy.toInt() * 4)).toFloat() + (yy.toInt() * 4)
        val randY6 =
            Random().nextInt((yy.toInt() * 6) - (yy.toInt() * 5)).toFloat() + (yy.toInt() * 5)
        val randY7 =
            Random().nextInt((yy.toInt() * 7) - (yy.toInt() * 6)).toFloat() + (yy.toInt() * 6)
        val randY8 =
            Random().nextInt((yy.toInt() * 8) - (yy.toInt() * 7)).toFloat() + (yy.toInt() * 7)
        val randY9 =
            Random().nextInt((yy.toInt() * 9) - (yy.toInt() * 8)).toFloat() + (yy.toInt() * 8)

        val path3 = Path()
        path3.moveTo(x0, y0)
        path3.lineTo(x0 - randX1, y0 - randY1)
        path3.lineTo(x0 - randX2, y0 - randY2)
        path3.lineTo(x0 - randX3, y0 - randY3)
        path3.lineTo(x0 - randX4, y0 - randY4)
        path3.lineTo(x0 - randX5, y0 - randY5)
        path3.lineTo(x0 - randX6, y0 - randY6)
        path3.lineTo(x0 - randX7, y0 - randY7)
        path3.lineTo(x0 - randX8, y0 - randY8)
        path3.lineTo(x0 - randX9, y0 - randY9)
        path3.lineTo(0f + randX1, 0f + randX1)
        canvas.drawPath(path3, paintSimple)
        canvas.drawPath(path3, paintBlur)

        /* tia 2*/
        val randXx1 = Random().nextInt(xx.toInt()).toFloat() + 10
        val randXx2 = Random().nextInt((xx.toInt() * 2) - xx.toInt()).toFloat() + xx + 10
        val randXx3 =
            Random().nextInt((xx.toInt() * 3) - (xx.toInt() * 2)).toFloat() + (xx.toInt() * 2) + 10
        val randXx4 =
            Random().nextInt((xx.toInt() * 4) - (xx.toInt() * 3)).toFloat() + (xx.toInt() * 3) + 10
        val randXx5 =
            Random().nextInt((xx.toInt() * 5) - (xx.toInt() * 4)).toFloat() + (xx.toInt() * 4) + 10
        val randXx6 =
            Random().nextInt((xx.toInt() * 6) - (xx.toInt() * 5)).toFloat() + (xx.toInt() * 5) + 10
        val randXx7 =
            Random().nextInt((xx.toInt() * 7) - (xx.toInt() * 6)).toFloat() + (xx.toInt() * 6) + 10
        val randXx8 =
            Random().nextInt((xx.toInt() * 8) - (xx.toInt() * 7)).toFloat() + (xx.toInt() * 7) + 10
        val randXx9 =
            Random().nextInt((xx.toInt() * 9) - (xx.toInt() * 8)).toFloat() + (xx.toInt() * 8) + 10
        val randYy1 = Random().nextInt(yy.toInt()).toFloat() + 10
        val randYy2 = Random().nextInt((yy.toInt() * 2) - yy.toInt()).toFloat() + yy + 10
        val randYy3 =
            Random().nextInt((yy.toInt() * 3) - (yy.toInt() * 2)).toFloat() + (yy.toInt() * 2) + 10
        val randYy4 =
            Random().nextInt((yy.toInt() * 4) - (yy.toInt() * 3)).toFloat() + (yy.toInt() * 3) + 10
        val randYy5 =
            Random().nextInt((yy.toInt() * 5) - (yy.toInt() * 4)).toFloat() + (yy.toInt() * 4) + 10
        val randYy6 =
            Random().nextInt((yy.toInt() * 6) - (yy.toInt() * 5)).toFloat() + (yy.toInt() * 5) + 10
        val randYy7 =
            Random().nextInt((yy.toInt() * 7) - (yy.toInt() * 6)).toFloat() + (yy.toInt() * 6) + 10
        val randYy8 =
            Random().nextInt((yy.toInt() * 8) - (yy.toInt() * 7)).toFloat() + (yy.toInt() * 7) + 10
        val randYy9 =
            Random().nextInt((yy.toInt() * 9) - (yy.toInt() * 8)).toFloat() + (yy.toInt() * 8) + 10

        val path32 = Path()
        path32.moveTo(x0, y0)
        path32.lineTo(x0 - randXx1, y0 - randYy1)
        path32.lineTo(x0 - randXx2, y0 - randYy2)
        path32.lineTo(x0 - randXx3, y0 - randYy3)
        path32.lineTo(x0 - randXx4, y0 - randYy4)
        path32.lineTo(x0 - randXx5, y0 - randYy5)
        path32.lineTo(x0 - randXx6, y0 - randYy6)
        path32.lineTo(x0 - randXx7, y0 - randYy7)
        path32.lineTo(x0 - randXx8, y0 - randYy8)
        path32.lineTo(x0 - randXx9, y0 - randYy9)
        path32.lineTo(0f + randXx1, 0f + randYy1)
        canvas.drawPath(path32, paintSimple)
        canvas.drawPath(path32, paintBlur)

    }

    private fun electTopEnd(canvas: Canvas, x0: Float, y0: Float) {
        val yy = (y0) / 10
        val xx = (v!!.width.toFloat() - x0) / 10
        if (yy < 1 || xx < 1) return
        val randX1 = Random().nextInt(xx.toInt()).toFloat()
        val randX2 = Random().nextInt((xx.toInt() * 2) - xx.toInt()).toFloat() + xx
        val randX3 =
            Random().nextInt((xx.toInt() * 3) - (xx.toInt() * 2)).toFloat() + (xx.toInt() * 2)
        val randX4 =
            Random().nextInt((xx.toInt() * 4) - (xx.toInt() * 3)).toFloat() + (xx.toInt() * 3)
        val randX5 =
            Random().nextInt((xx.toInt() * 5) - (xx.toInt() * 4)).toFloat() + (xx.toInt() * 4)
        val randX6 =
            Random().nextInt((xx.toInt() * 6) - (xx.toInt() * 5)).toFloat() + (xx.toInt() * 5)
        val randX7 =
            Random().nextInt((xx.toInt() * 7) - (xx.toInt() * 6)).toFloat() + (xx.toInt() * 6)
        val randX8 =
            Random().nextInt((xx.toInt() * 8) - (xx.toInt() * 7)).toFloat() + (xx.toInt() * 7)
        val randX9 =
            Random().nextInt((xx.toInt() * 9) - (xx.toInt() * 8)).toFloat() + (xx.toInt() * 8)
        val randY1 = Random().nextInt(yy.toInt()).toFloat()
        val randY2 = Random().nextInt((yy.toInt() * 2) - yy.toInt()).toFloat() + yy
        val randY3 =
            Random().nextInt((yy.toInt() * 3) - (yy.toInt() * 2)).toFloat() + (yy.toInt() * 2)
        val randY4 =
            Random().nextInt((yy.toInt() * 4) - (yy.toInt() * 3)).toFloat() + (yy.toInt() * 3)
        val randY5 =
            Random().nextInt((yy.toInt() * 5) - (yy.toInt() * 4)).toFloat() + (yy.toInt() * 4)
        val randY6 =
            Random().nextInt((yy.toInt() * 6) - (yy.toInt() * 5)).toFloat() + (yy.toInt() * 5)
        val randY7 =
            Random().nextInt((yy.toInt() * 7) - (yy.toInt() * 6)).toFloat() + (yy.toInt() * 6)
        val randY8 =
            Random().nextInt((yy.toInt() * 8) - (yy.toInt() * 7)).toFloat() + (yy.toInt() * 7)
        val randY9 =
            Random().nextInt((yy.toInt() * 9) - (yy.toInt() * 8)).toFloat() + (yy.toInt() * 8)

        val path4 = Path()
        path4.moveTo(x0, y0)
        path4.lineTo(x0 + randX1, y0 - randY1)
        path4.lineTo(x0 + randX2, y0 - randY2)
        path4.lineTo(x0 + randX3, y0 - randY3)
        path4.lineTo(x0 + randX4, y0 - randY4)
        path4.lineTo(x0 + randX5, y0 - randY5)
        path4.lineTo(x0 + randX6, y0 - randY6)
        path4.lineTo(x0 + randX7, y0 - randY7)
        path4.lineTo(x0 + randX8, y0 - randY8)
        path4.lineTo(x0 + randX9, y0 - randY9)
        path4.lineTo((v as ViewGroup).width.toFloat() - randX1, 0f + randX1)
        canvas.drawPath(path4, paintSimple)
        canvas.drawPath(path4, paintBlur)

        /* tia 2*/
        val randXx1 = Random().nextInt(xx.toInt()).toFloat() + 10
        val randXx2 = Random().nextInt((xx.toInt() * 2) - xx.toInt()).toFloat() + xx + 10
        val randXx3 =
            Random().nextInt((xx.toInt() * 3) - (xx.toInt() * 2)).toFloat() + (xx.toInt() * 2) + 10
        val randXx4 =
            Random().nextInt((xx.toInt() * 4) - (xx.toInt() * 3)).toFloat() + (xx.toInt() * 3) + 10
        val randXx5 =
            Random().nextInt((xx.toInt() * 5) - (xx.toInt() * 4)).toFloat() + (xx.toInt() * 4) + 10
        val randXx6 =
            Random().nextInt((xx.toInt() * 6) - (xx.toInt() * 5)).toFloat() + (xx.toInt() * 5) + 10
        val randXx7 =
            Random().nextInt((xx.toInt() * 7) - (xx.toInt() * 6)).toFloat() + (xx.toInt() * 6) + 10
        val randXx8 =
            Random().nextInt((xx.toInt() * 8) - (xx.toInt() * 7)).toFloat() + (xx.toInt() * 7) + 10
        val randXx9 =
            Random().nextInt((xx.toInt() * 9) - (xx.toInt() * 8)).toFloat() + (xx.toInt() * 8) + 10
        val randYy1 = Random().nextInt(yy.toInt()).toFloat() + 10
        val randYy2 = Random().nextInt((yy.toInt() * 2) - yy.toInt()).toFloat() + yy + 10
        val randYy3 =
            Random().nextInt((yy.toInt() * 3) - (yy.toInt() * 2)).toFloat() + (yy.toInt() * 2) + 10
        val randYy4 =
            Random().nextInt((yy.toInt() * 4) - (yy.toInt() * 3)).toFloat() + (yy.toInt() * 3) + 10
        val randYy5 =
            Random().nextInt((yy.toInt() * 5) - (yy.toInt() * 4)).toFloat() + (yy.toInt() * 4) + 10
        val randYy6 =
            Random().nextInt((yy.toInt() * 6) - (yy.toInt() * 5)).toFloat() + (yy.toInt() * 5) + 10
        val randYy7 =
            Random().nextInt((yy.toInt() * 7) - (yy.toInt() * 6)).toFloat() + (yy.toInt() * 6) + 10
        val randYy8 =
            Random().nextInt((yy.toInt() * 8) - (yy.toInt() * 7)).toFloat() + (yy.toInt() * 7) + 10
        val randYy9 =
            Random().nextInt((yy.toInt() * 9) - (yy.toInt() * 8)).toFloat() + (yy.toInt() * 8) + 10

        val path42 = Path()
        path42.moveTo(x0, y0)
        path42.lineTo(x0 + randXx1, y0 - randYy1)
        path42.lineTo(x0 + randXx2, y0 - randYy2)
        path42.lineTo(x0 + randXx3, y0 - randYy3)
        path42.lineTo(x0 + randXx4, y0 - randYy4)
        path42.lineTo(x0 + randXx5, y0 - randYy5)
        path42.lineTo(x0 + randXx6, y0 - randYy6)
        path42.lineTo(x0 + randXx7, y0 - randYy7)
        path42.lineTo(x0 + randXx8, y0 - randYy8)
        path42.lineTo(x0 + randXx9, y0 - randYy9)
        path42.lineTo((v as ViewGroup).width.toFloat() - randXx1, 0f + randXx1)
        canvas.drawPath(path42, paintSimple)
        canvas.drawPath(path42, paintBlur)
    }

    private fun electBottomEnd(canvas: Canvas, x0: Float, y0: Float) {
        val yy = (v!!.height.toFloat() - y0) / 10
        val xx = (v!!.width.toFloat() - x0) / 10
        if (yy < 1 || xx < 1) return
        val randX1 = Random().nextInt(xx.toInt()).toFloat()
        val randX2 = Random().nextInt((xx.toInt() * 2) - xx.toInt()).toFloat() + xx
        val randX3 =
            Random().nextInt((xx.toInt() * 3) - (xx.toInt() * 2)).toFloat() + (xx.toInt() * 2)
        val randX4 =
            Random().nextInt((xx.toInt() * 4) - (xx.toInt() * 3)).toFloat() + (xx.toInt() * 3)
        val randX5 =
            Random().nextInt((xx.toInt() * 5) - (xx.toInt() * 4)).toFloat() + (xx.toInt() * 4)
        val randX6 =
            Random().nextInt((xx.toInt() * 6) - (xx.toInt() * 5)).toFloat() + (xx.toInt() * 5)
        val randX7 =
            Random().nextInt((xx.toInt() * 7) - (xx.toInt() * 6)).toFloat() + (xx.toInt() * 6)
        val randX8 =
            Random().nextInt((xx.toInt() * 8) - (xx.toInt() * 7)).toFloat() + (xx.toInt() * 7)
        val randX9 =
            Random().nextInt((xx.toInt() * 9) - (xx.toInt() * 8)).toFloat() + (xx.toInt() * 8)
        val randY1 = Random().nextInt(yy.toInt()).toFloat()
        val randY2 = Random().nextInt((yy.toInt() * 2) - yy.toInt()).toFloat() + yy
        val randY3 =
            Random().nextInt((yy.toInt() * 3) - (yy.toInt() * 2)).toFloat() + (yy.toInt() * 2)
        val randY4 =
            Random().nextInt((yy.toInt() * 4) - (yy.toInt() * 3)).toFloat() + (yy.toInt() * 3)
        val randY5 =
            Random().nextInt((yy.toInt() * 5) - (yy.toInt() * 4)).toFloat() + (yy.toInt() * 4)
        val randY6 =
            Random().nextInt((yy.toInt() * 6) - (yy.toInt() * 5)).toFloat() + (yy.toInt() * 5)
        val randY7 =
            Random().nextInt((yy.toInt() * 7) - (yy.toInt() * 6)).toFloat() + (yy.toInt() * 6)
        val randY8 =
            Random().nextInt((yy.toInt() * 8) - (yy.toInt() * 7)).toFloat() + (yy.toInt() * 7)
        val randY9 =
            Random().nextInt((yy.toInt() * 9) - (yy.toInt() * 8)).toFloat() + (yy.toInt() * 8)

        val path1 = Path()
        path1.moveTo(x0, y0)
        path1.lineTo(x0 + randX1, y0 + randY1)
        path1.lineTo(x0 + randX2, y0 + randY2)
        path1.lineTo(x0 + randX3, y0 + randY3)
        path1.lineTo(x0 + randX4, y0 + randY4)
        path1.lineTo(x0 + randX5, y0 + randY5)
        path1.lineTo(x0 + randX6, y0 + randY6)
        path1.lineTo(x0 + randX7, y0 + randY7)
        path1.lineTo(x0 + randX8, y0 + randY8)
        path1.lineTo(x0 + randX9, y0 + randY9)
        path1.lineTo(
            (v as ViewGroup).width.toFloat() - randX1,
            (v as ViewGroup).height.toFloat() - randY1
        )
        canvas.drawPath(path1, paintSimple)
        canvas.drawPath(path1, paintBlur)

        /* tia 2*/
        val randXx1 = Random().nextInt(xx.toInt()).toFloat() + 10
        val randXx2 = Random().nextInt((xx.toInt() * 2) - xx.toInt()).toFloat() + xx + 10
        val randXx3 =
            Random().nextInt((xx.toInt() * 3) - (xx.toInt() * 2)).toFloat() + (xx.toInt() * 2) + 10
        val randXx4 =
            Random().nextInt((xx.toInt() * 4) - (xx.toInt() * 3)).toFloat() + (xx.toInt() * 3) + 10
        val randXx5 =
            Random().nextInt((xx.toInt() * 5) - (xx.toInt() * 4)).toFloat() + (xx.toInt() * 4) + 10
        val randXx6 =
            Random().nextInt((xx.toInt() * 6) - (xx.toInt() * 5)).toFloat() + (xx.toInt() * 5) + 10
        val randXx7 =
            Random().nextInt((xx.toInt() * 7) - (xx.toInt() * 6)).toFloat() + (xx.toInt() * 6) + 10
        val randXx8 =
            Random().nextInt((xx.toInt() * 8) - (xx.toInt() * 7)).toFloat() + (xx.toInt() * 7) + 10
        val randXx9 =
            Random().nextInt((xx.toInt() * 9) - (xx.toInt() * 8)).toFloat() + (xx.toInt() * 8) + 10
        val randYy1 = Random().nextInt(yy.toInt()).toFloat() + 10
        val randYy2 = Random().nextInt((yy.toInt() * 2) - yy.toInt()).toFloat() + yy + 10
        val randYy3 =
            Random().nextInt((yy.toInt() * 3) - (yy.toInt() * 2)).toFloat() + (yy.toInt() * 2) + 10
        val randYy4 =
            Random().nextInt((yy.toInt() * 4) - (yy.toInt() * 3)).toFloat() + (yy.toInt() * 3) + 10
        val randYy5 =
            Random().nextInt((yy.toInt() * 5) - (yy.toInt() * 4)).toFloat() + (yy.toInt() * 4) + 10
        val randYy6 =
            Random().nextInt((yy.toInt() * 6) - (yy.toInt() * 5)).toFloat() + (yy.toInt() * 5) + 10
        val randYy7 =
            Random().nextInt((yy.toInt() * 7) - (yy.toInt() * 6)).toFloat() + (yy.toInt() * 6) + 10
        val randYy8 =
            Random().nextInt((yy.toInt() * 8) - (yy.toInt() * 7)).toFloat() + (yy.toInt() * 7) + 10
        val randYy9 =
            Random().nextInt((yy.toInt() * 9) - (yy.toInt() * 8)).toFloat() + (yy.toInt() * 8) + 10

        val path12 = Path()
        path12.moveTo(x0, y0)
        path12.lineTo(x0 + randXx1, y0 + randYy1)
        path12.lineTo(x0 + randXx2, y0 + randYy2)
        path12.lineTo(x0 + randXx3, y0 + randYy3)
        path12.lineTo(x0 + randXx4, y0 + randYy4)
        path12.lineTo(x0 + randXx5, y0 + randYy5)
        path12.lineTo(x0 + randXx6, y0 + randYy6)
        path12.lineTo(x0 + randXx7, y0 + randYy7)
        path12.lineTo(x0 + randXx8, y0 + randYy8)
        path12.lineTo(x0 + randXx9, y0 + randYy9)
        path12.lineTo(
            (v as ViewGroup).width.toFloat() - randXx1,
            (v as ViewGroup).height.toFloat() - randYy1
        )
        canvas.drawPath(path12, paintSimple)
        canvas.drawPath(path12, paintBlur)

    }

    private fun electBottomStart(canvas: Canvas, x0: Float, y0: Float) {
        val yy = (v!!.height.toFloat() - y0) / 10
        val xx = x0 / 10
        if (yy < 1 || xx < 1) return
        val randX1 = Random().nextInt(xx.toInt()).toFloat()
        val randX2 = Random().nextInt((xx.toInt() * 2) - xx.toInt()).toFloat() + xx
        val randX3 =
            Random().nextInt((xx.toInt() * 3) - (xx.toInt() * 2)).toFloat() + (xx.toInt() * 2)
        val randX4 =
            Random().nextInt((xx.toInt() * 4) - (xx.toInt() * 3)).toFloat() + (xx.toInt() * 3)
        val randX5 =
            Random().nextInt((xx.toInt() * 5) - (xx.toInt() * 4)).toFloat() + (xx.toInt() * 4)
        val randX6 =
            Random().nextInt((xx.toInt() * 6) - (xx.toInt() * 5)).toFloat() + (xx.toInt() * 5)
        val randX7 =
            Random().nextInt((xx.toInt() * 7) - (xx.toInt() * 6)).toFloat() + (xx.toInt() * 6)
        val randX8 =
            Random().nextInt((xx.toInt() * 8) - (xx.toInt() * 7)).toFloat() + (xx.toInt() * 7)
        val randX9 =
            Random().nextInt((xx.toInt() * 9) - (xx.toInt() * 8)).toFloat() + (xx.toInt() * 8)
        val randY1 = Random().nextInt(yy.toInt()).toFloat()
        val randY2 = Random().nextInt((yy.toInt() * 2) - yy.toInt()).toFloat() + yy
        val randY3 =
            Random().nextInt((yy.toInt() * 3) - (yy.toInt() * 2)).toFloat() + (yy.toInt() * 2)
        val randY4 =
            Random().nextInt((yy.toInt() * 4) - (yy.toInt() * 3)).toFloat() + (yy.toInt() * 3)
        val randY5 =
            Random().nextInt((yy.toInt() * 5) - (yy.toInt() * 4)).toFloat() + (yy.toInt() * 4)
        val randY6 =
            Random().nextInt((yy.toInt() * 6) - (yy.toInt() * 5)).toFloat() + (yy.toInt() * 5)
        val randY7 =
            Random().nextInt((yy.toInt() * 7) - (yy.toInt() * 6)).toFloat() + (yy.toInt() * 6)
        val randY8 =
            Random().nextInt((yy.toInt() * 8) - (yy.toInt() * 7)).toFloat() + (yy.toInt() * 7)
        val randY9 =
            Random().nextInt((yy.toInt() * 9) - (yy.toInt() * 8)).toFloat() + (yy.toInt() * 8)

        val path2 = Path()
        path2.moveTo(x0, y0)
        path2.lineTo(x0 - randX1, y0 + randY1)
        path2.lineTo(x0 - randX2, y0 + randY2)
        path2.lineTo(x0 - randX3, y0 + randY3)
        path2.lineTo(x0 - randX4, y0 + randY4)
        path2.lineTo(x0 - randX5, y0 + randY5)
        path2.lineTo(x0 - randX6, y0 + randY6)
        path2.lineTo(x0 - randX7, y0 + randY7)
        path2.lineTo(x0 - randX8, y0 + randY8)
        path2.lineTo(x0 - randX9, y0 + randY9)
        path2.lineTo(0f + randX1, (v as ViewGroup).height.toFloat() - randX1)
        canvas.drawPath(path2, paintSimple)
        canvas.drawPath(path2, paintBlur)


        /* tia 2*/
        val randXx1 = Random().nextInt(xx.toInt()).toFloat() + 10
        val randXx2 = Random().nextInt((xx.toInt() * 2) - xx.toInt()).toFloat() + xx + 10
        val randXx3 =
            Random().nextInt((xx.toInt() * 3) - (xx.toInt() * 2)).toFloat() + (xx.toInt() * 2) + 10
        val randXx4 =
            Random().nextInt((xx.toInt() * 4) - (xx.toInt() * 3)).toFloat() + (xx.toInt() * 3) + 10
        val randXx5 =
            Random().nextInt((xx.toInt() * 5) - (xx.toInt() * 4)).toFloat() + (xx.toInt() * 4) + 10
        val randXx6 =
            Random().nextInt((xx.toInt() * 6) - (xx.toInt() * 5)).toFloat() + (xx.toInt() * 5) + 10
        val randXx7 =
            Random().nextInt((xx.toInt() * 7) - (xx.toInt() * 6)).toFloat() + (xx.toInt() * 6) + 10
        val randXx8 =
            Random().nextInt((xx.toInt() * 8) - (xx.toInt() * 7)).toFloat() + (xx.toInt() * 7) + 10
        val randXx9 =
            Random().nextInt((xx.toInt() * 9) - (xx.toInt() * 8)).toFloat() + (xx.toInt() * 8) + 10
        val randYy1 = Random().nextInt(yy.toInt()).toFloat() + 10
        val randYy2 = Random().nextInt((yy.toInt() * 2) - yy.toInt()).toFloat() + yy + 10
        val randYy3 =
            Random().nextInt((yy.toInt() * 3) - (yy.toInt() * 2)).toFloat() + (yy.toInt() * 2) + 10
        val randYy4 =
            Random().nextInt((yy.toInt() * 4) - (yy.toInt() * 3)).toFloat() + (yy.toInt() * 3) + 10
        val randYy5 =
            Random().nextInt((yy.toInt() * 5) - (yy.toInt() * 4)).toFloat() + (yy.toInt() * 4) + 10
        val randYy6 =
            Random().nextInt((yy.toInt() * 6) - (yy.toInt() * 5)).toFloat() + (yy.toInt() * 5) + 10
        val randYy7 =
            Random().nextInt((yy.toInt() * 7) - (yy.toInt() * 6)).toFloat() + (yy.toInt() * 6) + 10
        val randYy8 =
            Random().nextInt((yy.toInt() * 8) - (yy.toInt() * 7)).toFloat() + (yy.toInt() * 7) + 10
        val randYy9 =
            Random().nextInt((yy.toInt() * 9) - (yy.toInt() * 8)).toFloat() + (yy.toInt() * 8) + 10

        val path22 = Path()
        path22.moveTo(x0, y0)
        path22.lineTo(x0 - randXx1, y0 + randYy1)
        path22.lineTo(x0 - randXx2, y0 + randYy2)
        path22.lineTo(x0 - randXx3, y0 + randYy3)
        path22.lineTo(x0 - randXx4, y0 + randYy4)
        path22.lineTo(x0 - randXx5, y0 + randYy5)
        path22.lineTo(x0 - randXx6, y0 + randYy6)
        path22.lineTo(x0 - randXx7, y0 + randYy7)
        path22.lineTo(x0 - randXx8, y0 + randYy8)
        path22.lineTo(x0 - randXx9, y0 + randYy9)
        path22.lineTo(0f + randXx1, (v as ViewGroup).height.toFloat() - randYy1)
        canvas.drawPath(path22, paintSimple)
        canvas.drawPath(path22, paintBlur)
    }

    fun redraw(): Boolean {
        this.invalidate()
        return true
    }
}
