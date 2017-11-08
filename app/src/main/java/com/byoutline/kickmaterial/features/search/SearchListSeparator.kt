package com.byoutline.kickmaterial.features.search

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import com.byoutline.kickmaterial.R

class SearchListSeparator(context: Context) : RecyclerView.ItemDecoration() {
    private var space = context.resources.getDimensionPixelSize(R.dimen.recyler_padding)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        outRect.bottom = space
    }
}