package com.byoutline.kickmaterial.espressohelpers

import android.support.test.espresso.Espresso
import com.byoutline.cachedfield.utils.CachedFieldIdlingResource
import com.byoutline.kickmaterial.KickMaterialApp
import org.junit.After
import org.junit.Before

open class WithCachedFieldIdlingResourcesTest {
    private lateinit var cachedFieldIdlingResource: CachedFieldIdlingResource

    @Before
    fun registerIdlingResources() {
        cachedFieldIdlingResource = CachedFieldIdlingResource.from(KickMaterialApp.component.discoverField)
        Espresso.registerIdlingResources(cachedFieldIdlingResource)
    }

    @After
    fun unregisterIdlingResources() {
        Espresso.unregisterIdlingResources(cachedFieldIdlingResource)
    }
}