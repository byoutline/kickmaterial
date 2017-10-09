package com.byoutline.kickmaterial.robots;

import android.support.test.espresso.contrib.RecyclerViewActions;

import com.byoutline.kickmaterial.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.byoutline.kickmaterial.espressohelpers.CommonEspressoCalls.onBtnWithIdClick;
import static com.byoutline.kickmaterial.espressohelpers.CommonEspressoCalls.onViewWithTextClick;

public class UserScenarioRobot {

    private static final String DEFALT_PROJECT_NAME = "Smart thermos";
    public static final int ART_CATEGORY_POSITION = 1;

    public UserScenarioResultRobot openDefaultProjectDetails() {
        onViewWithTextClick(DEFALT_PROJECT_NAME);
        return new UserScenarioResultRobot();
    }

    public UserScenarioResultRobot openCategoryScreen() {
        onBtnWithIdClick(R.id.show_categories_fab);
        return new UserScenarioResultRobot();
    }

    public UserScenarioResultRobot selectDefaultCategory() {
        openCategoryScreen();
        onView(withId(R.id.categories_rv)).perform(
                RecyclerViewActions.actionOnItemAtPosition(ART_CATEGORY_POSITION, click()));
        return new UserScenarioResultRobot();
    }

}
