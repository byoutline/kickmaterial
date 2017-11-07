package com.byoutline.kickmaterial.espressohelpers

import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.byoutline.kickmaterial.KickMaterialApp
import com.byoutline.kickmaterial.dagger.AppComponent
import com.byoutline.kickmaterial.dagger.AppModule
import com.byoutline.kickmaterial.dagger.DaggerAppComponent
import com.byoutline.kickmaterial.features.projectlist.ProjectsListFragment

/**
 * Methods returning test [AppComponent]s with changed injects.
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
internal object TestComponents {

    fun getFirstRunComponent(app: KickMaterialApp) = getComponent(app, getFirstRunSharedPrefs(app))

    fun getNextRunComponent(app: KickMaterialApp) = getComponent(app, getNextRunSharedPrefs(app))

    private fun getComponent(app: KickMaterialApp, sharedPrefs: SharedPreferences): AppComponent {
        return DaggerAppComponent.builder()
                .appModule(object : AppModule(app) {
                    override fun provideSharedPrefs() = sharedPrefs

                    override fun provideAnimationDurationMultiplier() = 0
                })
                .build()
    }

    private fun getFirstRunSharedPrefs(app: KickMaterialApp): SharedPreferences {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(app)
        sharedPrefs.edit().clear().apply()
        return sharedPrefs
    }

    private fun getNextRunSharedPrefs(app: KickMaterialApp): SharedPreferences {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(app)
        sharedPrefs.edit().putBoolean(ProjectsListFragment.PREFS_SHOW_HEADER, false).apply()
        return sharedPrefs
    }
}
