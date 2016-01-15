package com.byoutline.kickmaterial.managers;

import com.byoutline.cachedfield.CachedFieldWithArg;
import com.byoutline.kickmaterial.api.KickMaterialService;
import com.byoutline.kickmaterial.dagger.GlobalScope;
import com.byoutline.kickmaterial.events.AccessTokenFetchedEvent;
import com.byoutline.kickmaterial.events.AccessTokenFetchingFailedEvent;
import com.byoutline.kickmaterial.model.AccessToken;
import com.byoutline.kickmaterial.model.EmailAndPass;
import com.byoutline.ottocachedfield.OttoCachedFieldWithArgBuilder;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import static com.byoutline.observablecachedfield.RetrofitHelper.apiValueProv;

/**
 * Created by Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 31.03.15.
 */
@GlobalScope
public class LoginManager {
    private CachedFieldWithArg<AccessToken, EmailAndPass> accessToken;
    private final AccessTokenProvider accessTokenProvider;

    @Inject
    public LoginManager(KickMaterialService service, AccessTokenProvider accessTokenProvider) {
        this.accessTokenProvider = accessTokenProvider;
        accessToken = new OttoCachedFieldWithArgBuilder<AccessToken, EmailAndPass>()
                .withValueProvider(apiValueProv(service::postGetAccessToken))
                .withSuccessEvent(new AccessTokenFetchedEvent())
                .withResponseErrorEvent(new AccessTokenFetchingFailedEvent())
                .withCustomSessionIdProvider(() -> "") // should be valid between sessions
                .build();
    }

    public void logIn(EmailAndPass emailAndPass) {
        accessToken.postValue(emailAndPass);
    }

    public void logOff() {
        accessTokenProvider.set("");
        accessToken.drop();
    }

    @Subscribe
    public void onAccessTokenFetched(AccessTokenFetchedEvent event) {
        accessTokenProvider.set(event.getResponse().accessToken);
    }
}
