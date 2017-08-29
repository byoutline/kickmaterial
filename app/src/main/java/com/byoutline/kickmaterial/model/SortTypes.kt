package com.byoutline.kickmaterial.model

import android.support.annotation.StringRes
import com.byoutline.kickmaterial.KickMaterialApp
import com.byoutline.kickmaterial.R
import java.util.*

/**
 * Created by Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 31.03.15.
 */
enum class SortTypes(@StringRes nameResId: Int) {
    MAGIC(R.string.sort_magic),
    POPULARITY(R.string.sort_popularity),
    NEWEST(R.string.sort_newest),
    END_DATE(R.string.sort_end_date),
    MOST_FUNDED(R.string.sort_most_funded);

    val apiName: String = name.toLowerCase(Locale.ENGLISH)
    private val displayName: String by lazy { KickMaterialApp.component.app.getString(nameResId) }

    override fun toString(): String {
        return displayName
    }
}
