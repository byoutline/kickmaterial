package com.byoutline.kickmaterial.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-04-14
 */

public class EndlessRecyclerView extends RecyclerView {

    private static final int DEFAULT_ITEMS_THRESHOLD = 2;
    private static final int SCROLL_THRESHOLD = 1;

    private int visibleItemsThreshold = DEFAULT_ITEMS_THRESHOLD;
    private OnScrollListener scrollListener;
    private EndlessScrollListener endlessScrollListener;
    private int visibleItemCount;
    private int totalItemCount;
    private int lastVisibleItem;

    public EndlessRecyclerView(Context context) {
        super(context);
        init();
    }


    public EndlessRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EndlessRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setEndlessScrollListener(EndlessScrollListener listener) {
        endlessScrollListener = listener;
    }

    private void init() {
        super.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (scrollListener != null) {
                    scrollListener.onScrolled(recyclerView, dx, dy);
                }

                if (endlessScrollListener != null && dy > SCROLL_THRESHOLD) {
                    visibleItemCount = getChildCount();
                    totalItemCount = getLayoutManager().getItemCount();
                    lastVisibleItem = endlessScrollListener.getLastVisibleItemPosition();
                    boolean shouldLoadMoreData = lastVisibleItem + visibleItemsThreshold > totalItemCount;

                    if (endlessScrollListener.hasMoreDataAndNotLoading() && shouldLoadMoreData) {
                        endlessScrollListener.loadMoreData();
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (scrollListener != null) {
                    scrollListener.onScrollStateChanged(recyclerView, newState);
                }
            }
        });
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public void setOnScrollListener(OnScrollListener listener) {
        scrollListener = listener;
    }

    public int getVisibleItemsThreshold() {
        return visibleItemsThreshold;
    }

    public void setVisibleItemsThreshold(int visibleItemsThreshold) {
        this.visibleItemsThreshold = visibleItemsThreshold;
    }

    public interface EndlessScrollListener {
        int getLastVisibleItemPosition();

        void loadMoreData();

        boolean hasMoreDataAndNotLoading();
    }
}
