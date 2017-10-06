package com.byoutline.kickmaterial.features.projectdetails

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.support.annotation.ColorInt
import android.support.annotation.StringRes
import android.view.View
import android.widget.ImageView
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.model.Project
import com.byoutline.kickmaterial.model.ProjectDetails
import com.byoutline.kickmaterial.model.ProjectIdAndSignature
import com.byoutline.kickmaterial.model.ProjectVideo
import com.byoutline.kickmaterial.transitions.PaletteAndAplaTransformation
import com.byoutline.observablecachedfield.ObservableCachedFieldWithArg
import com.byoutline.rx.invokeOnAPause
import com.byoutline.secretsauce.utils.ViewUtils
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.android.ActivityEvent
import dagger.Reusable
import javax.inject.Inject

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
const val MAX_TRANSITION_DELAY = 800
private const val IMAGE_RATIO = (4 / 3).toDouble()

@Reusable
class ProjectDetailsViewModelFactory
@Inject constructor(
        private val projectDetailsField: ObservableCachedFieldWithArg<ProjectDetails, ProjectIdAndSignature>,
        private val picassoCache: LruCacheWithPlaceholders) {

    fun create(project: Project, context: Context): ProjectDetailsViewModel {
        val imageHeight = context.resources.getDimensionPixelSize(R.dimen.project_details_photo_height)
        val imageWidth = (imageHeight * IMAGE_RATIO).toInt()
        return ProjectDetailsViewModel(projectDetailsField, picassoCache, project,
                imageWidth = imageWidth, imageHeight = imageHeight)
    }
}


interface DelayedTransitionActivity : LifecycleProvider<ActivityEvent> {
    fun startPostponedEnterTrans()
    fun setDetailsContainerBgColor(@ColorInt color: Int)
}


class ProjectDetailsViewModel(val projectDetailsField: ObservableCachedFieldWithArg<ProjectDetails, ProjectIdAndSignature>,
                              private val picassoCache: LruCacheWithPlaceholders,
                              val project: Project,
                              private val imageWidth: Int,
                              private val imageHeight: Int) {
    private var delayedTransitionActivity: DelayedTransitionActivity? = null
    @StringRes
    val projectBackingProgressTxtId: Int = if (project.isFunded) R.string.funded else R.string.backing_in_progress
    internal val videoBtnAnimator = VideoAlphaAnimator()

    fun executeIfCachedProjectDetailsAreAvailable(@StringRes errorResId: Int, action: (ProjectDetails) -> Unit) {
        val details = projectDetailsField.observable().get()
        if (details == null) {
            ViewUtils.showToast(errorResId)
            postProjectDetails()
        } else {
            action(details)
        }
    }

    fun attachViewUntilPause(delayedTransitionActivity: DelayedTransitionActivity) {
        this.delayedTransitionActivity = delayedTransitionActivity
        delayedTransitionActivity.invokeOnAPause { this.delayedTransitionActivity = null }
        postProjectDetails()
    }

    private fun postProjectDetails() {
        val params = ProjectIdAndSignature(project.id, project.detailsQueryMap)
        projectDetailsField.postValue(params)
    }

    fun loadProjectPhoto(projectPhotoIv: ImageView) {
        val bitmap = picassoCache.getPlaceholder(project.bigPhotoUrl) ?: picassoCache.getPlaceholder(project.photoUrl)
        val placeholderAlreadyFetched = bitmap != null
        if (placeholderAlreadyFetched) {
            projectPhotoIv.setImageBitmap(bitmap)
        }
        // Make sure that transition starts soon even if image is not ready.
        projectPhotoIv.postDelayed({ delayedTransitionActivity?.startPostponedEnterTrans() }, MAX_TRANSITION_DELAY.toLong())
        Picasso.with(projectPhotoIv.context)
                .load(project.bigPhotoUrl)
                .resize(imageWidth, imageHeight)
                .onlyScaleDown()
                .centerCrop()
                .transform(PaletteAndAplaTransformation.instance())
                .into(projectPhotoIv, object : Callback {
                    override fun onSuccess() {
                        val bitmap = (projectPhotoIv.drawable as BitmapDrawable).bitmap // Ew!
                        PaletteAndAplaTransformation.getPalette(bitmap)?.let { palette ->
                            delayedTransitionActivity?.setDetailsContainerBgColor(palette.getDarkVibrantColor(Color.BLACK))
                        }
                        delayedTransitionActivity?.startPostponedEnterTrans()
                    }

                    override fun onError() {
                        delayedTransitionActivity?.startPostponedEnterTrans()
                    }
                })
    }
}

