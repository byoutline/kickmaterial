package com.byoutline.kickmaterial.transitions

import android.animation.Animator
import android.animation.AnimatorSet
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.Transition
import android.transition.TransitionValues
import android.util.AttributeSet
import android.view.ViewGroup
import com.byoutline.kickmaterial.BuildConfig
import com.byoutline.kickmaterial.R

/**
 * Custom transition wrapper that delegates handling to [CircleTransition] for
 * FAB, and to [ChangeBounds] and [ChangeImageTransform] for other elements.

 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class SharedElementTransition(context: Context, attrs: AttributeSet) : Transition(context, attrs) {
    private val fabTransition: CircleTransition = CircleTransition(context, attrs)
    private val fabTransitionName: String = context.getString(R.string.transition_fab)
    private val imageTransition: ChangeImageTransform = ChangeImageTransform(context, attrs)
    private val defaultTransition: ChangeBounds = ChangeBounds(context, attrs)
    private val transitionProperties: Array<String> = arrayOf(*fabTransition.transitionProperties,
            *imageTransition.transitionProperties,
            *defaultTransition.transitionProperties)

    init {
        if (BuildConfig.DEBUG && TextUtils.isEmpty(fabTransitionName)) {
            throw AssertionError("Transition name should not be empty")
        }
    }

    override fun getTransitionProperties(): Array<String> = transitionProperties

    override fun captureStartValues(transitionValues: TransitionValues) {
        if (isFabTransition(transitionValues)) {
            fabTransition.captureStartValues(transitionValues)
        } else {
            defaultTransition.captureStartValues(transitionValues)
            imageTransition.captureStartValues(transitionValues)
        }
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        if (isFabTransition(transitionValues)) {
            fabTransition.captureEndValues(transitionValues)
        } else {
            defaultTransition.captureEndValues(transitionValues)
            imageTransition.captureStartValues(transitionValues)
        }
    }

    private fun isFabTransition(transitionValues: TransitionValues): Boolean {
        val view = transitionValues.view
        return fabTransitionName == view.transitionName
    }

    override fun createAnimator(sceneRoot: ViewGroup, startValues: TransitionValues, endValues: TransitionValues): Animator? {
        if (isFabTransition(endValues)) {
            return fabTransition.createAnimator(sceneRoot, startValues, endValues)
        } else {
            val imageAnimator = imageTransition.createAnimator(sceneRoot, startValues, endValues)
            val defaultAnimator = defaultTransition.createAnimator(sceneRoot, startValues, endValues)
            if (imageAnimator == null) {
                return defaultAnimator
            }
            if (defaultAnimator == null) {
                return imageAnimator
            }
            val set = AnimatorSet()
            set.playTogether(imageAnimator, defaultAnimator)
            return set
        }
    }
}
