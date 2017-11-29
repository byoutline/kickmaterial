package com.byoutline.kickmaterial.robots


import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.espressohelpers.viewWithIdIsDisplayed
import com.byoutline.kickmaterial.espressohelpers.viewWithTextIsDisplayed

private const val ART_CATEGORY_NAME = "Art"

class UserScenarioResultRobot {

    fun verifyCategoryScreenIsDisplayed() = viewWithIdIsDisplayed(R.id.select_category_tv, true)

    fun verifyDetailsScreenIsDisplayed() = viewWithIdIsDisplayed(R.id.play_video_btn, true)

    fun verifyCategoryHasChangedToDefault() = viewWithTextIsDisplayed(ART_CATEGORY_NAME, true)
}
