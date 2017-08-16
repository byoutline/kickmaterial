package com.byoutline.kickmaterial.views

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-04-14
 */

class EndlessRecyclerView : RecyclerView {

    var visibleItemsThreshold = DEFAULT_ITEMS_THRESHOLD
    private var scrollListener: RecyclerView.OnScrollListener? = null
    private var endlessScrollListener: EndlessScrollListener? = null
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var lastVisibleItem: Int = 0

    constructor(context: Context) : super(context) {
        init()
    }


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    fun setEndlessScrollListener(listener: EndlessScrollListener) {
        endlessScrollListener = listener
    }

    private fun init() {
        super.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (scrollListener != null) {
                    scrollListener!!.onScrolled(recyclerView, dx, dy)
                }

                if (endlessScrollListener != null && dy > SCROLL_THRESHOLD) {
                    visibleItemCount = childCount
                    totalItemCount = layoutManager.itemCount
                    lastVisibleItem = endlessScrollListener!!.lastVisibleItemPosition
                    val shouldLoadMoreData = lastVisibleItem + visibleItemsThreshold > totalItemCount

                    if (endlessScrollListener!!.hasMoreDataAndNotLoading() && shouldLoadMoreData) {
                        endlessScrollListener!!.loadMoreData()
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (scrollListener != null) {
                    scrollListener!!.onScrollStateChanged(recyclerView, newState)
                }
            }
        })
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
    }

    override fun setOnScrollListener(listener: RecyclerView.OnScrollListener) {
        scrollListener = listener
    }

    interface EndlessScrollListener {
        val lastVisibleItemPosition: Int

        fun loadMoreData()

        fun hasMoreDataAndNotLoading(): Boolean
    }

    companion object {

        private const val DEFAULT_ITEMS_THRESHOLD = 2
        private const val SCROLL_THRESHOLD = 1
    }
}
