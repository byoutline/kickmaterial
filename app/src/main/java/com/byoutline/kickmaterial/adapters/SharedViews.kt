package com.byoutline.kickmaterial.adapters

import android.view.View
import java.util.*

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
class SharedViews(vararg views: View?) {
    private val views = ArrayList<View>()

    init {
        this.views.addAll(listOf(*views).filterNotNull())
    }

    fun asArrayList(): ArrayList<View> {
        return views
    }

    fun asArray(): Array<View> {
        return views.toTypedArray()
    }

    fun add(view: View) {
        views.add(view)
    }
}
