package com.byoutline.kickmaterial;

import android.support.test.rule.ActivityTestRule;

import com.byoutline.kickmaterial.espressohelpers.DaggerRules;
import com.byoutline.kickmaterial.espressohelpers.WithCachedFieldIdlingResourcesTest;
import com.byoutline.kickmaterial.features.projectlist.MainActivity;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.byoutline.kickmaterial.espressohelpers.CommonEspressoCalls.viewWithIdIsDisplayed;
import static com.byoutline.kickmaterial.espressohelpers.CommonEspressoCalls.viewWithTextIsDisplayed;


public class MainActivityViewsTest extends WithCachedFieldIdlingResourcesTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = DaggerRules.INSTANCE.userNextLaunchRule();

    @Test
    public void testHeaderShouldNotBeVisibleAfterScroll() {
        scrollDownView();
        viewWithIdIsDisplayed(R.id.toolbar_title_tv, false);
    }

    @Test
    public void testCategoriesBtnShouldBeVisible() {
        viewWithIdIsDisplayed(R.id.show_categories_fab, true);
    }

    @Test
    public void testCategoriesBtnShouldNotBeVisibleAfterScroll() {
        scrollDownView();
        viewWithIdIsDisplayed(R.id.show_categories_fab, false);
    }

    @Test
    public void testProjectTileShouldBeVisible() {
        viewWithTextIsDisplayed("Smart thermos");
    }

    private void scrollDownView() {
        onView(withId(R.id.container)).perform(swipeUp());
    }
}
