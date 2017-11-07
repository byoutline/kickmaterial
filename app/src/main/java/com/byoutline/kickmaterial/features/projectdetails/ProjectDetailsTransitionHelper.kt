package com.byoutline.kickmaterial.features.projectdetails

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.support.annotation.ColorInt
import android.support.annotation.StringRes
import android.widget.ImageView
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.model.Project
import com.byoutline.kickmaterial.model.ProjectDetails
import com.byoutline.kickmaterial.model.ProjectIdAndSignature
import com.byoutline.kickmaterial.transitions.PaletteAndAplaTransformation
import com.byoutline.observablecachedfield.ObservableCachedFieldWithArg
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.Reusable
import javax.inject.Inject

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
const val MAX_TRANSITION_DELAY = 800
private const val IMAGE_RATIO = (4 / 3).toDouble()

// Factory is reusable, but helper is not - due to it handling the postponed transition
// it is bound to specific activity instance.
@Reusable
class ProjectDetailsTransitionHelperFactory
@Inject constructor(
        private val projectDetailsField: ObservableCachedFieldWithArg<ProjectDetails, ProjectIdAndSignature>,
        private val picassoCache: LruCacheWithPlaceholders) {

    fun create(project: Project, projectDetailsActivity: ProjectDetailsActivity): ProjectDetailsTransitionHelper {
        val imageHeight = projectDetailsActivity.resources.getDimensionPixelSize(R.dimen.project_details_photo_height)
        val imageWidth = (imageHeight * IMAGE_RATIO).toInt()
        return ProjectDetailsTransitionHelper(projectDetailsField, picassoCache, project,
                imageWidth = imageWidth, imageHeight = imageHeight,
                delayedTransitionActivity = projectDetailsActivity)
    }
}


interface DelayedTransitionActivity {
    fun startPostponedEnterTrans()
    fun setDetailsContainerBgColor(@ColorInt color: Int)
}


class ProjectDetailsTransitionHelper(val projectDetailsField: ObservableCachedFieldWithArg<ProjectDetails, ProjectIdAndSignature>,
                                     private val picassoCache: LruCacheWithPlaceholders,
                                     val project: Project,
                                     private val imageWidth: Int,
                                     private val imageHeight: Int,
                                     private val delayedTransitionActivity: DelayedTransitionActivity) {
    @StringRes
    val projectBackingProgressTxtId: Int = if (project.isFunded) R.string.funded else R.string.backing_in_progress
    internal val videoBtnAnimator = VideoAlphaAnimator()

    fun executeIfCachedProjectDetailsAreAvailable(action: (ProjectDetails) -> Any, displayErrorAction: ()-> Any) {
        val details = projectDetailsField.observable().get()
        if (details == null) {
            displayErrorAction()
            postProjectDetails()
        } else {
            action(details)
        }
    }

    fun postProjectDetails() {
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
        projectPhotoIv.postDelayed({ delayedTransitionActivity.startPostponedEnterTrans() }, MAX_TRANSITION_DELAY.toLong())
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
                            delayedTransitionActivity.setDetailsContainerBgColor(palette.getDarkVibrantColor(Color.BLACK))
                        }
                        delayedTransitionActivity.startPostponedEnterTrans()
                    }

                    override fun onError() {
                        delayedTransitionActivity.startPostponedEnterTrans()
                    }
                })
    }
}

