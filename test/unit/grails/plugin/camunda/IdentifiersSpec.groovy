package grails.plugin.camunda

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
@TestMixin(GrailsUnitTestMixin)
class IdentifiersSpec extends Specification {

    @Unroll
    void "Test type and extension handling"() {
        
        when:
        def (actualPkg, actualName) = Identifiers.generate(givenName)
        
        then:
        actualPkg == "com.plexiti"
        actualName == "Sample"
        
        where:
        givenName << [
            "com.plexiti.Sample",
            "com.plexiti.Sample.bpmn",
            "com.plexiti.SampleProcess",
            "com.plexiti.SampleProcess.bpmn",
        ]

    }

    @Unroll
    void "Test allowed names"() {

        when:
        def (actualPkg, actualName) = Identifiers.generate(givenName)

        then:
        givenName.startsWith(actualPkg)
        givenName.contains(actualName)

        where:
        givenName << [
            "com.plexiti1.Sample",
            "com.plex1t1.Sample.bpmn",
            "com.plexiti.Sample1Process",
            "_com_.plexiti.SampleProcess.bpmn",
        ]

    }

    @Unroll
    void "Test disallowed names"() {

        when:
        Identifiers.generate(givenName)

        then:
        thrown(AssertionError.class)

        where:
        givenName << [
            "1com.plexiti.Sample",
            "com.1plexiti.Sample.bpmn",
            "com.plexiti.1SampleProcess",
            "com.plexiti.Sample#Process.bpmn",
            ".plexiti.SampleProcess",
            "com.plexiti..SampleProcess",
            "com.plexiti.SampleProcess.",
        ]

    }

}
