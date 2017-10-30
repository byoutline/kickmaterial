package com.byoutline.kickmaterial.espressohelpers;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.byoutline.kickmaterial.KickMaterialApp;
import com.byoutline.kickmaterial.dagger.DaggerGlobalComponent;
import com.byoutline.kickmaterial.dagger.GlobalComponent;
import com.byoutline.kickmaterial.dagger.GlobalModule;
import com.byoutline.kickmaterial.features.projectlist.ProjectsListFragment;

/**
 * Methods returning test {@link GlobalComponent}s with changed injects.
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
final class TestComponents {
    private TestComponents() {
    }


    static GlobalComponent getFirstRunComponent(KickMaterialApp app) {
        return getComponent(app, getFirstRunSharedPrefs(app));
    }

    static GlobalComponent getNextRunComponent(KickMaterialApp app) {
        return getComponent(app, getNextRunSharedPrefs(app));
    }

    private static GlobalComponent getComponent(final KickMaterialApp app, final SharedPreferences sharedPrefs) {
        return DaggerGlobalComponent.builder()
                .globalModule(new GlobalModule(app) {
                    @Override
                    public SharedPreferences provideSharedPrefs() {
                        return sharedPrefs;
                    }
                })
                .build();
    }

    private static SharedPreferences getFirstRunSharedPrefs(KickMaterialApp app) {
        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(app);
        sharedPrefs.edit().clear().apply();
        return sharedPrefs;
    }

    private static SharedPreferences getNextRunSharedPrefs(KickMaterialApp app) {
        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(app);
        sharedPrefs.edit().putBoolean(ProjectsListFragment.PREFS_SHOW_HEADER, false).apply();
        return sharedPrefs;
    }
}
