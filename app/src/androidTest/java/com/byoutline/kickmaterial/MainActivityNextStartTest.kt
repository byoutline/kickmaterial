package com.byoutline.kickmaterial

import android.support.test.espresso.NoMatchingViewException
import com.byoutline.kickmaterial.espressohelpers.DaggerRules
import com.byoutline.kickmaterial.espressohelpers.viewWithTextIsDisplayed
import org.junit.Rule
import org.junit.Test

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
class MainActivityNextStartTest {
    @get:Rule
    val activityRule = DaggerRules.userNextLaunchRule()

    @Test(expected = NoMatchingViewException::class)
    fun testHeaderShouldNotBeVisible() {
        viewWithTextIsDisplayed(R.string.explore)
    }
}
