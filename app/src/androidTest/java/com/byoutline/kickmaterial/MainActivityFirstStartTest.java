package com.byoutline.kickmaterial;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import com.byoutline.cachedfield.utils.CachedFieldIdlingResource;
import com.byoutline.kickmaterial.espressohelpers.DaggerRules;
import com.byoutline.kickmaterial.features.projectlist.MainActivity;
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
public class MainActivityFirstStartTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = DaggerRules.INSTANCE.userFirstLaunchRule();
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

    @Test
    public void testAllCategoriesShouldBeVisible() {
        onView(withText(R.string.all_categories)).check(matches(isDisplayed()));
    }

    @Test
    public void testHeaderShouldBeVisible() {
        onView(withText(R.string.explore))
                .check(matches(isDisplayed()));
    }
}
