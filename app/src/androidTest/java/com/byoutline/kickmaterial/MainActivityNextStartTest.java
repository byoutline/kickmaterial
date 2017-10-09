package com.byoutline.kickmaterial;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.rule.ActivityTestRule;

import com.byoutline.kickmaterial.espressohelpers.DaggerRules;
import com.byoutline.kickmaterial.espressohelpers.WithCachedFieldIdlingResourcesTest;
import com.byoutline.kickmaterial.features.projectlist.MainActivity;

import org.junit.Rule;
import org.junit.Test;

import static com.byoutline.kickmaterial.espressohelpers.CommonEspressoCalls.viewWithTextIsDisplayed;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class MainActivityNextStartTest extends WithCachedFieldIdlingResourcesTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = DaggerRules.INSTANCE.userNextLaunchRule();

    @Test(expected = NoMatchingViewException.class)
    public void testHeaderShouldNotBeVisible() {
        viewWithTextIsDisplayed(R.string.explore);
    }
}
