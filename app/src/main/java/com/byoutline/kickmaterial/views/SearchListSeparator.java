package com.byoutline.kickmaterial.views;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.byoutline.kickmaterial.R;

public class SearchListSeparator extends RecyclerView.ItemDecoration {
    private int space = 0;

    public SearchListSeparator(Context context) {
        space = context.getResources().getDimensionPixelSize(R.dimen.recyler_padding);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int pos = parent.getChildPosition(view);
        outRect.bottom = space;
    }
}