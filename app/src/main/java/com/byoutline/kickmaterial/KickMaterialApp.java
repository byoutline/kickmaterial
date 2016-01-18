package com.byoutline.kickmaterial;

import android.support.annotation.VisibleForTesting;
import com.byoutline.androidstubserver.AndroidStubServer;
import com.byoutline.kickmaterial.dagger.*;
import com.byoutline.kickmaterial.managers.AccessTokenProvider;
import com.byoutline.mockserver.NetworkType;
import com.byoutline.observablecachedfield.RetrofitHelper;
import com.byoutline.secretsauce.BaseApp;
import com.byoutline.secretsauce.utils.ViewUtils;
import com.squareup.otto.Bus;
import timber.log.Timber;

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
public class KickMaterialApp extends BaseApp {

    public static GlobalComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        AndroidStubServer.start(this, NetworkType.UMTS);
        RetrofitHelper.MSG_DISPLAYER = msg -> ViewUtils.showToast(msg, true);
        resetComponents();
    }


    @Override
    protected boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    @VisibleForTesting
    public synchronized void setComponents(GlobalComponent mainComponent, AppComponent appComponent) {
        component = mainComponent;
        init(appComponent);
//        component.inject(this);
    }

    public void resetComponents() {
        AppComponent appComponent = createAppComponent();
        GlobalComponent mainComponent = createGlobalComponent(appComponent.getBus(), appComponent.getAccessTokenProvider());
        setComponents(mainComponent, appComponent);
    }

    private GlobalComponent createGlobalComponent(Bus bus, AccessTokenProvider accessTokenProvider) {
        return DaggerGlobalComponent.builder()
                .globalModule(new GlobalModule(this, bus, accessTokenProvider))
                .build();
    }

    private AppComponent createAppComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }
}
