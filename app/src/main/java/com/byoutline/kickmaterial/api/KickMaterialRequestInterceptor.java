package com.byoutline.kickmaterial.api;

import android.text.TextUtils;
import com.byoutline.kickmaterial.dagger.GlobalScope;
import com.byoutline.kickmaterial.managers.AccessTokenProvider;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import javax.inject.Inject;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 05.05.14.
 */
@GlobalScope
public class KickMaterialRequestInterceptor implements Interceptor {

    private final AccessTokenProvider accessTokenProvider;

    @Inject
    public KickMaterialRequestInterceptor(AccessTokenProvider accessTokenProvider) {
        this.accessTokenProvider = accessTokenProvider;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        HttpUrl.Builder urlBuilder = original.url().newBuilder();
        addBasicHeaders(urlBuilder);
        String accessToken = accessTokenProvider.get();
        if (!TextUtils.isEmpty(accessToken)) {
            urlBuilder.addQueryParameter("oauth_token", accessToken);
        }
        HttpUrl newUrl = urlBuilder.build();
        Request newRequest = original.newBuilder().url(newUrl).build();
        return chain.proceed(newRequest);
    }

    private static void addBasicHeaders(HttpUrl.Builder builder) {
        for (Map.Entry<String, String> query : getBasicQueries()) {
            builder.addQueryParameter(query.getKey(), query.getValue());
        }
    }

    public static List<Map.Entry<String, String>> getBasicQueries() {
        List<Map.Entry<String, String>> queries = new ArrayList<>();
        queries.add(new AbstractMap.SimpleEntry<>("client_id", "SECRET_KEY"));
        return queries;
    }
}
