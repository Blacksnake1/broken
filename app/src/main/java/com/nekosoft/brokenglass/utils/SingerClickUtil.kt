package com.nekosoft.brokenglass.utils

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View


fun <T : View> T.click(action: (T) -> Unit) {
    setOnClickListener {
        action(this)
    }
}

//fun <T : View> T.singerClick(interval: Long = 500L, action: ((T) -> Unit)?) {
//    setOnClickListener(SingleClickListener(interval, action))
//}
//
//class SingleClickListener<T : View>(
//    private val interval: Long = 500L,
//    private var clickFunc: ((T) -> Unit)?
//) : View.OnClickListener {
//    private var lastClickTime = 0L
//
//    override fun onClick(v: View) {
//        val nowTime = System.currentTimeMillis()
//        if (nowTime - lastClickTime > interval) {
//            if (clickFunc != null) {
//                clickFunc!!(v as T)
//            }
//            lastClickTime = nowTime
//        }
//    }
//}
//
//private var mIsClickable = true
//
//class NonDoubleClickListener(
//    private val interval: Long,
//    private var clickFunc: (() -> Unit)?
//) : View.OnClickListener {
//    override fun onClick(v: View) {
//        if (mIsClickable) {
//            mIsClickable = false
//            Handler(Looper.getMainLooper()).postDelayed({
//                // Kích hoạt lại sự kiện click sau khi chờ xong
//                mIsClickable = true
//            }, interval)
//            clickFunc?.invoke()
//        }
//    }
//}
//
//fun View.clickSafe(interval: Long = 500L, action: (() -> Unit)?) {
//    setOnClickListener(NonDoubleClickListener(interval, action))
//}

var isAvailableClick = true
fun handleAvailableClick(time: Long) {
    Handler(Looper.getMainLooper()).postDelayed({
        isAvailableClick = true
    }, time)
}

fun View.clickSafe(time: Long = 200, action: () -> Unit) {
    this.setOnClickListener {
        if (isAvailableClick) {
            isAvailableClick = false
            handleAvailableClick(time)
            action.invoke()
        }
    }
}
@SuppressLint("ClickableViewAccessibility")
fun View.setOnTouchScale(
    scale: Float = 0.95f,
    disView: Boolean = true,
    time: Long = 200L,
    action: () -> Unit,
) {
    var isClick = true
    this.setOnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                isClick = true
                view.scaleX = scale
                view.scaleY = scale
            }

            MotionEvent.ACTION_MOVE -> {
                if (motionEvent.x < 0 || motionEvent.x > this.width || motionEvent.y < 0 || motionEvent.y > this.height) {
                    isClick = false
                    if (disView) {
                        view.scaleX = 1f
                        view.scaleY = 1f
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                if (isClick) {
                    if (isAvailableClick) {
                        isAvailableClick = false
                        handleAvailableClick(time)
                        action()
                    }
                }
                view.scaleX = 1f
                view.scaleY = 1f
            }

            MotionEvent.ACTION_CANCEL -> {
                view.scaleX = 1f
                view.scaleY = 1f
            }
        }
        true
    }
}