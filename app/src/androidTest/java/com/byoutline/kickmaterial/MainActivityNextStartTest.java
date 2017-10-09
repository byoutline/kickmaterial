package com.byoutline.kickmaterial;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.rule.ActivityTestRule;

import com.byoutline.kickmaterial.espressohelpers.DaggerRules;
import com.byoutline.kickmaterial.espressohelpers.WithCachedFieldIdlingResourcesTest;
import com.byoutline.kickmaterial.features.projectlist.MainActivity;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class MainActivityNextStartTest extends WithCachedFieldIdlingResourcesTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = DaggerRules.INSTANCE.userNextLaunchRule();

    @Test(expected = NoMatchingViewException.class)
    public void testHeaderShouldNotBeVisible() {
        onView(withText(R.string.explore))
                .check(matches(isDisplayed()));
    }
}
