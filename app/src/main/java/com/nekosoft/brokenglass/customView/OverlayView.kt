package com.nekosoft.brokenglass.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.nekosoft.brokenglass.data.local.AppPreference

class OverlayView : RelativeLayout {
    private var mLoadColor: Int? = null

    constructor(context: Context) : super(context) {
        getField()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        getField()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        getField()
    }


    private fun getField() {
//        mLoadColor = Color.argb(255 ,255, 255, 255)
//        setWillNotDraw(false)

    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        mLoadColor?.let { canvas.drawColor(it) }
    }

    fun redraw(): Boolean {
        this.invalidate()
        return true
    }

}

