package com.byoutline.kickmaterial.model

import org.joda.time.DateTime
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>.
 */
class ProjectGetTimeSpec extends Specification {

    @Shared
    DateTime now = DateTime.now();

    @Unroll
    def "getTime should return value: #val and desc: #desc for launchedAt: #start and deadline: #deadline"() {
        given:
        Project instance = new Project();
        instance.launchedAt = start;
        instance.deadline = deadline;
        when:
        ProjectTime result = instance.getTimeLeft(now);

        then:
        val == result.value
        desc == result.description

        where:
        // Use start in table so it can be used in test name.
        start | deadline                            | val  | desc
        now   | now.plusHours(60)                   | '2'  | 'days left'
        now   | now.plusHours(25)                   | '1'  | 'day left'
        now   | now.plusHours(12)                   | '12' | 'hours left'
        now   | now.plusMinutes(62)                 | '1'  | 'hour left'
        now   | now.plusMinutes(30).plusSeconds(30) | '30' | 'minutes left'
        now   | now.plusSeconds(90)                 | '1'  | 'minute left'
        now   | now.plusSeconds(30).plusMillis(999) | '30' | 'seconds left'
    }
}