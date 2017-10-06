package com.byoutline.kickmaterial.espressohelpers;

import android.content.SharedPreferences;
import com.byoutline.kickmaterial.dagger.AppComponent;
import com.byoutline.kickmaterial.dagger.AppModule;
import com.byoutline.kickmaterial.dagger.DaggerAppComponent;
import com.byoutline.kickmaterial.features.projectlist.ProjectsListFragment;
import com.byoutline.secretsauce.BaseApp;

/**
 * Methods returning test {@link AppComponent}s with changed injects.
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
final class TestComponents {
    private TestComponents() {
    }


    static AppComponent getFirstRunAppComponent(BaseApp app) {
        return getAppComponent(app, getFirstRunSharedPrefs(app));
    }

    static AppComponent getNextRunAppComponent(BaseApp app) {
        return getAppComponent(app, getNextRunSharedPrefs(app));
    }

    private static AppComponent getAppComponent(final BaseApp app, final SharedPreferences sharedPrefs) {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(app) {
                    @Override
                    public SharedPreferences provideSharedPrefs() {
                        return sharedPrefs;
                    }
                })
                .build();
    }

    private static SharedPreferences getFirstRunSharedPrefs(BaseApp app) {
        final SharedPreferences sharedPrefs = app.getSharedPrefs();
        sharedPrefs.edit().clear().apply();
        return sharedPrefs;
    }

    private static SharedPreferences getNextRunSharedPrefs(BaseApp app) {
        final SharedPreferences sharedPrefs = app.getSharedPrefs();
        sharedPrefs.edit().putBoolean(ProjectsListFragment.PREFS_SHOW_HEADER, false).apply();
        return sharedPrefs;
    }
}
