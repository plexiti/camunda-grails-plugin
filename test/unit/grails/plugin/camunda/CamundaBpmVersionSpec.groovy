package grails.plugin.camunda

import spock.lang.Specification
import spock.lang.Unroll


/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
class CamundaBpmVersionSpec extends Specification {

    void "Test whether camunda BPM version is retrievable"() {

        when: "we retrieve the actual camunda BPM version"
        def version = CamundaBpmVersion.version

        then: "we expect to see a specific pattern"
        version == null || version =~ CamundaBpmVersion.versionPattern
        
    }
    
    @Unroll
    void "Test whether camunda BPM #actualVersion is at least #neededVersion"() {

        given: "we set a camunda BPM version as test value"
        CamundaBpmVersion.testVersion = actualVersion
        
        when: "we check for a specific needed version"
        def actualResult = CamundaBpmVersion.isAtLeast(neededVersion)
        
        then: "the needed version is greater than the actual version - or not"
        actualResult == expectedResult
        
        where:
        actualVersion  | neededVersion  | expectedResult
        "7.0.0-alpha1" | "7.0.0-alpha1" | true
        "7.0.0-alpha2" | "7.0.0-alpha1" | true
        "7.0.0-beta1"  | "7.0.0-alpha1" | true
        "7.0.0-beta2"  | "7.0.0-alpha1" | true
        "7.0.0-Final"  | "7.0.0-alpha1" | true
        "7.1.0-alpha1" | "7.0.0-alpha1" | true
        "7.1.0-alpha2" | "7.0.0-alpha1" | true
        "7.1.0-beta1"  | "7.0.0-alpha1" | true
        "7.1.0-beta2"  | "7.0.0-alpha1" | true
        "7.1.0-Final"  | "7.0.0-alpha1" | true
        "7.2.0-alpha1" | "7.0.0-alpha1" | true
        "7.2.0-alpha2" | "7.0.0-alpha1" | true
        "7.2.0-beta1"  | "7.0.0-alpha1" | true
        "7.2.0-beta2"  | "7.0.0-alpha1" | true
        "7.2.0-Final"  | "7.0.0-alpha1" | true

        "7.0.0-alpha1" | "7.0.0-alpha2" | false
        "7.0.0-alpha2" | "7.0.0-alpha2" | true
        "7.0.0-beta1"  | "7.0.0-alpha2" | true
        "7.0.0-beta2"  | "7.0.0-alpha2" | true
        "7.0.0-Final"  | "7.0.0-alpha2" | true
        "7.1.0-alpha1" | "7.0.0-alpha2" | true
        "7.1.0-alpha2" | "7.0.0-alpha2" | true
        "7.1.0-beta1"  | "7.0.0-alpha2" | true
        "7.1.0-beta2"  | "7.0.0-alpha2" | true
        "7.1.0-Final"  | "7.0.0-alpha2" | true
        "7.2.0-alpha1" | "7.0.0-alpha2" | true
        "7.2.0-alpha2" | "7.0.0-alpha2" | true
        "7.2.0-beta1"  | "7.0.0-alpha2" | true
        "7.2.0-beta2"  | "7.0.0-alpha2" | true
        "7.2.0-Final"  | "7.0.0-alpha2" | true

        "7.0.0-alpha1" | "7.0.0-beta1" | false
        "7.0.0-alpha2" | "7.0.0-beta1" | false
        "7.0.0-beta1"  | "7.0.0-beta1" | true
        "7.0.0-beta2"  | "7.0.0-beta1" | true
        "7.0.0-Final"  | "7.0.0-beta1" | true
        "7.1.0-alpha1" | "7.0.0-beta1" | true
        "7.1.0-alpha2" | "7.0.0-beta1" | true
        "7.1.0-beta1"  | "7.0.0-beta1" | true
        "7.1.0-beta2"  | "7.0.0-beta1" | true
        "7.1.0-Final"  | "7.0.0-beta1" | true
        "7.2.0-alpha1" | "7.0.0-beta1" | true
        "7.2.0-alpha2" | "7.0.0-beta1" | true
        "7.2.0-beta1"  | "7.0.0-beta1" | true
        "7.2.0-beta2"  | "7.0.0-beta1" | true
        "7.2.0-Final"  | "7.0.0-beta1" | true

        "7.0.0-alpha1" | "7.0.0-beta2" | false
        "7.0.0-alpha2" | "7.0.0-beta2" | false
        "7.0.0-beta1"  | "7.0.0-beta2" | false
        "7.0.0-beta2"  | "7.0.0-beta2" | true
        "7.0.0-Final"  | "7.0.0-beta2" | true
        "7.1.0-alpha1" | "7.0.0-beta2" | true
        "7.1.0-alpha2" | "7.0.0-beta2" | true
        "7.1.0-beta1"  | "7.0.0-beta2" | true
        "7.1.0-beta2"  | "7.0.0-beta2" | true
        "7.1.0-Final"  | "7.0.0-beta2" | true
        "7.2.0-alpha1" | "7.0.0-beta2" | true
        "7.2.0-alpha2" | "7.0.0-beta2" | true
        "7.2.0-beta1"  | "7.0.0-beta2" | true
        "7.2.0-beta2"  | "7.0.0-beta2" | true
        "7.2.0-Final"  | "7.0.0-beta2" | true

        "7.0.0-alpha1" | "7.0.0-Final" | false
        "7.0.0-alpha2" | "7.0.0-Final" | false
        "7.0.0-beta1"  | "7.0.0-Final" | false
        "7.0.0-beta2"  | "7.0.0-Final" | false
        "7.0.0-Final"  | "7.0.0-Final" | true
        "7.1.0-alpha1" | "7.0.0-Final" | true
        "7.1.0-alpha2" | "7.0.0-Final" | true
        "7.1.0-beta1"  | "7.0.0-Final" | true
        "7.1.0-beta2"  | "7.0.0-Final" | true
        "7.1.0-Final"  | "7.0.0-Final" | true
        "7.2.0-alpha1" | "7.0.0-Final" | true
        "7.2.0-alpha2" | "7.0.0-Final" | true
        "7.2.0-beta1"  | "7.0.0-Final" | true
        "7.2.0-beta2"  | "7.0.0-Final" | true
        "7.2.0-Final"  | "7.0.0-Final" | true

        "7.0.0-alpha1" | "7.1.0-alpha1" | false
        "7.0.0-alpha2" | "7.1.0-alpha1" | false
        "7.0.0-beta1"  | "7.1.0-alpha1" | false
        "7.0.0-beta2"  | "7.1.0-alpha1" | false
        "7.0.0-Final"  | "7.1.0-alpha1" | false
        "7.1.0-alpha1" | "7.1.0-alpha1" | true
        "7.1.0-alpha2" | "7.1.0-alpha1" | true
        "7.1.0-beta1"  | "7.1.0-alpha1" | true
        "7.1.0-beta2"  | "7.1.0-alpha1" | true
        "7.1.0-Final"  | "7.1.0-alpha1" | true
        "7.2.0-alpha1" | "7.1.0-alpha1" | true
        "7.2.0-alpha2" | "7.1.0-alpha1" | true
        "7.2.0-beta1"  | "7.1.0-alpha1" | true
        "7.2.0-beta2"  | "7.1.0-alpha1" | true
        "7.2.0-Final"  | "7.1.0-alpha1" | true

        "7.0.0-alpha1" | "7.1.0-alpha2" | false
        "7.0.0-alpha2" | "7.1.0-alpha2" | false
        "7.0.0-beta1"  | "7.1.0-alpha2" | false
        "7.0.0-beta2"  | "7.1.0-alpha2" | false
        "7.0.0-Final"  | "7.1.0-alpha2" | false
        "7.1.0-alpha1" | "7.1.0-alpha2" | false
        "7.1.0-alpha2" | "7.1.0-alpha2" | true
        "7.1.0-beta1"  | "7.1.0-alpha2" | true
        "7.1.0-beta2"  | "7.1.0-alpha2" | true
        "7.1.0-Final"  | "7.1.0-alpha2" | true
        "7.2.0-alpha1" | "7.1.0-alpha2" | true
        "7.2.0-alpha2" | "7.1.0-alpha2" | true
        "7.2.0-beta1"  | "7.1.0-alpha2" | true
        "7.2.0-beta2"  | "7.1.0-alpha2" | true
        "7.2.0-Final"  | "7.1.0-alpha2" | true

        "7.0.0-alpha1" | "7.1.0-beta1" | false
        "7.0.0-alpha2" | "7.1.0-beta1" | false
        "7.0.0-beta1"  | "7.1.0-beta1" | false
        "7.0.0-beta2"  | "7.1.0-beta1" | false
        "7.0.0-Final"  | "7.1.0-beta1" | false
        "7.1.0-alpha1" | "7.1.0-beta1" | false
        "7.1.0-alpha2" | "7.1.0-beta1" | false
        "7.1.0-beta1"  | "7.1.0-beta1" | true
        "7.1.0-beta2"  | "7.1.0-beta1" | true
        "7.1.0-Final"  | "7.1.0-beta1" | true
        "7.2.0-alpha1" | "7.1.0-beta1" | true
        "7.2.0-alpha2" | "7.1.0-beta1" | true
        "7.2.0-beta1"  | "7.1.0-beta1" | true
        "7.2.0-beta2"  | "7.1.0-beta1" | true
        "7.2.0-Final"  | "7.1.0-beta1" | true

        "7.0.0-alpha1" | "7.1.0-beta2" | false
        "7.0.0-alpha2" | "7.1.0-beta2" | false
        "7.0.0-beta1"  | "7.1.0-beta2" | false
        "7.0.0-beta2"  | "7.1.0-beta2" | false
        "7.0.0-Final"  | "7.1.0-beta2" | false
        "7.1.0-alpha1" | "7.1.0-beta2" | false
        "7.1.0-alpha2" | "7.1.0-beta2" | false
        "7.1.0-beta1"  | "7.1.0-beta2" | false
        "7.1.0-beta2"  | "7.1.0-beta2" | true
        "7.1.0-Final"  | "7.1.0-beta2" | true
        "7.2.0-alpha1" | "7.1.0-beta2" | true
        "7.2.0-alpha2" | "7.1.0-beta2" | true
        "7.2.0-beta1"  | "7.1.0-beta2" | true
        "7.2.0-beta2"  | "7.1.0-beta2" | true
        "7.2.0-Final"  | "7.1.0-beta2" | true

        "7.0.0-alpha1" | "7.1.0-Final" | false
        "7.0.0-alpha2" | "7.1.0-Final" | false
        "7.0.0-beta1"  | "7.1.0-Final" | false
        "7.0.0-beta2"  | "7.1.0-Final" | false
        "7.0.0-Final"  | "7.1.0-Final" | false
        "7.1.0-alpha1" | "7.1.0-Final" | false
        "7.1.0-alpha2" | "7.1.0-Final" | false
        "7.1.0-beta1"  | "7.1.0-Final" | false
        "7.1.0-beta2"  | "7.1.0-Final" | false
        "7.1.0-Final"  | "7.1.0-Final" | true
        "7.2.0-alpha1" | "7.1.0-Final" | true
        "7.2.0-alpha2" | "7.1.0-Final" | true
        "7.2.0-beta1"  | "7.1.0-Final" | true
        "7.2.0-beta2"  | "7.1.0-Final" | true
        "7.2.0-Final"  | "7.1.0-Final" | true

        "7.0.0-alpha1" | "7.2.0-alpha1" | false
        "7.0.0-alpha2" | "7.2.0-alpha1" | false
        "7.0.0-beta1"  | "7.2.0-alpha1" | false
        "7.0.0-beta2"  | "7.2.0-alpha1" | false
        "7.0.0-Final"  | "7.2.0-alpha1" | false
        "7.1.0-alpha1" | "7.2.0-alpha1" | false
        "7.1.0-alpha2" | "7.2.0-alpha1" | false
        "7.1.0-beta1"  | "7.2.0-alpha1" | false
        "7.1.0-beta2"  | "7.2.0-alpha1" | false
        "7.1.0-Final"  | "7.2.0-alpha1" | false
        "7.2.0-alpha1" | "7.2.0-alpha1" | true
        "7.2.0-alpha2" | "7.2.0-alpha1" | true
        "7.2.0-beta1"  | "7.2.0-alpha1" | true
        "7.2.0-beta2"  | "7.2.0-alpha1" | true
        "7.2.0-Final"  | "7.2.0-alpha1" | true

        "7.0.0-alpha1" | "7.2.0-alpha2" | false
        "7.0.0-alpha2" | "7.2.0-alpha2" | false
        "7.0.0-beta1"  | "7.2.0-alpha2" | false
        "7.0.0-beta2"  | "7.2.0-alpha2" | false
        "7.0.0-Final"  | "7.2.0-alpha2" | false
        "7.1.0-alpha1" | "7.2.0-alpha2" | false
        "7.1.0-alpha2" | "7.2.0-alpha2" | false
        "7.1.0-beta1"  | "7.2.0-alpha2" | false
        "7.1.0-beta2"  | "7.2.0-alpha2" | false
        "7.1.0-Final"  | "7.2.0-alpha2" | false
        "7.2.0-alpha1" | "7.2.0-alpha2" | false
        "7.2.0-alpha2" | "7.2.0-alpha2" | true
        "7.2.0-beta1"  | "7.2.0-alpha2" | true
        "7.2.0-beta2"  | "7.2.0-alpha2" | true
        "7.2.0-Final"  | "7.2.0-alpha2" | true

        "7.0.0-alpha1" | "7.2.0-beta1" | false
        "7.0.0-alpha2" | "7.2.0-beta1" | false
        "7.0.0-beta1"  | "7.2.0-beta1" | false
        "7.0.0-beta2"  | "7.2.0-beta1" | false
        "7.0.0-Final"  | "7.2.0-beta1" | false
        "7.1.0-alpha1" | "7.2.0-beta1" | false
        "7.1.0-alpha2" | "7.2.0-beta1" | false
        "7.1.0-beta1"  | "7.2.0-beta1" | false
        "7.1.0-beta2"  | "7.2.0-beta1" | false
        "7.1.0-Final"  | "7.2.0-beta1" | false
        "7.2.0-alpha1" | "7.2.0-beta1" | false
        "7.2.0-alpha2" | "7.2.0-beta1" | false
        "7.2.0-beta1"  | "7.2.0-beta1" | true
        "7.2.0-beta2"  | "7.2.0-beta1" | true
        "7.2.0-Final"  | "7.2.0-beta1" | true

        "7.0.0-alpha1" | "7.2.0-beta2" | false
        "7.0.0-alpha2" | "7.2.0-beta2" | false
        "7.0.0-beta1"  | "7.2.0-beta2" | false
        "7.0.0-beta2"  | "7.2.0-beta2" | false
        "7.0.0-Final"  | "7.2.0-beta2" | false
        "7.1.0-alpha1" | "7.2.0-beta2" | false
        "7.1.0-alpha2" | "7.2.0-beta2" | false
        "7.1.0-beta1"  | "7.2.0-beta2" | false
        "7.1.0-beta2"  | "7.2.0-beta2" | false
        "7.1.0-Final"  | "7.2.0-beta2" | false
        "7.2.0-alpha1" | "7.2.0-beta2" | false
        "7.2.0-alpha2" | "7.2.0-beta2" | false
        "7.2.0-beta1"  | "7.2.0-beta2" | false
        "7.2.0-beta2"  | "7.2.0-beta2" | true
        "7.2.0-Final"  | "7.2.0-beta2" | true

        "7.0.0-alpha1" | "7.2.0-Final" | false
        "7.0.0-alpha2" | "7.2.0-Final" | false
        "7.0.0-beta1"  | "7.2.0-Final" | false
        "7.0.0-beta2"  | "7.2.0-Final" | false
        "7.0.0-Final"  | "7.2.0-Final" | false
        "7.1.0-alpha1" | "7.2.0-Final" | false
        "7.1.0-alpha2" | "7.2.0-Final" | false
        "7.1.0-beta1"  | "7.2.0-Final" | false
        "7.1.0-beta2"  | "7.2.0-Final" | false
        "7.1.0-Final"  | "7.2.0-Final" | false
        "7.2.0-alpha1" | "7.2.0-Final" | false
        "7.2.0-alpha2" | "7.2.0-Final" | false
        "7.2.0-beta1"  | "7.2.0-Final" | false
        "7.2.0-beta2"  | "7.2.0-Final" | false
        "7.2.0-Final"  | "7.2.0-Final" | true

        "7.2.0-SNAPSHOT" | "7.0.0-alpha1" | true
        "7.2.0-SNAPSHOT" | "7.0.0-alpha2" | true
        "7.2.0-SNAPSHOT" | "7.0.0-beta1"  | true
        "7.2.0-SNAPSHOT" | "7.0.0-beta2"  | true
        "7.2.0-SNAPSHOT" | "7.0.0-Final"  | true
        "7.2.0-SNAPSHOT" | "7.1.0-alpha1" | true
        "7.2.0-SNAPSHOT" | "7.1.0-alpha2" | true
        "7.2.0-SNAPSHOT" | "7.1.0-beta1"  | true
        "7.2.0-SNAPSHOT" | "7.1.0-beta2"  | true
        "7.2.0-SNAPSHOT" | "7.1.0-Final"  | true
        "7.2.0-SNAPSHOT" | "7.2.0-alpha1" | false
        "7.2.0-SNAPSHOT" | "7.2.0-alpha2" | false
        "7.2.0-SNAPSHOT" | "7.2.0-beta1"  | false
        "7.2.0-SNAPSHOT" | "7.2.0-beta2"  | false
        "7.2.0-SNAPSHOT" | "7.2.0-Final"  | false

        "7.2.0-SNAPSHOT" | "7.2.0-SNAPSHOT"  | false

    }

}