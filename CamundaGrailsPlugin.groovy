import grails.plugin.camunda.Identifiers
import grails.util.Environment
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration
import org.camunda.bpm.engine.spring.application.SpringServletProcessApplication
import org.camunda.bpm.engine.spring.container.ManagedProcessEngineFactoryBean
import org.camunda.bpm.engine.test.mock.MockExpressionManager
import org.slf4j.bridge.SLF4JBridgeHandler

class CamundaGrailsPlugin {
    def version = "0.1.0-SNAPSHOT"
    def grailsVersion = "2.3 > *"
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]
    def title = "camunda BPM Plugin" // Headline display name of the plugin
    def author = "Martin Schimak"
    def authorEmail = "martin.schimak@plexiti.com"
    def description = '''\
This plugin integrates the camunda BPM platform with Grails. camunda BPM is a flexible framework for 
workflow and process automation. It's core is a native BPMN 2.0 process engine that runs inside the Java 
Virtual Machine. It is a perfect match for the Spring Framework - and therefore for Grails, too. On top 
of the process engine, you can choose from a stack of tools for human workflow management, operations & 
monitoring.
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

        if (!application.config.camunda.deployment.scenario 
            || application.config.camunda.deployment.scenario == 'embedded') {
            // Instantiate basic camunda beans for embedded scenario
            camundaProcessApplicationBean(SpringServletProcessApplication)
            camundaProcessEngineBean(ManagedProcessEngineFactoryBean) {
                processEngineConfiguration = ref('camundaProcessEngineConfigurationBean')
            }
            camundaProcessEngineConfigurationBean(SpringProcessEngineConfiguration) { beanDefinition ->
                dataSource = ref('dataSource')
                transactionManager = ref('transactionManager')
                if (Environment.current in [ Environment.DEVELOPMENT, Environment.TEST ]) {
                    databaseSchemaUpdate = true
                    deploymentResources = ['classpath:/**/*.bpmn']
                }
                if (System.properties.containsKey('grails.test.phase')) {
                    if (!SLF4JBridgeHandler.installed 
                            && !application.flatConfig.containsKey('grails.logging.jul.usebridge')) {
                        SLF4JBridgeHandler.removeHandlersForRootLogger();
                        SLF4JBridgeHandler.install();
                    }
                    if (System.properties['grails.test.phase'] != 'functional') {
                        jobExecutorActivate = false
                        expressionManager = bean(MockExpressionManager)
                    }
                }
                application.config.camunda.engine.configuration.each {
                    beanDefinition.setPropertyValue(it.key, it.value)
                }
            }
            // Instantiate camunda service API beans
            camundaRuntimeServiceBean(camundaProcessEngineBean: 'getRuntimeService')
            camundaRepositoryServiceBean(camundaProcessEngineBean: 'getRepositoryService')
            camundaTaskServiceBean(camundaProcessEngineBean: 'getTaskService')
            camundaManagementServiceBean(camundaProcessEngineBean: 'getManagementService')
            camundaIdentityServiceBean(camundaProcessEngineBean: 'getIdentityService')
            camundaAuthorizationServiceBean(camundaProcessEngineBean: 'getAuthorizationService')
            camundaHistoryServiceBean(camundaProcessEngineBean: 'getHistoryService')
            camundaFormServiceBean(camundaProcessEngineBean: 'getFormService')
            // Finally, register all camunda beans under their default or user configured aliases
            springConfig.beanNames.findAll { it.startsWith("camunda") && it.endsWith("Bean") }.each {
                springConfig.addAlias Identifiers.beanName(it), it
            }
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
