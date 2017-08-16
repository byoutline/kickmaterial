package com.byoutline.kickmaterial.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.SeekBar

class DisabledSeekBar : SeekBar {

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context) : super(context) {}

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return true
    }
}
