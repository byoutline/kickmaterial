package com.byoutline.kickmaterial.features.projectdetails

import android.content.Context
import android.graphics.Bitmap
import com.squareup.picasso.LruCache
import org.apache.commons.collections.map.LRUMap


/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
class LruCacheWithPlaceholders(context: Context) : LruCache(context) {
    private val urlToKeyMap = LRUMap(1024)

    fun getPlaceholder(uri: String): Bitmap? {
        val key = urlToKeyMap[uri] as? String ?: return null
        return get(key)
    }

    override fun set(key: String, bitmap: Bitmap) {
        super.set(key, bitmap)
        val newlineIndex = key.indexOf(KEY_SEPARATOR)
        urlToKeyMap.put(key.substring(0, newlineIndex), key)
    }

    companion object {
        private const val KEY_SEPARATOR = "\n"
    }
}
