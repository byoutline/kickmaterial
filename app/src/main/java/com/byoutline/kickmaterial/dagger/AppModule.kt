package com.byoutline.kickmaterial.dagger


import android.content.SharedPreferences
import android.support.annotation.IdRes
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.features.login.AccessTokenProvider
import com.byoutline.ottoeventcallback.PostFromAnyThreadBus
import com.byoutline.secretsauce.BaseApp
import com.byoutline.secretsauce.di.ContainerId
import com.byoutline.secretsauce.di.DefaultFontName
import com.byoutline.secretsauce.di.SessionId
import com.squareup.otto.Bus
import dagger.Module
import dagger.Provides

@Module
open class AppModule(private val app: BaseApp) {

    @Provides
    @AppScope
    internal fun provideBus(): Bus {
        return PostFromAnyThreadBus()
    }

    @Provides
    protected open fun provideSharedPrefs(): SharedPreferences {
        return app.sharedPrefs
    }

    @Provides
    @SessionId
    internal fun providesSessionId(accessTokenProvider: AccessTokenProvider): String {
        // In this app we handle session cleanup by dropping components manually.
        return accessTokenProvider.get()
    }

    @Provides
    @DefaultFontName
    internal fun provideDefaultFontName(): String {
        return MEDIUM_FONT_NAME
    }

    @Provides
    @ContainerId
    @IdRes
    internal fun provideContainerId(): Int {
        return R.id.container
    }

    companion object {
        private const val MEDIUM_FONT_NAME = "Roboto_Medium.ttf"
    }
}