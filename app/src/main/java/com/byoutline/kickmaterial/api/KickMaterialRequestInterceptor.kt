package com.byoutline.kickmaterial.api

import android.text.TextUtils
import com.byoutline.kickmaterial.dagger.GlobalScope
import com.byoutline.kickmaterial.login.AccessTokenProvider
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 05.05.14.
 */
@GlobalScope
class KickMaterialRequestInterceptor
@Inject constructor(private val accessTokenProvider: AccessTokenProvider) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val urlBuilder = original.url().newBuilder()
        urlBuilder.addQueryParameter("client_id", "SECRET_KEY")
        val accessToken = accessTokenProvider.get()
        if (!TextUtils.isEmpty(accessToken)) {
            urlBuilder.addQueryParameter("oauth_token", accessToken)
        }
        val newUrl = urlBuilder.build()
        val newRequest = original.newBuilder().url(newUrl).build()
        return chain.proceed(newRequest)
    }
}
