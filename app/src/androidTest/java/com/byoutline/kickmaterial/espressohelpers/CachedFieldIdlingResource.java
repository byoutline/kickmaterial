package com.byoutline.kickmaterial.espressohelpers;

import android.support.test.espresso.IdlingResource;
import com.byoutline.cachedfield.CachedField;
import com.byoutline.cachedfield.CachedFieldWithArg;
import com.byoutline.cachedfield.cachedendpoint.CachedEndpointWithArg;

import java.util.Collection;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class CachedFieldIdlingResource implements IdlingResource, CachedFieldsIdleListener {
    private volatile ResourceCallback resourceCallback;
    private boolean loading = false;
    private final CachedFieldsListener cachedFieldsToObserve;

    public CachedFieldIdlingResource(CachedFieldsListener cachedFieldsToObserve) {
        this.cachedFieldsToObserve = cachedFieldsToObserve;
        this.cachedFieldsToObserve.setListener(this);
        loading = this.cachedFieldsToObserve.getCurrentState();
    }

    @Override
    public String getName() {
        return getClass().getSimpleName() + " with " + cachedFieldsToObserve.getRegisterCount();
    }

    @Override
    public boolean isIdleNow() {
        return !loading;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        this.resourceCallback = resourceCallback;
    }

    @Override
    public void onFieldsStateChange(boolean loading) {
        if (this.loading != loading) {
            this.loading = loading;
            if (isIdleNow()) {
                resourceCallback.onTransitionToIdle();
            }
        }
    }

    public static CachedFieldIdlingResource from(CachedField... fields) {
        return new CachedFieldIdlingResource(CachedFieldsListener.from(fields));
    }

    public static CachedFieldIdlingResource from(CachedFieldWithArg... fields) {
        return new CachedFieldIdlingResource(CachedFieldsListener.from(fields));
    }

    public static CachedFieldIdlingResource from(CachedEndpointWithArg... endpoints) {
        return new CachedFieldIdlingResource(CachedFieldsListener.from(endpoints));
    }

    public static CachedFieldIdlingResource from(Collection<CachedField> fieldsNoArgs,
                                                 Collection<CachedFieldWithArg> fields,
                                                 Collection<CachedEndpointWithArg> endpoints) {
        return new CachedFieldIdlingResource(CachedFieldsListener.from(fieldsNoArgs, fields, endpoints));
    }
}
