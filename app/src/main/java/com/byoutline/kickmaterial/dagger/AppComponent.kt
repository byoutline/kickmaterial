package com.byoutline.kickmaterial.dagger


import com.byoutline.kickmaterial.managers.AccessTokenProvider
import com.byoutline.secretsauce.di.AppComponentInterface
import dagger.Component

/**
 * Created by Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 27.03.15.
 */
@AppScope
@Component(modules = arrayOf(AppModule::class))
interface AppComponent : AppComponentInterface {
    val accessTokenProvider: AccessTokenProvider
}
