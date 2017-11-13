package com.byoutline.kickmaterial.features.selectcategory

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import com.byoutline.kickmaterial.utils.LUtils
import com.byoutline.secretsauce.utils.ViewUtils

class CategoriesListSeparator @JvmOverloads constructor(
        context: Context,
        private val firstItemSpace: Int = ViewUtils.dpToPx(12f, context)
) : RecyclerView.ItemDecoration() {
    private val restItemPadding: Int = ViewUtils.dpToPx(if (LUtils.hasL()) -2f else -8f, context)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        val pos = parent.getChildAdapterPosition(view)

        if (pos == 0) {
            outRect.bottom = firstItemSpace
        } else {
            outRect.top = restItemPadding
        }
    }
}