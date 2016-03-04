package com.byoutline.kickmaterial;

import android.support.test.rule.ActivityTestRule;
import com.byoutline.kickmaterial.activities.MainActivity;
import com.byoutline.kickmaterial.espressohelpers.DaggerRules;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class MainActivityFirstStart8Test {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = DaggerRules.userFirstLaunchRule();

    @Test
    public void testAllCategoriesShouldBeVisible() {
        onView(withText(R.string.all_categories)).check(matches(isDisplayed()));
    }

    @Test
    public void testHeaderShouldBeVisible() {
//        onView(withId(R.id.project_recycler_view))
//                .perform(RecyclerViewActions.scrollTo(withText(R.string.explore)));
        onView(withText(R.string.explore))
                .check(matches(isDisplayed()));
    }
}
