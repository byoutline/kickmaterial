package com.byoutline.kickmaterial.views

import android.support.v7.widget.RecyclerView

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-04-14
 */
private const val DEFAULT_ITEMS_THRESHOLD = 2
private const val SCROLL_THRESHOLD = 1

interface EndlessScrollListener {
    val lastVisibleItemPosition: Int

    fun loadMoreData()

    fun hasMoreDataAndNotLoading(): Boolean
}

fun RecyclerView.setEndlessScrollListener(
        listener: EndlessScrollListener,
        visibleItemsThreshold: Int = DEFAULT_ITEMS_THRESHOLD) {
    addOnScrollListener(EndlessScrollListenerRV(listener, visibleItemsThreshold))
}

private class EndlessScrollListenerRV(
        private val endlessScrollListener: EndlessScrollListener,
        private val visibleItemsThreshold: Int = DEFAULT_ITEMS_THRESHOLD
) : RecyclerView.OnScrollListener() {
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var lastVisibleItem: Int = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dy > SCROLL_THRESHOLD) {
            visibleItemCount = recyclerView.childCount
            totalItemCount = recyclerView.layoutManager.itemCount
            lastVisibleItem = endlessScrollListener.lastVisibleItemPosition
            val shouldLoadMoreData = lastVisibleItem + visibleItemsThreshold > totalItemCount

            if (endlessScrollListener.hasMoreDataAndNotLoading() && shouldLoadMoreData) {
                endlessScrollListener.loadMoreData()
            }
        }
    }
}
