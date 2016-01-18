package com.byoutline.kickmaterial.dagger;


import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import com.byoutline.kickmaterial.R;
import com.byoutline.kickmaterial.managers.AccessTokenProvider;
import com.byoutline.ottoeventcallback.PostFromAnyThreadBus;
import com.byoutline.secretsauce.BaseApp;
import com.byoutline.secretsauce.di.ContainerId;
import com.byoutline.secretsauce.di.DefaultFontName;
import com.byoutline.secretsauce.di.SessionId;
import com.squareup.otto.Bus;
import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final BaseApp app;
    private static final String MEDIUM_FONT_NAME = "Roboto_Medium.ttf";

    public AppModule(BaseApp app) {
        this.app = app;
    }

    @Provides
    @AppScope
    Bus provideBus() {
        return new PostFromAnyThreadBus();
    }

    @Provides
    protected SharedPreferences provideSharedPrefs() {
        return app.getSharedPrefs();
    }

    @Provides
    @SessionId
    String providesSessionId(AccessTokenProvider accessTokenProvider) {
        // In this app we handle session cleanup by dropping components manually.
        return accessTokenProvider.get();
    }

    @Provides
    @DefaultFontName
    String provideDefaultFontName() {
        return MEDIUM_FONT_NAME;
    }

    @Provides
    @ContainerId
    @IdRes
    int provideContainerId() {
        return R.id.container;
    }
}