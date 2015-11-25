package com.byoutline.kickmaterial.dagger;


import com.byoutline.kickmaterial.managers.AccessTokenProvider;
import com.byoutline.secretsauce.di.SessionId;

import dagger.Module;
import dagger.Provides;


@Module
public class AppModule {

    private final AccessTokenProvider accessTokenProvider;

    public AppModule(AccessTokenProvider accessTokenProvider) {
        this.accessTokenProvider = accessTokenProvider;
    }

    @Provides
    @SessionId
    String providesSessionId() {
        return accessTokenProvider.get();
    }
}
