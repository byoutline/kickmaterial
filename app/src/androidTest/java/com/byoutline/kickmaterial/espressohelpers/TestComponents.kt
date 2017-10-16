package com.byoutline.kickmaterial.espressohelpers

import android.content.SharedPreferences
import com.byoutline.kickmaterial.dagger.AppModule
import com.byoutline.kickmaterial.dagger.DaggerAppComponent
import com.byoutline.kickmaterial.features.projectlist.ProjectsListFragment
import com.byoutline.secretsauce.BaseApp

/**
 * Methods returning test {@link AppComponent}s with changed injects.
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
internal object TestComponents {

    fun getFirstRunAppComponent(app: BaseApp) = getAppComponent(app, getFirstRunSharedPrefs(app))

    fun getNextRunAppComponent(app: BaseApp) = getAppComponent(app, getNextRunSharedPrefs(app))

    private fun getAppComponent(app: BaseApp, sharedPrefs: SharedPreferences)
            = DaggerAppComponent.builder()
            .appModule(object : AppModule(app) {
                public override fun provideSharedPrefs(): SharedPreferences {
                    return sharedPrefs
                }
            })
            .build()

    private fun getFirstRunSharedPrefs(app: BaseApp): SharedPreferences {
        val sharedPrefs = app.sharedPrefs
        sharedPrefs.edit().clear().apply()
        return sharedPrefs
    }

    private fun getNextRunSharedPrefs(app: BaseApp): SharedPreferences {
        val sharedPrefs = app.sharedPrefs
        sharedPrefs.edit().putBoolean(ProjectsListFragment.PREFS_SHOW_HEADER, false).apply()
        return sharedPrefs
    }
}
