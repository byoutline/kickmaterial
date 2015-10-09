package com.byoutline.kickmaterial.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mount on 12/8/14.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CustomSharedElementTransition extends Transition {

    private static final String PROPERTY_TRANSLATION_X = "custom:translationX";
    private static final String PROPERTY_TRANSLATION_Z = "custom:translationZ";
    private static final String[] PROPERTIES = {PROPERTY_TRANSLATION_X, PROPERTY_TRANSLATION_Z};

    public CustomSharedElementTransition() {
    }

    public CustomSharedElementTransition(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public String[] getTransitionProperties() {
        return PROPERTIES;
    }

    private void captureValues(TransitionValues transitionValues) {
        float x = transitionValues.view.getTranslationX();
        float z = transitionValues.view.getTranslationZ();
        transitionValues.values.put(PROPERTY_TRANSLATION_X, x);
        transitionValues.values.put(PROPERTY_TRANSLATION_Z, z);
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        if (!propertiesExistsInValues(startValues, endValues)) {
            return null;
        }

        final float startX = (Float) startValues.values.get(PROPERTY_TRANSLATION_X);
        final float endX = (Float) endValues.values.get(PROPERTY_TRANSLATION_X);

        final float startZ = (Float) startValues.values.get(PROPERTY_TRANSLATION_Z);
        final float endZ = (Float) endValues.values.get(PROPERTY_TRANSLATION_Z);

        final View view = endValues.view;
        view.setTranslationX(startX);
        view.setTranslationZ(startZ);
//        return ObjectAnimator.ofFloat(view, View.TRANSLATION_Z, startZ, endZ);
        return ObjectAnimator.ofFloat(view, View.TRANSLATION_X, startX, endX);
    }

    private boolean propertiesExistsInValues(TransitionValues startValues, TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return false;
        }
        for (String property : PROPERTIES) {
            if (!startValues.values.containsKey(property) ||
                    !endValues.values.containsKey(property)) {
                return false;
            }
        }
        return true;
    }
}
