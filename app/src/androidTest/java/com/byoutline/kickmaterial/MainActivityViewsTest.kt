package com.byoutline.kickmaterial

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.swipeUp
import android.support.test.espresso.matcher.ViewMatchers.withId
import com.byoutline.kickmaterial.espressohelpers.DaggerRules
import com.byoutline.kickmaterial.espressohelpers.viewWithIdIsDisplayed
import com.byoutline.kickmaterial.espressohelpers.viewWithTextIsDisplayed
import org.junit.Rule
import org.junit.Test


class MainActivityViewsTest {
    @get:Rule
    val activityRule = DaggerRules.userNextLaunchRule()
    @get:Rule
    val idlingResourceRule = DaggerRules.idlingResourceDiscoverField()

    @Test
    fun testHeaderShouldNotBeVisibleAfterScroll() {
        scrollDownView()
        viewWithIdIsDisplayed(R.id.toolbar_title_tv, false)
    }

    @Test
    fun testCategoriesBtnShouldBeVisible() = viewWithIdIsDisplayed(R.id.show_categories_fab, true)

    @Test
    fun testCategoriesBtnShouldNotBeVisibleAfterScroll() {
        scrollDownView()
        viewWithIdIsDisplayed(R.id.show_categories_fab, false)
    }

    @Test
    fun testProjectTileShouldBeVisible() = viewWithTextIsDisplayed("Smart thermos", true)

    private fun scrollDownView() = onView(withId(R.id.container)).perform(swipeUp())
}
