package grails.plugin.camunda.test

import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.TaskService
import org.camunda.bpm.engine.runtime.Execution
import org.camunda.bpm.engine.test.mock.Mocks
import spock.lang.Specification

/**
 * Integration Test for Camunda TestProcess 
 */
class TestProcessApplicationSpec extends Specification {

    RuntimeService runtimeService
    TaskService taskService

    def sampleTestProcessService = Mock(grails.plugin.camunda.test.SampleService)

    def setup() {
        Mocks.register("sampleService", sampleTestProcessService)
    }

    def cleanup() {
        Mocks.reset()
    }

    void "Testing a happy walk through TestProcess"() {

        when: "we create a new instance of TestProcess"
        runtimeService.startProcessInstanceByKey("TestProcess")

        then: "the task listener was notified for creation and assignment"
        def task = taskService.createTaskQuery().singleResult()
        TestProcessApplication.getCounter(task.id) == 2

        when: "completing the user task"
        taskService.complete(task.id)

        then: "the test process application was notified for completion, too"
        TestProcessApplication.getCounter(task.id) == 3

        and: "the process instance finished"
        !runtimeService.createProcessInstanceQuery().singleResult()

    }

}
