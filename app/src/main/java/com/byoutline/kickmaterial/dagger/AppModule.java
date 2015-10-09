package com.byoutline.kickmaterial.dagger;


import com.byoutline.kickmaterial.managers.AccessTokenProvider;
import com.byoutline.secretsauce.BaseApp;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;


@Module
public class AppModule {

    private final AccessTokenProvider accessTokenProvider;

    public AppModule(AccessTokenProvider accessTokenProvider) {
        this.accessTokenProvider = accessTokenProvider;
    }

    @Provides
    @Named(BaseApp.INJECT_NAME_SESSION_ID)
    String providesSessionId() {
        return accessTokenProvider.get();
    }
}
