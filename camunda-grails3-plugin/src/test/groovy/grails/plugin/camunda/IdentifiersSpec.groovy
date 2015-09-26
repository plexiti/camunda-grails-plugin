package grails.plugin.camunda

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.util.Holders
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
@TestMixin(GrailsUnitTestMixin)
class IdentifiersSpec extends Specification {
    
    @Unroll
    void "Test camunda bean names"() {
        
        given:
        Holders.config['camunda.beans.taskService'] = "myTaskService"
       
        when:
        def name = Identifiers.beanName(beanName)
        
        then:
        name == alias
        
        where:
        beanName | alias
        "camundaRuntimeServiceBean" | "runtimeService"
        "camundaTaskServiceBean" | "myTaskService"
        "someOtherBean" | "someOtherBean"
        "camundaStrangeStuff" | "camundaStrangeStuff"
        
    }
    
}
