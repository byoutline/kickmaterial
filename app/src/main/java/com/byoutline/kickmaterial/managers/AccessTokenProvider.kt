package com.byoutline.kickmaterial.managers

import com.byoutline.kickmaterial.dagger.AppScope
import javax.inject.Inject
import javax.inject.Provider

/**
 * Created by Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 31.03.15.
 */
@AppScope
class AccessTokenProvider @Inject
constructor() : Provider<String> {

    private var accessToken = ""

    fun set(accessToken: String?) {
        this.accessToken = accessToken ?: ""
    }

    override fun get(): String {
        return accessToken
    }
}
