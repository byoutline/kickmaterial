package com.byoutline.kickmaterial.utils

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.transition.Transition
import android.transition.TransitionValues
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

/**
 * Created by mount on 12/8/14.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class CustomSharedElementTransition : Transition {

    constructor() {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun getTransitionProperties(): Array<String> {
        return PROPERTIES
    }

    private fun captureValues(transitionValues: TransitionValues) {
        val x = transitionValues.view.translationX
        val z = transitionValues.view.translationZ
        transitionValues.values.put(PROPERTY_TRANSLATION_X, x)
        transitionValues.values.put(PROPERTY_TRANSLATION_Z, z)
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun createAnimator(sceneRoot: ViewGroup, startValues: TransitionValues, endValues: TransitionValues): Animator? {
        if (!propertiesExistsInValues(startValues, endValues)) {
            return null
        }

        val startX = startValues.values[PROPERTY_TRANSLATION_X] as Float
        val endX = endValues.values[PROPERTY_TRANSLATION_X] as Float

        val startZ = startValues.values[PROPERTY_TRANSLATION_Z] as Float
        val endZ = endValues.values[PROPERTY_TRANSLATION_Z] as Float

        val view = endValues.view
        view.translationX = startX
        view.translationZ = startZ
        //        return ObjectAnimator.ofFloat(view, View.TRANSLATION_Z, startZ, endZ);
        return ObjectAnimator.ofFloat(view, View.TRANSLATION_X, startX, endX)
    }

    private fun propertiesExistsInValues(startValues: TransitionValues?, endValues: TransitionValues?): Boolean {
        if (startValues == null || endValues == null) {
            return false
        }
        for (property in PROPERTIES) {
            if (!startValues.values.containsKey(property) || !endValues.values.containsKey(property)) {
                return false
            }
        }
        return true
    }

    companion object {

        private val PROPERTY_TRANSLATION_X = "custom:translationX"
        private val PROPERTY_TRANSLATION_Z = "custom:translationZ"
        private val PROPERTIES = arrayOf(PROPERTY_TRANSLATION_X, PROPERTY_TRANSLATION_Z)
    }
}
