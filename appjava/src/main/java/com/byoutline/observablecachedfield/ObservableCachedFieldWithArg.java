package com.byoutline.observablecachedfield;

import com.byoutline.cachedfield.CachedFieldWithArgImpl;
import com.byoutline.cachedfield.ErrorListenerWithArg;
import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.cachedfield.SuccessListenerWithArg;

import javax.inject.Provider;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class ObservableCachedFieldWithArg<RETURN_TYPE, ARG_TYPE>
        extends CachedFieldWithArgImpl<RETURN_TYPE, ARG_TYPE> {
    // TODO: move
    static Provider<String> defaultSessionIdProvider;
    static ExecutorService defaultValueGetterExecutor;
    static Executor defaultStateListenerExecutor;
    private final ObservableField<RETURN_TYPE> observableValue;

    public ObservableCachedFieldWithArg(Provider<String> sessionIdProvider,
                                 ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueGetter,
                                        SuccessListenerWithArg<RETURN_TYPE, ARG_TYPE> additionalSuccessListener,
                                        ErrorListenerWithArg<ARG_TYPE> additionalErrorListener,
                                 ExecutorService valueGetterExecutor, Executor stateListenerExecutor) {
        this(sessionIdProvider, valueGetter,
                additionalSuccessListener, additionalErrorListener,
                valueGetterExecutor, stateListenerExecutor,
                new ObservableField<RETURN_TYPE>());
    }

    private ObservableCachedFieldWithArg(Provider<String> sessionIdProvider,
                                         ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueGetter,
                                         final SuccessListenerWithArg<RETURN_TYPE, ARG_TYPE> additionalSuccessListener,
                                         final ErrorListenerWithArg<ARG_TYPE> additionalErrorListener,
                                         ExecutorService valueGetterExecutor, Executor stateListenerExecutor,
                                         final ObservableField<RETURN_TYPE> observableValue) {
        super(sessionIdProvider,
                valueGetter,
                new SuccessListenerWithArg<RETURN_TYPE, ARG_TYPE>() {

                    @Override
                    public void valueLoaded(RETURN_TYPE value, ARG_TYPE arg) {
                        observableValue.set(value);
                        additionalSuccessListener.valueLoaded(value, arg);
                    }
                },
                new ErrorListenerWithArg<ARG_TYPE>() {

                    @Override
                    public void valueLoadingFailed(Exception ex, ARG_TYPE arg) {
                        observableValue.set(null);
                        additionalErrorListener.valueLoadingFailed(ex, arg);
                    }
                },
                valueGetterExecutor, stateListenerExecutor);
        this.observableValue = observableValue;
    }

    public ObservableField<RETURN_TYPE> observable() {
        return observableValue;
    }

    static void init(Provider<String> defaultSessionIdProvider,
                     ExecutorService defaultValueGetterExecutor, Executor defaultStateListenerExecutor) {
        // TODO: move
        ObservableCachedFieldWithArg.defaultSessionIdProvider = defaultSessionIdProvider;
        ObservableCachedFieldWithArg.defaultValueGetterExecutor = defaultValueGetterExecutor;
        ObservableCachedFieldWithArg.defaultStateListenerExecutor = defaultStateListenerExecutor;
    }
}
