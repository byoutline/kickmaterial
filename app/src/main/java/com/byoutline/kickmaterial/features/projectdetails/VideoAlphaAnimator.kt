package com.byoutline.kickmaterial.features.projectdetails

import android.animation.Animator
import android.animation.ObjectAnimator
import android.databinding.BindingAdapter
import android.view.View
import com.byoutline.kickmaterial.model.ProjectVideo
import timber.log.Timber
import java.lang.ref.WeakReference

private const val ACTION_BUTTON_VISIBILITY_ANIM_DELAY = MAX_TRANSITION_DELAY + 200

internal fun getAlphaAnimator(view: View, hideView: Boolean = false): Animator {
    val start = (if (hideView) 1 else 0).toFloat()
    val end = (if (hideView) 0 else 1).toFloat()
    view.alpha = start
    val animator = ObjectAnimator.ofFloat(view, View.ALPHA, start, end)
    animator.duration = 200
    animator.setAutoCancel(true)
    return animator
}

class VideoAlphaAnimator {
    private var lastRunnable: WeakReference<Runnable>? = null



    fun animateVideoBtn(videoBtn: View, video: ProjectVideo?) {
        val videoExist: Boolean = video != null
        Timber.d("playVisibility: $videoExist")
        videoBtn.isEnabled = videoExist
        val r = Runnable {
            // Button alpha may change during transition, we may have to wait until its end
            // to check its state.
            Timber.w("playVisibility Action btn alpha: " + videoBtn.alpha)
            val actionBtnShouldAnimate = videoExist && videoBtn.alpha != 1f || !videoExist && videoBtn.alpha != 0f
            if (!actionBtnShouldAnimate) {
                return@Runnable
            }
            val alphaAnimator = getAlphaAnimator(videoBtn, hideView = !videoExist)
            alphaAnimator.duration = 600
            alphaAnimator.start()
        }
        // Cancel previous animation (if any)
        lastRunnable?.get()?.let { lastAnim -> videoBtn.removeCallbacks(lastAnim) }
        lastRunnable = WeakReference(r)
        // Wait just in case transition is still in progress.
        videoBtn.postDelayed(r, ACTION_BUTTON_VISIBILITY_ANIM_DELAY.toLong())
    }
}

@BindingAdapter("projectVideo", "viewModel")
fun animateVideoBtn(videoBtn: View, video: ProjectVideo?, viewModel: ProjectDetailsViewModel?) {
    viewModel?.videoBtnAnimator?.animateVideoBtn(videoBtn, video)
}