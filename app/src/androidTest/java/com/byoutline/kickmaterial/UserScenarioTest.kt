package com.byoutline.kickmaterial

import com.byoutline.kickmaterial.espressohelpers.DaggerRules
import com.byoutline.kickmaterial.robots.UserScenarioRobot
import org.junit.Rule
import org.junit.Test

class UserScenarioTest {
    @get:Rule
    val activityRule = DaggerRules.userNextLaunchRule()
    @get:Rule
    val idlingResourceRule = DaggerRules.idlingResourceDiscoverField()

    private val transitionsRobot = UserScenarioRobot()

    @Test
    fun testCheckDetailsScreenIsDisplayedAfterProjectClick()
            = transitionsRobot.openDefaultProjectDetails().verifyDetailsScreenIsDisplayed()


    @Test
    fun testCheckCategoryScreenIsDisplayedAfterFabBtnClick()
            = transitionsRobot.openCategoryScreen().verifyCategoryScreenIsDisplayed()

    @Test
    fun testSelectedCategoryIsDisplayed()
            = transitionsRobot.selectDefaultCategory().verifyCategoryHasChangedToDefault()

}
