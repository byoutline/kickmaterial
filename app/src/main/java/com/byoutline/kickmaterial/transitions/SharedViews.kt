package com.byoutline.kickmaterial.transitions

import android.view.View
import java.util.*

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
class SharedViews(vararg views: View?) {
    private val views = ArrayList<View>()

    init {
        this.views.addAll(listOfNotNull(*views))
    }

    fun asArrayList(): ArrayList<View> = views

    fun asArray(): Array<View> = views.toTypedArray()

    fun add(view: View) {
        views.add(view)
    }
}
