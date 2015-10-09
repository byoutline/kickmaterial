package com.byoutline.kickmaterial.model

import spock.lang.Unroll

/**
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>.
 */
class ProjectSpec extends spock.lang.Specification {


    @Unroll
    def "getProgress should return value: #val for gathered amount: #gA and total amount: #tA"() {
        given:
        Project instance = new Project()
        instance.gatheredAmount = gA
        instance.totalAmount = tA
        when:
        float result = instance.getPercentProgress()

        then:
        result == val

        where:
        gA | tA | val
        0  | 3  | 0
        2  | 2  | 100
        1  | 2  | 50
        12 | 1  | 100
    }

    def "getSignature should returned signature only"() {
        given:
        Project instance = new Project()
        instance.urls = new ProjectUrls()
        instance.urls.api = new ProjectUrlsApi()
        instance.urls.api.project = "https://api.byoutline.com/v1/projects/866180756?signature=1427292197.67ec163ed8dbd36529f591b18fe0f7c4c5867ee1"
        when:
        Map<String, String> result = instance.getDetailsQueryMap()
        then:
        result == [signature: '1427292197.67ec163ed8dbd36529f591b18fe0f7c4c5867ee1']
    }

}