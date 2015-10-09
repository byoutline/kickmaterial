package com.byoutline.kickmaterial.utils;

import timber.log.Timber;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Helper methods fot extracting query parameters from url.
 * Created by Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 30.03.15.
 */
public final class QueryParamsExtractor {

    private QueryParamsExtractor() {
    }

    public static Map<String, String> getQueryParams(String url) {
        try {
            URL u = new URL(url);
            return splitQuery(u);
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            Timber.e("Failed to get query params from url: " + url, e);
            return Collections.emptyMap();
        }
    }

    public static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
        Map<String, String> queryPairs = new LinkedHashMap<>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        String charsetName = "UTF-8";
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            String key = URLDecoder.decode(pair.substring(0, idx), charsetName);
            String value = URLDecoder.decode(pair.substring(idx + 1), charsetName);
            queryPairs.put(key, value);
        }
        return queryPairs;
    }

}
