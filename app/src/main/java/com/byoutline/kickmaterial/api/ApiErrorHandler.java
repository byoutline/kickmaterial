package com.byoutline.kickmaterial.api;

import android.text.TextUtils;
import com.byoutline.kickmaterial.dagger.GlobalScope;
import com.byoutline.secretsauce.utils.ViewUtils;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import timber.log.Timber;

import javax.inject.Inject;

/**
 * Intercepts errors.
 */
@GlobalScope
public class ApiErrorHandler implements ErrorHandler {

    @Inject
    public ApiErrorHandler() {
    }

    @Override
    public Throwable handleError(RetrofitError error) {
        if (!TextUtils.isEmpty(error.getLocalizedMessage())) {
            ViewUtils.showToast(error.getLocalizedMessage(), true);
        }
        Timber.e("Api call failed", error.getCause());
        return error;
    }
}
