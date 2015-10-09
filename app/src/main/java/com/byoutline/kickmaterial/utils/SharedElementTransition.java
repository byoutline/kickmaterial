package com.byoutline.kickmaterial.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.byoutline.kickmaterial.BuildConfig;
import com.byoutline.kickmaterial.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Custom transition wrapper that delegates handling to {@link CircleTransition} for
 * FAB, and to {@link ChangeBounds} and {@link ChangeImageTransform} for other elements.
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class SharedElementTransition extends Transition {
    private final CircleTransition fabTransition;
    private final String fabTransitionName;
    private final ChangeImageTransform imageTransition;
    private final ChangeBounds defaultTransition;
    private final String[] transitionProperties;

    public SharedElementTransition(Context context, AttributeSet attrs) {
        super(context, attrs);
        fabTransition = new CircleTransition(context, attrs);
        imageTransition = new ChangeImageTransform(context, attrs);
        defaultTransition = new ChangeBounds(context, attrs);
        fabTransitionName = context.getString(R.string.transition_fab);
        transitionProperties = initTransProps();
        if (BuildConfig.DEBUG && TextUtils.isEmpty(fabTransitionName)) {
            throw new AssertionError("Transition name should not be empty");
        }
    }

    private String[] initTransProps() {
        ArrayList<String> transProps = new ArrayList<>();
        transProps.addAll(Arrays.asList(fabTransition.getTransitionProperties()));
        transProps.addAll(Arrays.asList(imageTransition.getTransitionProperties()));
        transProps.addAll(Arrays.asList(defaultTransition.getTransitionProperties()));
        return transProps.toArray(new String[transProps.size()]);
    }

    @Override
    public String[] getTransitionProperties() {
        return transitionProperties;
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        if (isFabTransition(transitionValues)) {
            fabTransition.captureStartValues(transitionValues);
        } else {
            defaultTransition.captureStartValues(transitionValues);
            imageTransition.captureStartValues(transitionValues);
        }
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        if (isFabTransition(transitionValues)) {
            fabTransition.captureEndValues(transitionValues);
        } else {
            defaultTransition.captureEndValues(transitionValues);
            imageTransition.captureStartValues(transitionValues);
        }
    }

    private boolean isFabTransition(TransitionValues transitionValues) {
        View view = transitionValues.view;
        return fabTransitionName.equals(view.getTransitionName());
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        if (isFabTransition(endValues)) {
            return fabTransition.createAnimator(sceneRoot, startValues, endValues);
        } else {
            Animator imageAnimator = imageTransition.createAnimator(sceneRoot, startValues, endValues);
            Animator defaultAnimator = defaultTransition.createAnimator(sceneRoot, startValues, endValues);
            if (imageAnimator == null) {
                return defaultAnimator;
            }
            if (defaultAnimator == null) {
                return imageAnimator;
            }
            AnimatorSet set = new AnimatorSet();
            set.playTogether(imageAnimator, defaultAnimator);
            return set;
        }
    }
}
