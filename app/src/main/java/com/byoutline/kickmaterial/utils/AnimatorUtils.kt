package com.byoutline.kickmaterial.utils

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
object AnimatorUtils {
    fun getScaleAnimator(view: View, startScale: Float, endScale: Float): AnimatorSet {
        val set = AnimatorSet()
        val scaleXAnimator = ObjectAnimator.ofFloat(view, View.SCALE_X, startScale, endScale)
        val scaleYAnimator = ObjectAnimator.ofFloat(view, View.SCALE_Y, startScale, endScale)
        set.playTogether(scaleXAnimator, scaleYAnimator)
        return set
    }

    @JvmOverloads fun getAlphaAnimator(view: View, hideView: Boolean = false): Animator {
        val start = (if (hideView) 1 else 0).toFloat()
        val end = (if (hideView) 0 else 1).toFloat()
        view.alpha = start
        val animator = ObjectAnimator.ofFloat(view, View.ALPHA, start, end)
        animator.duration = 200
        return animator
    }
}
