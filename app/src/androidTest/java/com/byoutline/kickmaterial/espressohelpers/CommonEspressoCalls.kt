package com.byoutline.kickmaterial.espressohelpers

import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.scrollTo
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.view.View
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.IsNot.not

/**
 * Static methods that wraps most common espresso calls. <br />
 * This makes test a bit more readable, and also will make switching from espresso easier(if ever such a need occurs).
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
fun onBtnWithIdClick(@IdRes btnId: Int) = onView(withId(btnId)).perform(click())

fun onViewWithTextClick(text: String)
        = onView(allOf<View>(withText(text), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))).perform(click())

fun checkErrorIsDisplayed(@IdRes viewId: Int, @StringRes errorId: Int, activityRule: ActivityTestRule<*>): ViewInteraction {
    return onView(withId(viewId)).check(matches(withErrorSet(activityRule, errorId)))
}

fun onBtnWithTextScrollAndClick(@StringRes stringId: Int) = onView(withText(stringId)).perform(scrollTo(), click())

fun viewWithTextIsDisplayed(@StringRes textId: Int) = onView(withText(textId)).check(matches(isDisplayed()))

fun viewWithTextIsDisplayed(text: String, isDisplayed: Boolean) {
    if (isDisplayed) {
        onView(withText(text)).check(matches(isDisplayed()))
    } else {
        onView(withText(text)).check(matches(not<View>(isDisplayed())))
    }
}

fun viewWithIdIsDisplayed(@IdRes viewId: Int, isDisplayed: Boolean) {
    if (isDisplayed) {
        onView(withId(viewId)).check(matches(isDisplayed()))
    } else {
        onView(withId(viewId)).check(matches(not<View>(isDisplayed())))
    }
}

