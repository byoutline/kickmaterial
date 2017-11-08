package com.byoutline.kickmaterial.espressohelpers

import android.app.Activity
import android.app.Application
import android.os.Handler
import android.os.Looper
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.base.DefaultFailureHandler
import android.support.test.rule.ActivityTestRule
import com.byoutline.cachedfield.CachedFieldWithArg
import com.byoutline.cachedfield.utils.CachedFieldIdlingResource
import com.byoutline.kickmaterial.KickMaterialApp
import com.byoutline.kickmaterial.dagger.GlobalComponent
import com.byoutline.kickmaterial.features.projectlist.MainActivity
import com.squareup.spoon.Spoon
import org.junit.rules.ExternalResource
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Methods returning custom [ActivityTestRule]s that set test [AppComponent].

 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
object DaggerRules {

    fun userFirstLaunchRule(useCachedFieldIdlRes: Boolean = true): ActivityTestRule<MainActivity>
            = getActivityRule({ TestComponents.getFirstRunComponent(it) }, MainActivity::class.java, useCachedFieldIdlRes)

    fun userNextLaunchRule(useCachedFieldIdlRes: Boolean = true): ActivityTestRule<MainActivity>
            = getActivityRule({ TestComponents.getNextRunComponent(it) }, MainActivity::class.java, useCachedFieldIdlRes)

    private fun <ACTIVITY : Activity> getActivityRule(
            mainComponentProv: (KickMaterialApp) -> GlobalComponent,
            clazz: Class<ACTIVITY>, useCachedFieldIdlRes: Boolean
    ): ActivityTestRule<ACTIVITY> {
        val mainHandler = Handler(Looper.getMainLooper())
        return DaggerActivityTestRule(clazz, beforeActivityLaunchedAction = { application ->
            val app = application as KickMaterialApp
            val globalComponent = mainComponentProv(app)
            mainHandler.post { app.setComponents(globalComponent) }
        }, useCachedFieldIdlRes = useCachedFieldIdlRes)
    }
}

class DaggerActivityTestRule<T : Activity>(
        activityClass: Class<T>, initialTouchMode: Boolean = false,
        launchActivity: Boolean = true,
        private val beforeActivityLaunchedAction: (Application) -> Unit = {},
        private val useCachedFieldIdlRes: Boolean
) : ActivityTestRule<T>(activityClass, initialTouchMode, launchActivity) {

    private lateinit var cachedFieldIdlingResource: CachedFieldIdlingResource

    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        beforeActivityLaunchedAction(InstrumentationRegistry.getInstrumentation()
                .targetContext.applicationContext as Application)
        if (useCachedFieldIdlRes) {
            cachedFieldIdlingResource = CachedFieldIdlingResource.from(KickMaterialApp.component.discoverField)
            Espresso.registerIdlingResources(cachedFieldIdlingResource)
        }
    }

    override fun afterActivityFinished() {
        if (useCachedFieldIdlRes) {
            Espresso.unregisterIdlingResources(cachedFieldIdlingResource)
        }
        super.afterActivityFinished()
    }

    override fun apply(base: Statement, description: Description): Statement {
        // On Ci take screenshot if test fails
        if (System.getenv("CIRCLECI") != null) {
            Espresso.setFailureHandler { error, viewMatcher ->
                Spoon.screenshot(activity, error.javaClass.simpleName, description.className, description.methodName)
                DefaultFailureHandler(activity).handle(error, viewMatcher)
            }
        }
        return super.apply(base, description)
    }
}


class CachedFieldIdlingResourceRule(private val cachedFieldWithArg: CachedFieldWithArg<*, *>) : ExternalResource() {
    private lateinit var cachedFieldIdlingResource: CachedFieldIdlingResource

    @Throws(Throwable::class)
    override fun before() {
        cachedFieldIdlingResource = CachedFieldIdlingResource.from(cachedFieldWithArg)
        Espresso.registerIdlingResources(cachedFieldIdlingResource)
    }

    override fun after() {
        Espresso.unregisterIdlingResources(cachedFieldIdlingResource)
    }
}

