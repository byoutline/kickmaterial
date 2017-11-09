/**
 * Based on: https://github.com/sitepoint-editors/FloatingActionButton_Animation_Project
 */
package com.byoutline.kickmaterial.features.projectlist

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.LinearInterpolator
import com.byoutline.kickmaterial.KickMaterialApp

class FABBehaviour(context: Context, attrs: AttributeSet) : FloatingActionButton.Behavior() {

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: FloatingActionButton, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
        //child -> Floating Action Button
        val translation: ViewPropertyAnimator? = when {
            dyConsumed > 0 -> {
                val layoutParams = child.layoutParams as CoordinatorLayout.LayoutParams
                val fab_bottomMargin = layoutParams.bottomMargin
                child.animate().translationY((child.height + fab_bottomMargin).toFloat())
            }
            dyConsumed < 0 -> child.animate().translationY(0f)
            else -> null
        }
        translation?.multiplyDuration()?.setInterpolator(LinearInterpolator())?.start()
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: FloatingActionButton, directTargetChild: View, target: View, nestedScrollAxes: Int, type: Int): Boolean {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
    }
}

private fun ViewPropertyAnimator.multiplyDuration(): ViewPropertyAnimator {
    return setDuration((duration * KickMaterialApp.component.getAnimationDurationMultiplier()))
}