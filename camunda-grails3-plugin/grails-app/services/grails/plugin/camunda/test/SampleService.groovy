package grails.plugin.camunda.test

import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.runtime.Execution

import java.util.logging.Logger

class SampleService {
    
    // Use java.util.logging in order to see something in vanilla tomcat catalina.out
    static Logger logger = Logger.getLogger(SampleService.name)
    
    RuntimeService runtimeService

    def serviceMethod(Execution execution) {
        println("${SampleService.class.simpleName} called from Camunda BPM ProcessInstance (id='${execution.processInstanceId}').")
        runtimeService.getVariables(execution.processInstanceId).each {
            logger.info("- Process Variable '${it.key}' = ${it.value} (${it.value?.class})")
        }
    }
    
}
