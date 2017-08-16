package com.byoutline.kickmaterial.utils

import timber.log.Timber
import java.io.UnsupportedEncodingException
import java.net.MalformedURLException
import java.net.URL
import java.net.URLDecoder
import java.util.*

/**
 * Helper methods fot extracting query parameters from url.
 * Created by Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 30.03.15.
 */
object QueryParamsExtractor {

    fun getQueryParams(url: String): Map<String, String> {
        try {
            val u = URL(url)
            return splitQuery(u)
        } catch (e: MalformedURLException) {
            Timber.e("Failed to get query params from url: " + url, e)
            return emptyMap()
        } catch (e: UnsupportedEncodingException) {
            Timber.e("Failed to get query params from url: " + url, e)
            return emptyMap()
        }

    }

    @Throws(UnsupportedEncodingException::class)
    fun splitQuery(url: URL): Map<String, String> {
        val queryPairs = LinkedHashMap<String, String>()
        val query = url.query
        val pairs = query.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val charsetName = "UTF-8"
        for (pair in pairs) {
            val idx = pair.indexOf("=")
            val key = URLDecoder.decode(pair.substring(0, idx), charsetName)
            val value = URLDecoder.decode(pair.substring(idx + 1), charsetName)
            queryPairs.put(key, value)
        }
        return queryPairs
    }

}
