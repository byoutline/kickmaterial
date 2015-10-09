package com.byoutline.kickmaterial.views;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.byoutline.kickmaterial.utils.LUtils;
import com.byoutline.secretsauce.utils.ViewUtils;

public class CategoriesListSeparator extends RecyclerView.ItemDecoration {
    private final int restItemPadding;
    private final int firstItemSpace;

    public CategoriesListSeparator(Context context) {
        this(context, ViewUtils.dpToPx(12, context));
    }

    public CategoriesListSeparator(Context context, int firstItemSpace) {
        this.firstItemSpace = firstItemSpace;
        if (LUtils.hasL()) {
            restItemPadding = ViewUtils.dpToPx(-2, context);
        } else {
            restItemPadding = ViewUtils.dpToPx(-8, context);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int pos = parent.getChildPosition(view);

        if (pos == 0) {
            outRect.bottom = firstItemSpace;
        } else {
            outRect.top = restItemPadding;
        }
    }
}