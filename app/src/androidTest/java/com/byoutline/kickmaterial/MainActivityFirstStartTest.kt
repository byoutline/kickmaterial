package com.byoutline.kickmaterial

import com.byoutline.kickmaterial.espressohelpers.DaggerRules
import com.byoutline.kickmaterial.espressohelpers.viewWithTextIsDisplayed
import org.junit.Rule
import org.junit.Test

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
class MainActivityFirstStartTest {
    @get:Rule
    val activityRule = DaggerRules.userFirstLaunchRule()

    @Test
    fun testAllCategoriesShouldBeVisible() {
        viewWithTextIsDisplayed(R.string.all_categories)
    }

    @Test
    fun testHeaderShouldBeVisible() {
        viewWithTextIsDisplayed(R.string.explore)
    }
}
