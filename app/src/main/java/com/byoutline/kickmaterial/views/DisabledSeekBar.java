package com.byoutline.kickmaterial.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class DisabledSeekBar extends SeekBar {

    public DisabledSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DisabledSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DisabledSeekBar(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
