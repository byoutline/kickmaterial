package com.byoutline.kickmaterial.api

import android.text.TextUtils
import com.byoutline.kickmaterial.dagger.GlobalScope
import com.byoutline.kickmaterial.login.AccessTokenProvider
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.*
import javax.inject.Inject

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 05.05.14.
 */
@GlobalScope
class KickMaterialRequestInterceptor @Inject
constructor(private val accessTokenProvider: AccessTokenProvider) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val urlBuilder = original.url().newBuilder()
        addBasicHeaders(urlBuilder)
        val accessToken = accessTokenProvider.get()
        if (!TextUtils.isEmpty(accessToken)) {
            urlBuilder.addQueryParameter("oauth_token", accessToken)
        }
        val newUrl = urlBuilder.build()
        val newRequest = original.newBuilder().url(newUrl).build()
        return chain.proceed(newRequest)
    }

    companion object {

        private fun addBasicHeaders(builder: HttpUrl.Builder) {
            for ((name, value) in basicQueries) {
                builder.addQueryParameter(name, value)
            }
        }

        val basicQueries: List<Pair<String, String>>
            get() {
                val queries = ArrayList<Pair<String, String>>()
                queries.add("client_id" to "SECRET_KEY")
                return queries
            }
    }
}
