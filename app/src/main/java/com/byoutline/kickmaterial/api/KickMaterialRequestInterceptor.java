package com.byoutline.kickmaterial.api;

import android.text.TextUtils;
import com.byoutline.kickmaterial.dagger.GlobalScope;
import com.byoutline.kickmaterial.managers.AccessTokenProvider;
import retrofit.RequestInterceptor;

import javax.inject.Inject;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 05.05.14.
 */
@GlobalScope
public class KickMaterialRequestInterceptor implements RequestInterceptor {

    private final AccessTokenProvider accessTokenProvider;

    @Inject
    public KickMaterialRequestInterceptor(AccessTokenProvider accessTokenProvider) {
        this.accessTokenProvider = accessTokenProvider;
    }

    @Override
    public void intercept(RequestFacade request) {
        addBasicHeaders(request);
        String accessToken = accessTokenProvider.get();
        if (!TextUtils.isEmpty(accessToken)) {
            request.addQueryParam("oauth_token", accessToken);
        }
    }

    private static void addBasicHeaders(RequestFacade request) {
        for (Map.Entry<String, String> query : getBasicQueries()) {
            request.addQueryParam(query.getKey(), query.getValue());
        }
    }

    public static List<Map.Entry<String, String>> getBasicQueries() {
        List<Map.Entry<String, String>> queries = new ArrayList<>();
        queries.add(new AbstractMap.SimpleEntry<>("client_id", "SECRET_KEY"));
        return queries;
    }
}
