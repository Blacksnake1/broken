package com.nekosoft.brokenglass.customView

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.nekosoft.brokenglass.data.local.AppPreference
import com.nekosoft.brokenglass.data.model.EffectBrokenModel
import com.nekosoft.brokenglass.data.model.ScreenModel
import com.nekosoft.brokenglass.utils.ConstantsApp

class BrokenView : AppCompatImageView {
    private var sharedPreferences: AppPreference = AppPreference(context)
    private var screenModel: EffectBrokenModel? = null


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        screenModel = sharedPreferences.getObjectFromSharePreference(ConstantsApp.EFFECT)
        scaleType = ScaleType.CENTER_CROP
        Glide.with(this).load(screenModel?.image!!).into(this)
        setWillNotDraw(false)
    }


}