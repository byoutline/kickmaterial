package com.byoutline.kickmaterial.model;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sebastian Kacprzak on 24.03.15.
 */
public class DiscoverResponseTest extends TestCase {
    public void testGetMoreProjectsUrl() {
        // given
        String input = "https://api.test.com/v1/discover?include_potd=true&page=2&per_page=12&signature=1427292197.5d3c71b32776e8bff2d2314d7349c7a837d06ce0&sort=magic&staff_picks=true";
        DiscoverUrls urls = new DiscoverUrls(new DiscoverUrlsApi(input));
        DiscoverResponse instance = new DiscoverResponse(null, urls);
        Map<String, String> exp = new HashMap<>();
        exp.put("include_potd", "true");
        exp.put("page", "2");
        exp.put("per_page", "12");
        exp.put("signature", "1427292197.5d3c71b32776e8bff2d2314d7349c7a837d06ce0");
        exp.put("sort", "magic");
        exp.put("staff_picks", "true");
        // when
        Map<String, String> result = instance.getMoreProjectsUrl();
        // then
        assertEquals(result, exp);
    }

}