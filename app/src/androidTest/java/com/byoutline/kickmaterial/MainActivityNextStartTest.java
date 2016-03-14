package com.byoutline.kickmaterial;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.rule.ActivityTestRule;
import com.byoutline.kickmaterial.activities.MainActivity;
import com.byoutline.kickmaterial.espressohelpers.CachedFieldIdlingResource;
import com.byoutline.kickmaterial.espressohelpers.DaggerRules;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class MainActivityNextStartTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = DaggerRules.userNextLaunchRule();
    private CachedFieldIdlingResource cachedFieldIdlingResource;

    @Before
    public void registerIdlingResources() {
        cachedFieldIdlingResource = CachedFieldIdlingResource.from(KickMaterialApp.component.getDiscoverField());
        Espresso.registerIdlingResources(cachedFieldIdlingResource);
    }

    @After
    public void unregisterIdlingResources() {
        Espresso.unregisterIdlingResources(cachedFieldIdlingResource);
    }
    @Test(expected=NoMatchingViewException.class)
    public void testHeaderShouldNotBeVisible() {
        onView(withText(R.string.explore))
                .check(matches(isDisplayed()));
    }
}
