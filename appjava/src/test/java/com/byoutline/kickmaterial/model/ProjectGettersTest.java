package com.byoutline.kickmaterial.model;

import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Sebastian Kacprzak on 25.03.15.
 */
public class ProjectGettersTest {

    @Ignore(value = "Not yet implemented")
    @Test
    public void getTimeLeftLeftShouldReturnFundedIfTimeHasPassed() {
        // given
        Project instance = new Project();
        DateTime now = DateTime.now();
        instance.launchedAt = now.minusDays(2);
        instance.deadline = now.minus(1);
        // when
        ProjectTime result = instance.getTimeLeft(now);
        // then
        assertEquals("Funded", result.value);
    }

    @Test
    public void getTimeLeftShouldENDEDIfTimeHasPassed() {
        // given
        Project instance = new Project();
        DateTime now = DateTime.now();
        instance.launchedAt = now.minusDays(2);
        instance.deadline = now.minus(1);
        // when
        ProjectTime result = instance.getTimeLeft(now);
        // then
        assertEquals("ENDED", result.value);
    }
}