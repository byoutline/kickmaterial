package com.byoutline.kickmaterial;

import android.support.test.rule.ActivityTestRule;

import com.byoutline.kickmaterial.espressohelpers.DaggerRules;
import com.byoutline.kickmaterial.espressohelpers.WithCachedFieldIdlingResourcesTest;
import com.byoutline.kickmaterial.features.projectlist.MainActivity;
import com.byoutline.kickmaterial.robots.UserScenarioRobot;

import org.junit.Rule;
import org.junit.Test;

public class UserScenarioTest extends WithCachedFieldIdlingResourcesTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = DaggerRules.INSTANCE.userNextLaunchRule();

    private UserScenarioRobot transitionsRobot = new UserScenarioRobot();

    @Test
    public void testCheckDetailsScreenIsDisplayedAfterProjectClick() {
        transitionsRobot.openDefaultProjectDetails()
                .verifyDetailsScreenIsDisplayed();
    }

    @Test
    public void testCheckCategoryScreenIsDisplayedAfterFabBtnClick() {
        transitionsRobot.openCategoryScreen()
                .verifyCategoryScreenIsDisplayed();
    }

    @Test
    public void testSelectedCategoryIsDisplayed() {
        transitionsRobot.selectDefaultCategory()
                .verifyCategoryHasChangedToDefault();
    }
}
