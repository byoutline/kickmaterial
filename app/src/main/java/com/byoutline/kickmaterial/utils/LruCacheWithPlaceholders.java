package com.byoutline.kickmaterial.utils;

import android.content.Context;
import android.graphics.Bitmap;
import com.squareup.picasso.LruCache;
import org.apache.commons.collections.map.LRUMap;

import java.util.Map;


/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class LruCacheWithPlaceholders extends LruCache {
    private static final String KEY_SEPARATOR = "\n";
    private final Map<String, String> urlToKeyMap = new LRUMap(1024);

    public LruCacheWithPlaceholders(Context context) {
        super(context);
    }

    public Bitmap getPlaceholder(String uri) {
        String key = urlToKeyMap.get(uri);
        if (key == null) {
            return null;
        }
        return get(key);
    }

    @Override
    public void set(String key, Bitmap bitmap) {
        super.set(key, bitmap);
        int newlineIndex = key.indexOf(KEY_SEPARATOR);
        urlToKeyMap.put(key.substring(0, newlineIndex), key);
    }
}
