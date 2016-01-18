package com.byoutline.kickmaterial.managers;

import com.byoutline.kickmaterial.dagger.AppScope;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Created by Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 31.03.15.
 */
@AppScope
public class AccessTokenProvider implements Provider<String> {

    @Nonnull
    private String accessToken = "";

    @Inject
    public AccessTokenProvider() {
    }

    public void set(String accessToken) {
        if (accessToken == null) {
            accessToken = "";
        }
        this.accessToken = accessToken;
    }

    @Override
    public String get() {
        return accessToken;
    }
}
