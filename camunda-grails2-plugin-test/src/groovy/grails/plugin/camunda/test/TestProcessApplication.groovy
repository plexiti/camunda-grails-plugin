package grails.plugin.camunda.test

import groovy.util.logging.Log
import org.camunda.bpm.engine.delegate.DelegateTask
import org.camunda.bpm.engine.delegate.TaskListener
import org.camunda.bpm.engine.spring.application.SpringServletProcessApplication

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
@Log
class TestProcessApplication extends SpringServletProcessApplication {
    
    private static notifications = new HashMap<String, Integer>()
    
    @Override
    public TaskListener getTaskListener() {
        return new TaskListener() {
            @Override
            public void notify(DelegateTask delegateTask) {
                def counter = incrementCounter(delegateTask.id)
                log.info("Notification #$counter for task '${delegateTask.taskDefinitionKey}' received")
            }
        }
    }
    
    protected static int getCounter(String taskId) {
        notifications.get(taskId) ?: 0
    }

    protected static int incrementCounter(String taskId) {
        notifications.put(taskId, ++getCounter(taskId))
        getCounter(taskId)
    }

}
