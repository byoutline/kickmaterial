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
package com.byoutline.kickmaterial.transitions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.transition.Transition
import android.transition.TransitionValues
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class CircleTransition : Transition {

    constructor() {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun getTransitionProperties(): Array<String> {
        return TRANSITION_PROPERTIES
    }

    private fun captureValues(transitionValues: TransitionValues) {
        val view = transitionValues.view
        transitionValues.values.put(PROPERTY_BOUNDS, Rect(
                view.left, view.top, view.right, view.bottom
        ))
        val position = IntArray(2)
        transitionValues.view.getLocationInWindow(position)
        transitionValues.values.put(PROPERTY_POSITION, position)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        val view = transitionValues.view
        if (view.width <= 0 || view.height <= 0) {
            return
        }
        captureValues(transitionValues)
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        val view = transitionValues.view
        if (view.width <= 0 || view.height <= 0) {
            return
        }
        captureValues(transitionValues)
        val bitmap = Bitmap.createBitmap(view.width, view.height,
                Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        transitionValues.values.put(PROPERTY_IMAGE, bitmap)
    }

    override fun createAnimator(sceneRoot: ViewGroup, startValues: TransitionValues?,
                                endValues: TransitionValues?): Animator? {
        if (startValues == null || endValues == null) {
            return null
        }
        val startBounds = startValues.values[PROPERTY_BOUNDS] as Rect
        val endBounds = endValues.values[PROPERTY_BOUNDS] as Rect
        val boundsEqual = startBounds == endBounds
        if (boundsEqual) {
            return null
        }
        val sceneRootLoc = IntArray(2)
        sceneRoot.getLocationInWindow(sceneRootLoc)
        val startLoc = startValues.values[PROPERTY_POSITION] as IntArray

        val startView = getStartView(sceneRoot, startValues, sceneRootLoc, startLoc)
        val endView = endValues.view

        endView.alpha = 0f

        val circlePath = getMovePath(endValues, startView, sceneRootLoc, startLoc, endView)
        val circleAnimator = ObjectAnimator.ofFloat(startView, View.TRANSLATION_X,
                View.TRANSLATION_Y, circlePath)


        circleAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                startView.visibility = View.INVISIBLE
                endView.alpha = 1f
                sceneRoot.overlay.remove(startView)
            }
        })

        val moveSet = AnimatorSet()
        val scaleRatio = endView.width.toFloat() / startView.width
        val scaleXAnimator = ObjectAnimator.ofFloat<View>(startView, View.SCALE_X, 1f, scaleRatio)
        val scaleYAnimator = ObjectAnimator.ofFloat<View>(startView, View.SCALE_Y, 1f, scaleRatio)
        moveSet.playTogether(circleAnimator, scaleXAnimator, scaleYAnimator)
        //        moveSet.setDuration(4000);

        return moveSet
    }

    private fun getStartView(sceneRoot: ViewGroup, startValues: TransitionValues, sceneRootLoc: IntArray, startLoc: IntArray): View {
        val startImage = startValues.values[PROPERTY_IMAGE] as Bitmap
        val startBackground = BitmapDrawable(startImage)
        val startView = addViewToOverlay(sceneRoot, startImage.width,
                startImage.height, startBackground)

        val startTranslationX = startLoc[0] - sceneRootLoc[0]
        val startTranslationY = startLoc[1] - sceneRootLoc[1]

        startView.translationX = startTranslationX.toFloat()
        startView.translationY = startTranslationY.toFloat()
        return startView
    }

    private fun getMovePath(endValues: TransitionValues, startView: View, sceneRootLoc: IntArray, startLoc: IntArray, endView: View): Path {
        val circleStartX = (startLoc[0] - sceneRootLoc[0]).toFloat()
        val circleStartY = (startLoc[1] - sceneRootLoc[1]).toFloat()
        val endLoc = endValues.values[PROPERTY_POSITION] as IntArray
        val circleEndX = (endLoc[0] - sceneRootLoc[0] + (endView.width - startView.width) / 2).toFloat()
        val circleEndY = (endLoc[1] - sceneRootLoc[1] + (endView.height - startView.height) / 2).toFloat()
        return pathMotion.getPath(circleStartX, circleStartY, circleEndX,
                circleEndY)
    }

    private fun addViewToOverlay(sceneRoot: ViewGroup, width: Int, height: Int, background: Drawable): View {
        val view = View(sceneRoot.context)
        view.background = background
        val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
        view.measure(widthSpec, heightSpec)
        view.layout(0, 0, width, height)
        sceneRoot.overlay.add(view)
        return view
    }

    companion object {
        private val PROPERTY_BOUNDS = "circleTransition:bounds"
        private val PROPERTY_POSITION = "circleTransition:position"
        private val PROPERTY_IMAGE = "circleTransition:image"
        private val TRANSITION_PROPERTIES = arrayOf(PROPERTY_BOUNDS, PROPERTY_POSITION)
    }
}
