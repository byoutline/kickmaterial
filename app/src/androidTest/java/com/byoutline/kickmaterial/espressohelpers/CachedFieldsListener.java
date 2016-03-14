package com.byoutline.kickmaterial.espressohelpers;

import com.byoutline.cachedfield.CachedField;
import com.byoutline.cachedfield.CachedFieldWithArg;
import com.byoutline.cachedfield.FieldState;
import com.byoutline.cachedfield.FieldStateListener;
import com.byoutline.cachedfield.cachedendpoint.CachedEndpointWithArg;
import com.byoutline.cachedfield.cachedendpoint.EndpointState;
import com.byoutline.cachedfield.cachedendpoint.EndpointStateListener;
import com.byoutline.cachedfield.cachedendpoint.StateAndValue;

import java.util.*;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class CachedFieldsListener implements FieldStateListener, EndpointStateListener {
    private final Collection<CachedField> fieldsNoArgs;
    private final Collection<CachedFieldWithArg> fields;
    private final Collection<CachedEndpointWithArg> endpoints;
    private CachedFieldsIdleListener listener;

    public CachedFieldsListener(Collection<CachedField> fieldsNoArgs,
                                Collection<CachedFieldWithArg> fields,
                                Collection<CachedEndpointWithArg> endpoints) {
        this.fieldsNoArgs = fieldsNoArgs;
        this.fields = fields;
        this.endpoints = endpoints;
    }

    @SuppressWarnings("unchecked")
    public void startTrackingFields() {
        for (CachedField field : fieldsNoArgs) {
            field.addStateListener(this);
        }
        for (CachedFieldWithArg field : fields) {
            field.addStateListener(this);
        }
        for (CachedEndpointWithArg endpoint : endpoints) {
            endpoint.addEndpointListener(this);
        }
    }

    @SuppressWarnings("unchecked")
    public void unregisterFromFields() {
        for (CachedField field : fieldsNoArgs) {
            field.removeStateListener(this);
        }
        for (CachedFieldWithArg field : fields) {
            field.removeStateListener(this);
        }
        for (CachedEndpointWithArg endpoint : endpoints) {
            endpoint.removeEndpointListener(this);
        }
        fieldsNoArgs.clear();
        fields.clear();
        endpoints.clear();
    }

    public void setListener(CachedFieldsIdleListener listener) {
        this.listener = listener;
    }


    public static CachedFieldsListener from(CachedField... fields) {
        return from(Arrays.asList(fields), Collections.<CachedFieldWithArg>emptyList(), Collections.<CachedEndpointWithArg>emptyList());
    }

    public static CachedFieldsListener from(CachedFieldWithArg... fields) {
        return from(Collections.<CachedField>emptyList(), Arrays.asList(fields), Collections.<CachedEndpointWithArg>emptyList());
    }

    public static CachedFieldsListener from(CachedEndpointWithArg... endpoints) {
        return from(Collections.<CachedField>emptyList(), Collections.<CachedFieldWithArg>emptyList(), Arrays.asList(endpoints));
    }


    public static CachedFieldsListener from(Collection<CachedField> fieldsNoArgs,
                                            Collection<CachedFieldWithArg> fields,
                                            Collection<CachedEndpointWithArg> endpoints) {
        CachedFieldsListener listener = new CachedFieldsListener(fieldsNoArgs, fields, endpoints);
        listener.startTrackingFields();
        return listener;
    }

    private synchronized void checkState() {
        boolean loading = getCurrentState();
        CachedFieldsIdleListener callback = this.listener;
        if(callback != null) {
            callback.onFieldsStateChange(loading);
        }
    }

    public synchronized boolean getCurrentState() {
        boolean loading = false;
        List<CachedField> currentFieldsNoArgs = new ArrayList<>(fieldsNoArgs);
        List<CachedFieldWithArg> currentFields = new ArrayList<>(fields);
        List<CachedEndpointWithArg> currentEndpoints = new ArrayList<>(endpoints);
        for (CachedField field : currentFieldsNoArgs) {
            if (field.getState() == FieldState.CURRENTLY_LOADING) {
                loading = true;
                break;
            }
        }
        if (!loading) {
            for (CachedFieldWithArg field : currentFields) {
                if (field.getState() == FieldState.CURRENTLY_LOADING) {
                    loading = true;
                    break;
                }
            }
        }
        if (!loading) {
            for (CachedEndpointWithArg endpoint : currentEndpoints) {
                if (endpoint.getStateAndValue().getState() == EndpointState.DURING_CALL) {
                    loading = true;
                    break;
                }
            }
        }
        return loading;
    }

    @Override
    public void fieldStateChanged(FieldState fieldState) {
        checkState();
    }

    @Override
    public void endpointStateChanged(StateAndValue endpointState) {
        checkState();
    }

    public synchronized String getRegisterCount() {
        return String.format("fields without arg: %d, fields with arg: %d, endpoints: %d", fieldsNoArgs.size(), fields.size(), endpoints.size());
    }
}

interface CachedFieldsIdleListener {
    void onFieldsStateChange(boolean loading);
}