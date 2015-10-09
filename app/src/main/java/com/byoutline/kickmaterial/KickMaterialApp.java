package com.byoutline.kickmaterial;

import com.byoutline.androidstubserver.AndroidStubServer;
import com.byoutline.kickmaterial.dagger.*;
import com.byoutline.mockserver.NetworkType;
import com.byoutline.secretsauce.BaseApp;
import timber.log.Timber;

import javax.annotation.Nonnull;

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
public class KickMaterialApp extends BaseApp {

    private GlobalComponent globalComponent;
    public static GlobalComponent component;

    @Override
    protected void initComponents() {
        globalComponent = DaggerGlobalComponent.builder()
                .globalModule(new GlobalModule(this))
                .build();
        component = globalComponent;
    }

    @Nonnull
    @Override
    protected AppComponent createAppComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(globalComponent.getAccessTokenProvider()))
                .globalComponent(globalComponent)
                .build();
    }

    @Override
    protected boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    @Nonnull
    @Override
    protected String getDefaultFontName() {
        return "OpenSansRegular.ttf";
    }

    @Override
    protected int getContainerId() {
        return R.id.container;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        AndroidStubServer.start(this, NetworkType.UMTS);
    }
}
