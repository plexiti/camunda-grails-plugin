@artifact.package@import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.TaskService
import grails.plugin.camunda.test.SampleService
import org.camunda.bpm.engine.runtime.Execution
import org.camunda.bpm.engine.test.mock.Mocks
import spock.lang.Specification

/**
 * Integration Test for camunda @artifact.testclass@ 
 */
class @artifact.name@ extends Specification {

    /**
     * 1) Inject camunda process engine API service beans
     */
    RuntimeService runtimeService
    TaskService taskService

    /**
     * 2) Mock your Grail(s) services called from @artifact.testclass@
     */
    def sample@artifact.testclass@Service = Mock(SampleService)

    /**
     * 3) Register your service mocks to make them accessible via @artifact.testclass@
     */
    def setup() {
        Mocks.register("sampleService", sample@artifact.testclass@Service)
    }

    def cleanup() {
        Mocks.reset()
    }

    /**
     * 4) Test the various aspects and behaviour of @artifact.testclass@
     */
    void "Testing a happy walk through @artifact.testclass@"() {

        given: "a new instance of @artifact.testclass@"
        runtimeService.startProcessInstanceByKey("@artifact.testclass@")

        when: "completing the user task"
        def task = taskService.createTaskQuery().singleResult()
        taskService.complete(task.id)

        then: "the service method defined for the subsequent service task was called exactly once"
        1 * sample@artifact.testclass@Service.serviceMethod(_ as Execution)

        and: "nothing else was called"
        0 * _

        and: "the process instance finished"
        !runtimeService.createProcessInstanceQuery().singleResult()

    }

}