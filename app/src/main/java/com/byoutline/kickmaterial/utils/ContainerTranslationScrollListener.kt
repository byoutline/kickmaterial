package com.byoutline.kickmaterial.utils

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

/** Helps manually collapse containers when user scrolls down the recycler view */
class ContainerTranslationScrollListener(private val containerTranslationFactor: Float,
                                         private val container: ViewGroup,
                                         private val header: View)
    : RecyclerView.OnScrollListener() {

    var summaryScrolledValue: Int = 0

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        summaryScrolledValue += dy
        container.translationY = containerTranslationFactor * summaryScrolledValue
        header.translationY = (-summaryScrolledValue).toFloat()
    }
}