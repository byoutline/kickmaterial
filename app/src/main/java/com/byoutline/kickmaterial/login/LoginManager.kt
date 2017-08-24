package com.byoutline.kickmaterial.login

import com.byoutline.cachedfield.CachedFieldWithArg
import com.byoutline.cachedfield.ProviderWithArg
import com.byoutline.ibuscachedfield.util.RetrofitHelper.apiValueProv
import com.byoutline.kickmaterial.api.KickMaterialService
import com.byoutline.kickmaterial.dagger.GlobalScope
import com.byoutline.kickmaterial.model.AccessToken
import com.byoutline.kickmaterial.model.EmailAndPass
import com.byoutline.kickmaterial.utils.AccessTokenFetchedEvent
import com.byoutline.kickmaterial.utils.AccessTokenFetchingFailedEvent
import com.byoutline.ottocachedfield.OttoCachedFieldWithArgBuilder
import com.squareup.otto.Subscribe
import retrofit2.Call
import javax.inject.Inject

/**
 * Created by Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 31.03.15.
 */
@GlobalScope
class LoginManager @Inject
constructor(service: KickMaterialService, private val accessTokenProvider: AccessTokenProvider) {
    private val accessToken: CachedFieldWithArg<AccessToken, EmailAndPass> = OttoCachedFieldWithArgBuilder<AccessToken, EmailAndPass>()
            .withValueProvider(apiValueProv<AccessToken, EmailAndPass>(ProviderWithArg<Call<AccessToken>, EmailAndPass> { service.postGetAccessToken(it) }))
            .withSuccessEvent(AccessTokenFetchedEvent())
            .withResponseErrorEvent(AccessTokenFetchingFailedEvent())
            .withCustomSessionIdProvider { "" } // should be valid between sessions
            .build()

    fun logIn(emailAndPass: EmailAndPass) {
        accessToken.postValue(emailAndPass)
    }

    fun logOff() {
        accessTokenProvider.set("")
        accessToken.drop()
    }

    @Subscribe
    fun onAccessTokenFetched(event: AccessTokenFetchedEvent) {
        accessTokenProvider.set(event.response.accessToken)
    }
}
