package com.byoutline.kickmaterial.features.projectdetails

import android.content.Context
import android.util.TypedValue
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.databinding.ActivityProjectDetailsBinding
import com.byoutline.secretsauce.utils.ViewUtils

class ProjectDetailsScrollListener(context: Context,
                                   private val binding: ActivityProjectDetailsBinding): ObservableScrollView.Callbacks {
    private val minTitlesMarginTop: Int
    private val maxTitlesMarginTop: Int
    private val maxTitlesMarginLeft: Int
    private val maxParallaxValue: Int
    private val titleFontMaxSize: Int
    private val titleFontMinSize: Int
    private val maxTitlePaddingRight: Int
    init {
        val applicationContext = context.applicationContext
        val res = context.resources
        minTitlesMarginTop = ViewUtils.dpToPx(32f, applicationContext)
        maxTitlesMarginTop = res.getDimensionPixelSize(R.dimen.titles_container_margin_top) - res.getDimensionPixelSize(R.dimen.status_bar_height)


        maxTitlesMarginLeft = ViewUtils.dpToPx(32f, applicationContext)
        maxTitlePaddingRight = ViewUtils.dpToPx(72f, applicationContext)
        maxParallaxValue = res.getDimensionPixelSize(R.dimen.project_details_photo_height) / 3
        titleFontMaxSize = res.getDimensionPixelSize(R.dimen.font_21)
        titleFontMinSize = res.getDimensionPixelSize(R.dimen.font_16)
    }

    override fun onScrollChanged(deltaX: Int, deltaY: Int) {
        val scrollY = (binding.scrollView.scrollY * 0.6f).toInt()
        val newTitleLeft = Math.min(maxTitlesMarginLeft.toFloat(), scrollY * 0.5f)
        val newTitleTop = Math.min(maxTitlesMarginTop, scrollY).toFloat()
        val newTitlePaddingRight = Math.min(maxTitlePaddingRight, scrollY)

        binding.projectTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, Math.max(titleFontMaxSize - scrollY * 0.05f, titleFontMinSize.toFloat()))

        binding.projectTitleTv.setPadding(0, 0, newTitlePaddingRight, 0)

        binding.projectDetailsTitleContainer.translationX = newTitleLeft
        binding.projectDetailsTitleContainer.translationY = -newTitleTop
        binding.detailsContainer.translationY = -newTitleTop
        binding.playVideoBtn.translationY = -newTitleTop

        /** Content of scroll view is hiding to early during scroll so we move it also by
         * changing to padding  */
        binding.scrollView.setPadding(0, (newTitleTop * 0.6f).toInt(), 0, 0)


        // Move background photo (parallax effect)
        val parallax = (scrollY * .3f).toInt()
        if (maxParallaxValue > parallax) {
            binding.projectPhotoContainer.translationY = (-parallax).toFloat()
        }
    }
}