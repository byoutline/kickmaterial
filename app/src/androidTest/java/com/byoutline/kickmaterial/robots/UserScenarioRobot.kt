package com.byoutline.kickmaterial.robots

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.v7.widget.RecyclerView
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.espressohelpers.onBtnWithIdClick
import com.byoutline.kickmaterial.espressohelpers.onViewWithTextClick

private const val DEFALT_PROJECT_NAME = "Smart thermos"
private const val ART_CATEGORY_POSITION = 1

class UserScenarioRobot {

    fun openDefaultProjectDetails(): UserScenarioResultRobot {
        onViewWithTextClick(DEFALT_PROJECT_NAME)
        return UserScenarioResultRobot()
    }

    fun openCategoryScreen(): UserScenarioResultRobot {
        onBtnWithIdClick(R.id.show_categories_fab)
        return UserScenarioResultRobot()
    }

    fun selectDefaultCategory(): UserScenarioResultRobot {
        openCategoryScreen()
        onView(withId(R.id.categories_rv)).perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(ART_CATEGORY_POSITION, click()))
        return UserScenarioResultRobot()
    }
}
