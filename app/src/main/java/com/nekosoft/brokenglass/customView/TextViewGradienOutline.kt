package com.nekosoft.crazynake.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.widget.TextView
import com.snake.screen.snake.`in`.hand.R

@SuppressLint("AppCompatCustomView")
class TextViewGradienOutline : TextView {
    companion object {
        const val DEFAULT_STROKE_WIDTH = 0f
    }

    private var gradientShader: LinearGradient? = null
    private var _fillColor = 0
    private var _strokeWidth = 0f
    private val gradientColors = intArrayOf(
        context.getColor(R.color.color_782716), //Start color
        context.getColor(R.color.color_c25100), //Start color
        context.getColor(R.color.color_28005) // End color
    )

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initView(attributeSet)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        attrs?.let {
            val obtain = context.obtainStyledAttributes(attrs, R.styleable.TextViewGradienOutline)
            _strokeWidth =
                obtain.getFloat(R.styleable.TextViewGradienOutline_gradientOutlineWidth, 0f)
            _fillColor =
                obtain.getColor(R.styleable.TextViewGradienOutline_gradientFillColor, R.color.black)
            obtain.recycle()
        } ?: kotlin.run {
            _fillColor = currentTextColor
            _strokeWidth = DEFAULT_STROKE_WIDTH
        }
        _strokeWidth = dpToPx(context, _strokeWidth)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        val p: Paint = paint
        val width = measuredWidth.toFloat()
        val height = (measuredHeight.toFloat() / 1.4).toFloat()
        if (gradientShader == null) {
            gradientShader = LinearGradient(
                0f, 0f, width, 0f,
                gradientColors,
                null,
                Shader.TileMode.CLAMP
            )
        }
        p.style = Paint.Style.STROKE
        p.strokeWidth = _strokeWidth
        p.shader = gradientShader
        //draw text stroke
//        canvas.drawText(text.toString().trim(), 0f, height, p)
        super.onDraw(canvas)
        val paint: Paint = this.paint
        paint.style = Paint.Style.FILL
        paint.color = _fillColor
        super.onDraw(canvas)

    }

    private fun dpToPx(context: Context, dp: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f)
    }

//    fun setTextStrokeColor(color: String) {
//        _strokeColor = Color.parseColor(color)
//        invalidate()
//    }
}