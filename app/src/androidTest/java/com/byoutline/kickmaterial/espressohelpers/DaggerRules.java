package com.byoutline.kickmaterial.espressohelpers;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.support.test.rule.ActivityTestRule;
import com.byoutline.kickmaterial.KickMaterialApp;
import com.byoutline.kickmaterial.activities.MainActivity;
import com.byoutline.kickmaterial.dagger.AppComponent;
import com.byoutline.kickmaterial.dagger.DaggerGlobalComponent;
import com.byoutline.kickmaterial.dagger.GlobalComponent;
import com.byoutline.kickmaterial.dagger.GlobalModule;

/**
 * Methods returning custom {@link ActivityTestRule}s that set test {@link AppComponent}.
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public final class DaggerRules {
    private DaggerRules() {
    }

    public static ActivityTestRule<MainActivity> userFirstLaunchRule() {
        return getActivityRule(TestComponents::getFirstRunAppComponent,
                MainActivity.class);
    }

    public static ActivityTestRule<MainActivity> userNextLaunchRule() {
        return getActivityRule(TestComponents::getNextRunAppComponent,
                MainActivity.class);
    }

    public static <ACTIVITY extends Activity> ActivityTestRule<ACTIVITY> getActivityRule(final AppComponentProvider mainComponentProv,
                                                                                         Class<ACTIVITY> clazz) {
        final Handler mainHandler = new Handler(Looper.getMainLooper());
        return new DaggerActivityTestRule<>(clazz, (application, activity) -> {
            final KickMaterialApp app = (KickMaterialApp) application;
            final AppComponent appComponent = mainComponentProv.getComponent(app);
            final GlobalComponent globalComponent = DaggerGlobalComponent.builder()
                    .globalModule(new GlobalModule(app, appComponent.getBus(), appComponent.getAccessTokenProvider()))
                    .build();
            mainHandler.post(() -> app.setComponents(globalComponent, appComponent));
        });
    }

    public interface AppComponentProvider {
        AppComponent getComponent(KickMaterialApp app);
    }
}

