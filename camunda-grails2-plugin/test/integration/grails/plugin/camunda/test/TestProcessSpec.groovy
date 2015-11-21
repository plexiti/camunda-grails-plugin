package grails.plugin.camunda.test

import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.TaskService
import org.camunda.bpm.engine.runtime.Execution
import org.camunda.bpm.engine.test.mock.Mocks
import spock.lang.Specification

/**
 * Integration Test for Camunda TestProcess 
 */
class TestProcessSpec extends Specification {

    /**
     * 1) Inject Camunda process engine API service beans
     */
    RuntimeService runtimeService
    TaskService taskService

    /**
     * 2) Mock your Grail(s) services called from TestProcess
     */
    def sampleTestProcessService = Mock(SampleService)

    /**
     * 3) Register your service mocks to make them accessible via TestProcess
     */
    def setup() {
        Mocks.register("sampleService", sampleTestProcessService)
    }

    def cleanup() {
        Mocks.reset()
    }

    /**
     * 4) Test the various aspects and behaviour of TestProcess
     */
    void "Testing a happy walk through TestProcess"() {

        given: "a new instance of TestProcess"
        runtimeService.startProcessInstanceByKey("TestProcess")

        when: "completing the user task"
        def task = taskService.createTaskQuery().singleResult()
        taskService.complete(task.id)

        then: "the service method defined for the subsequent service task was called exactly once"
        1 * sampleTestProcessService.serviceMethod(_ as Execution)

        and: "nothing else was called"
        0 * _

        and: "the process instance finished"
        !runtimeService.createProcessInstanceQuery().singleResult()

    }

}
