package com.byoutline.kickmaterial.espressohelpers;

import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;

import org.jetbrains.annotations.NotNull;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.byoutline.kickmaterial.espressohelpers.CustomMatchers.withErrorSet;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNot.not;

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

    public static void onViewWithTextClick(@NotNull String text) {
        onView(allOf(withText(text), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))).perform(click());
    }

    public static ViewInteraction checkErrorIsDisplayed(@IdRes int viewId, @StringRes int errorId, ActivityTestRule activityRule) {
        return onView(withId(viewId)).check(matches(withErrorSet(activityRule, errorId)));
    }

    public static void onBtnWithTextScrollAndClick(@StringRes int stringId) {
        onView(withText(stringId)).perform(scrollTo(), click());
    }

    public static void viewWithTextIsDisplayed(@StringRes int textId) {
        onView(withText(textId)).check(matches(isDisplayed()));
    }

    public static void viewWithTextIsDisplayed(@NotNull String text, boolean isDisplayed) {
        if (isDisplayed) {
            onView(withText(text)).check(matches(isDisplayed()));
        } else {
            onView(withText(text)).check(matches(not(isDisplayed())));
        }
    }

    public static void viewWithIdIsDisplayed(@IdRes int viewId, boolean isDisplayed) {
        if (isDisplayed) {
            onView(withId(viewId)).check(matches(isDisplayed()));
        } else {
            onView(withId(viewId)).check(matches(not(isDisplayed())));
        }
    }
}
