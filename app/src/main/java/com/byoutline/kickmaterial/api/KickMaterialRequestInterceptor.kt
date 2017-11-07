package com.byoutline.kickmaterial.api

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 05.05.14.
 */
@Singleton
class KickMaterialRequestInterceptor
@Inject constructor() : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val urlBuilder = original.url().newBuilder()
        urlBuilder.addQueryParameter("client_id", "SECRET_KEY")
        val newUrl = urlBuilder.build()
        val newRequest = original.newBuilder().url(newUrl).build()
        return chain.proceed(newRequest)
    }
}
