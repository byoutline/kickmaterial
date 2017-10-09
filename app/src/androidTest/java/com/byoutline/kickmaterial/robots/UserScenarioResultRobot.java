package com.byoutline.kickmaterial.robots;


import com.byoutline.kickmaterial.R;

import static com.byoutline.kickmaterial.espressohelpers.CommonEspressoCalls.viewWithIdIsDisplayed;
import static com.byoutline.kickmaterial.espressohelpers.CommonEspressoCalls.viewWithTextIsDisplayed;

public class UserScenarioResultRobot {

    public static final String ART_CATEGORY_NAME = "Art";

    public void verifyCategoryScreenIsDisplayed() {
        viewWithIdIsDisplayed(R.id.select_category_tv, true);
    }

    public void verifyDetailsScreenIsDisplayed() {
        viewWithIdIsDisplayed(R.id.play_video_btn, true);
    }

    public void verifyCategoryHasChangedToDefault() {
        viewWithTextIsDisplayed(ART_CATEGORY_NAME, true);
    }
}
