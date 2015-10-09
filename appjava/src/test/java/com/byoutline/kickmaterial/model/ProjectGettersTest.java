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
        instance.launchedAt = DateTime.now().minusDays(2);
        instance.deadline = DateTime.now().minus(1);
        // when
        ProjectTime result = instance.getTimeLeft();
        // then
        assertEquals("Funded", result.value);
    }


    @Test
    public void getTimeLeftShouldENDEDIfTimeHasPassed() {
        // given
        Project instance = new Project();
        instance.launchedAt = DateTime.now().minusDays(2);
        instance.deadline = DateTime.now().minus(1);
        // when
        ProjectTime result = instance.getTimeLeft();
        // then
        assertEquals("ENDED", result.value);
    }

    @Test
    public void getTimeLeftShouldReturn2DaysLeftIf60HoursAreLeft() {
        // given
        Project instance = new Project();
        instance.launchedAt = DateTime.now();
        instance.deadline = DateTime.now().plusHours(60);
        // when
        ProjectTime result = instance.getTimeLeft();
        // then
        assertEquals("2", result.value);
        assertEquals("days left", result.description);
    }

    @Test
    public void getTimeLeftShouldReturn1DayLeftIf25HoursAreLeft() {
        // given
        Project instance = new Project();
        instance.launchedAt = DateTime.now();
        instance.deadline = DateTime.now().plusHours(25);
        // when
        ProjectTime result = instance.getTimeLeft();
        // then
        assertEquals("1", result.value);
        assertEquals("day left", result.description);
    }

    @Test
    public void getTimeLeftShouldReturn12Hours() {
        // given
        Project instance = new Project();
        instance.launchedAt = DateTime.now();
        instance.deadline = DateTime.now().plusHours(12);
        // when
        ProjectTime result = instance.getTimeLeft();
        // then
        assertEquals("12", result.value);
        assertEquals("hours left", result.description);
    }

    @Test
    public void getTimeLeftShould1Hour() {
        // given
        Project instance = new Project();
        instance.launchedAt = DateTime.now();
        instance.deadline = DateTime.now().plusMinutes(62);
        // when
        ProjectTime result = instance.getTimeLeft();
        // then
        assertEquals("1", result.value);
        assertEquals("hour left", result.description);
    }

    @Test
    public void getTimeLeftShould30Minutes() {
        // given
        Project instance = new Project();
        instance.launchedAt = DateTime.now();
        instance.deadline = DateTime.now().plusMinutes(30).plusSeconds(30);
        // when
        ProjectTime result = instance.getTimeLeft();
        // then
        assertEquals("30", result.value);
        assertEquals("minutes left", result.description);
    }

    @Test
    public void getTimeLeftShould1Minute() {
        // given
        Project instance = new Project();
        instance.launchedAt = DateTime.now();
        instance.deadline = DateTime.now().plusSeconds(90);
        // when
        ProjectTime result = instance.getTimeLeft();
        // then
        assertEquals("1", result.value);
        assertEquals("minute left", result.description);
    }

    @Test
    public void getTimeLeftShould30Seconds() {
        // given
        Project instance = new Project();
        instance.launchedAt = DateTime.now();
        instance.deadline = DateTime.now().plusSeconds(30).plusMillis(999);
        // when
        ProjectTime result = instance.getTimeLeft();
        // then
        assertEquals("30", result.value);
        assertEquals("seconds left", result.description);
    }

}