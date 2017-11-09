package com.byoutline.kickmaterial.espressohelpers

import android.support.annotation.StringRes
import android.support.test.rule.ActivityTestRule
import android.view.View
import android.widget.EditText

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * Contains static methods returning custom espresso matchers. <br></br>
 * Use with import static CustomMatchers.*
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
fun withErrorSet(testRule: ActivityTestRule<*>, @StringRes expected: Int): Matcher<View> {
    return withErrorSet(getString(testRule, expected))
}

fun withErrorSet(expected: String): Matcher<View> {
    return object : TypeSafeMatcher<View>() {

        public override fun matchesSafely(view: View): Boolean {
            if (view !is EditText) {
                return false
            }
            val error = view.error ?: return false
            return expected == error.toString()
        }

        override fun describeTo(description: Description) {
            description.appendText("view should have error: ").appendValue(expected).appendText(" set")
        }
    }
}

fun getString(testRule: ActivityTestRule<*>, @StringRes stringId: Int) = testRule.activity.getString(stringId)
