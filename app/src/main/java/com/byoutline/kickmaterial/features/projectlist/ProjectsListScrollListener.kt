package com.byoutline.kickmaterial.features.projectlist

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.databinding.FragmentProjectsBinding
import com.byoutline.kickmaterial.utils.KickMaterialFragment
import com.byoutline.secretsauce.utils.ViewUtils

private const val BG_COLOR_MAX = 255
private const val BG_COLOR_MIN = 232

class ProjectsListScrollListener(context: Context,
                                 private val hostActivityProv: () -> KickMaterialFragment.HostActivity?,
                                 private val binding: FragmentProjectsBinding) : RecyclerView.OnScrollListener() {
    var summaryScrolled: Float = 0f
    private var toolbarScrollPoint: Float = 0F
    private var maxScroll: Float = 0F

    init {
        maxScroll = (2 * context.resources.getDimensionPixelSize(R.dimen.project_header_padding_top) + ViewUtils.dpToPx(48f, context)).toFloat()
        toolbarScrollPoint = ViewUtils.dpToPx(24f, context).toFloat()
    }

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (dy > toolbarScrollPoint) {
            hostActivityProv()?.showToolbar(false, true)
            if (binding.showCategoriesFab.visibility == View.VISIBLE) {
                binding.showCategoriesFab.hide()
            }
        }

        if (dy < toolbarScrollPoint * -1) {
            hostActivityProv()?.showToolbar(true, true)
            if (binding.showCategoriesFab.visibility == View.GONE) {
                binding.showCategoriesFab.show()
            }
        }

        summaryScrolled += dy.toFloat()
        binding.bubblesIv.translationY = -0.5f * summaryScrolled

        var alpha = summaryScrolled / maxScroll
        alpha = Math.min(1.0f, alpha)

        hostActivityProv()?.setToolbarAlpha(alpha)

        //change background color on scroll
        val color = Math.max(BG_COLOR_MIN.toDouble(), BG_COLOR_MAX - summaryScrolled * 0.05).toInt()
        binding.mainParentRl.setBackgroundColor(Color.argb(255, color, color, color))
    }
}