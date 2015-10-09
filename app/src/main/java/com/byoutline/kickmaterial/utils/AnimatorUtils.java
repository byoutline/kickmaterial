package com.byoutline.kickmaterial.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class AnimatorUtils {
    public static AnimatorSet getScaleAnimator(View view, float startScale, float endScale) {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view, View.SCALE_X, startScale, endScale);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(view, View.SCALE_Y, startScale, endScale);
        set.playTogether(scaleXAnimator, scaleYAnimator);
        return set;
    }

    public static Animator getAlphaAnimator(View view) {
        return getAlphaAnimator(view, false);
    }

    public static Animator getAlphaAnimator(View view, boolean hideView) {
        float start = hideView ? 1 : 0;
        float end = hideView ? 0 : 1;
        view.setAlpha(start);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ALPHA, start, end);
        animator.setDuration(200);
        return animator;
    }
}
