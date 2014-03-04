import grails.util.Environment
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration
import org.camunda.bpm.engine.spring.application.SpringServletProcessApplication
import org.camunda.bpm.engine.spring.container.ManagedProcessEngineFactoryBean
import org.camunda.bpm.engine.test.mock.MockExpressionManager

class CamundaGrailsPlugin {
    def version = "0.1-SNAPSHOT"
    def grailsVersion = "2.3 > *"
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]
    def title = "camunda BPM Plugin" // Headline display name of the plugin
    def author = "Martin Schimak"
    def authorEmail = "martin.schimak@plexiti.com"
    def description = '''\
Integrates the camunda BPM platform with Grails. camunda BPM is a flexible framework for workflow 
and process automation. It's core is a native BPMN 2.0 process engine that runs inside the Java 
Virtual Machine. It is a perfect match for the Spring Framework - and therefore for Grails, too. 
On top of the process engine, you can choose from a stack of tools for human workflow management, 
operations & monitoring.
'''
    def documentation = "http://grails.org/plugin/camunda"
    def license = "APACHE"
    def organization = [ name: "plexiti GmbH", url: "http://plexiti.com" ]
    def developers = [ ]
    def issueManagement = [ system: "github", url: "https://github.com/plexiti/camunda-grails-plugin/issues" ]
    def scm = [ url: "https://github.com/plexiti/camunda-grails-plugin" ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {

        if (!application.config.grails.plugin.camunda.deployment.scenario 
            || application.config.grails.plugin.camunda.deployment.scenario == 'embedded') {
            processApplication(SpringServletProcessApplication)
            processEngine(ManagedProcessEngineFactoryBean) {
                processEngineConfiguration = ref("processEngineConfiguration")
            }
            processEngineConfiguration(SpringProcessEngineConfiguration) { beanDefinition ->
                dataSource = ref("dataSource")
                transactionManager = ref("transactionManager")
                if (Environment.current in [ Environment.DEVELOPMENT, Environment.TEST ]) {
                    databaseSchemaUpdate = true
                    deploymentResources = [
                        "classpath:/**/*.bpmn",
                        "classpath:/**/*.png"
                    ]
                }
                if (System.properties['grails.test.phase'] != 'functional') {
                    jobExecutorActivate = false
                    expressionManager = bean(MockExpressionManager)
                }
                application.config.grails.plugin.camunda.engine.configuration.each {
                    beanDefinition.setPropertyValue(it.key, it.value)
                }
            }
            runtimeService(processEngine: "getRuntimeService")
            repositoryService(processEngine: "getRepositoryService")
            taskService(processEngine: "getTaskService")
            managementService(processEngine: "getManagementService")
            identityService(processEngine: "getIdentityService")
            authorizationService(processEngine: "getAuthorizationService")
            historyService(processEngine: "getHistoryService")
            formService(processEngine: "getFormService")
        }

    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { ctx ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
    
}
