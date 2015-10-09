/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.byoutline.kickmaterial.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CircleTransition extends Transition {
    private static final String PROPERTY_BOUNDS = "circleTransition:bounds";
    private static final String PROPERTY_POSITION = "circleTransition:position";
    private static final String PROPERTY_IMAGE = "circleTransition:image";
    private static final String[] TRANSITION_PROPERTIES = {
            PROPERTY_BOUNDS,
            PROPERTY_POSITION,
    };

    public CircleTransition() {
    }

    public CircleTransition(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public String[] getTransitionProperties() {
        return TRANSITION_PROPERTIES;
    }

    private void captureValues(TransitionValues transitionValues) {
        final View view = transitionValues.view;
        transitionValues.values.put(PROPERTY_BOUNDS, new Rect(
                view.getLeft(), view.getTop(), view.getRight(), view.getBottom()
        ));
        int[] position = new int[2];
        transitionValues.view.getLocationInWindow(position);
        transitionValues.values.put(PROPERTY_POSITION, position);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        final View view = transitionValues.view;
        if (view.getWidth() <= 0 || view.getHeight() <= 0) {
            return;
        }
        captureValues(transitionValues);
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        final View view = transitionValues.view;
        if (view.getWidth() <= 0 || view.getHeight() <= 0) {
            return;
        }
        captureValues(transitionValues);
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        transitionValues.values.put(PROPERTY_IMAGE, bitmap);
    }

    @Override
    public Animator createAnimator(final ViewGroup sceneRoot, TransitionValues startValues,
                                   final TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return null;
        }
        Rect startBounds = (Rect) startValues.values.get(PROPERTY_BOUNDS);
        Rect endBounds = (Rect) endValues.values.get(PROPERTY_BOUNDS);
        boolean boundsEqual = startBounds == null || endBounds == null || startBounds.equals(endBounds);
        if (boundsEqual) {
            return null;
        }
        int[] sceneRootLoc = new int[2];
        sceneRoot.getLocationInWindow(sceneRootLoc);
        int[] startLoc = (int[]) startValues.values.get(PROPERTY_POSITION);

        final View startView = getStartView(sceneRoot, startValues, sceneRootLoc, startLoc);
        final View endView = endValues.view;

        endView.setAlpha(0f);

        Path circlePath = getMovePath(endValues, startView, sceneRootLoc, startLoc, endView);
        Animator circleAnimator = ObjectAnimator.ofFloat(startView, View.TRANSLATION_X,
                View.TRANSLATION_Y, circlePath);


        circleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startView.setVisibility(View.INVISIBLE);
                endView.setAlpha(1f);
                sceneRoot.getOverlay().remove(startView);
            }
        });

        AnimatorSet moveSet = new AnimatorSet();
        float scaleRatio = ((float) endView.getWidth()) / startView.getWidth();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(startView, View.SCALE_X, 1, scaleRatio);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(startView, View.SCALE_Y, 1, scaleRatio);
        moveSet.playTogether(circleAnimator, scaleXAnimator, scaleYAnimator);
//        moveSet.setDuration(4000);

        return moveSet;
    }

    private View getStartView(ViewGroup sceneRoot, TransitionValues startValues, int[] sceneRootLoc, int[] startLoc) {
        Bitmap startImage = (Bitmap) startValues.values.get(PROPERTY_IMAGE);
        Drawable startBackground = new BitmapDrawable(startImage);
        final View startView = addViewToOverlay(sceneRoot, startImage.getWidth(),
                startImage.getHeight(), startBackground);

        int startTranslationX = startLoc[0] - sceneRootLoc[0];
        int startTranslationY = startLoc[1] - sceneRootLoc[1];

        startView.setTranslationX(startTranslationX);
        startView.setTranslationY(startTranslationY);
        return startView;
    }

    private Path getMovePath(TransitionValues endValues, View startView, int[] sceneRootLoc, int[] startLoc, View endView) {
        float circleStartX = startLoc[0] - sceneRootLoc[0];
        float circleStartY = startLoc[1] - sceneRootLoc[1];
        int[] endLoc = (int[]) endValues.values.get(PROPERTY_POSITION);
        float circleEndX = endLoc[0] - sceneRootLoc[0] +
                ((endView.getWidth() - startView.getWidth()) / 2);
        float circleEndY = endLoc[1] - sceneRootLoc[1] +
                ((endView.getHeight() - startView.getHeight()) / 2);
        return getPathMotion().getPath(circleStartX, circleStartY, circleEndX,
                circleEndY);
    }

    private View addViewToOverlay(ViewGroup sceneRoot, int width, int height, Drawable background) {
        View view = new View(sceneRoot.getContext());
        view.setBackground(background);
        int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        view.measure(widthSpec, heightSpec);
        view.layout(0, 0, width, height);
        sceneRoot.getOverlay().add(view);
        return view;
    }
}
