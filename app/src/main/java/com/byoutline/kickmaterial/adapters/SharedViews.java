package com.byoutline.kickmaterial.adapters;

import android.view.View;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class SharedViews {
    private final ArrayList<View> views = new ArrayList<>();

    public SharedViews(View... views) {
        Collections.addAll(this.views, views);
    }

    public ArrayList<View> asArrayList() {
        return views;
    }

    public View[] asArray() {
        return views.toArray(new View[views.size()]);
    }

    public void add(View view) {
        views.add(view);
    }
}
