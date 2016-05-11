package com.byoutline.kickmaterial.espressohelpers;

import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.byoutline.kickmaterial.espressohelpers.CustomMatchers.withErrorSet;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Static methods that wraps most common espresso calls. <br />
 * This makes test a bit more readable, and also will make switching from espresso easier(if ever such a need occurs).
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class CommonEspressoCalls {
    public static void onBtnWithIdClick(@IdRes int btnId) {
        onView(withId(btnId)).perform(click());
    }

    public static void onBtnWithTextClick(@StringRes int stringId) {
        onView(allOf(withText(stringId), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))).perform(click());
    }

    public static ViewInteraction checkErrorIsDisplayed(@IdRes int viewId, @StringRes int errorId, ActivityTestRule activityRule) {
        return onView(withId(viewId)).check(matches(withErrorSet(activityRule, errorId)));
    }

    public static void onBtnWithTextScrollAndClick(@StringRes int stringId) {
        onView(withText(stringId)).perform(scrollTo(), click());
    }
}
