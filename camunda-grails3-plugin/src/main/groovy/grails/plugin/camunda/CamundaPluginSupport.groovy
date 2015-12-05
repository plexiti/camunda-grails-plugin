/*
 * Copyright 2014 Martin Schimak - plexiti GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grails.plugin.camunda

import grails.util.BuildScope
import grails.util.Environment
import grails.util.Metadata
import groovy.util.logging.Log
import org.camunda.bpm.BpmPlatform
import org.camunda.bpm.ProcessApplicationService
import org.camunda.bpm.ProcessEngineService
import org.camunda.bpm.engine.ProcessEngineException
import org.camunda.bpm.engine.RepositoryService
import org.camunda.bpm.engine.impl.ProcessEngineImpl
import org.camunda.bpm.engine.repository.DeploymentBuilder
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration
import org.camunda.bpm.engine.spring.application.SpringProcessApplication
import org.camunda.bpm.engine.spring.ProcessEngineFactoryBean
import org.camunda.bpm.engine.spring.container.ManagedProcessEngineFactoryBean
import org.camunda.bpm.engine.test.mock.MockExpressionManager
import org.slf4j.bridge.SLF4JBridgeHandler
import org.springframework.beans.BeanUtils

import java.lang.reflect.Method

import static grails.plugin.camunda.Configuration.*

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
@Log
class CamundaPluginSupport {
    
    // Make sure (for database migration plugin) that just one process application 
    // is deployed and just first process engine is registered with JMX
    static boolean firstContext = true

    static doWithSpring = {
        if (firstContext) { // Make sure (for database migration plugin) that just one process application is deployed
            def processApplicationName = "${Metadata.getCurrent().getApplicationName()}"
            log.info(processApplicationName)
            if (SpringProcessApplication.isAssignableFrom(config('camunda.deployment.application') as Class)) {
                "${processApplicationName}"(config('camunda.deployment.application'))
            }
            if (springConfig.beanNames.find { it == processApplicationName }) {
                springConfig.addAlias 'camundaProcessApplicationBean', processApplicationName
                springConfig.addAlias Identifiers.beanName('camundaProcessApplicationBean'), processApplicationName
            }
        }
        if (config('camunda.deployment.scenario') == 'embedded') {
            // Make sure (for database migration plugin) that just first process engine is registered with JMX
            camundaProcessEngineBean(firstContext ?  ManagedProcessEngineFactoryBean : ProcessEngineFactoryBean) {
                processEngineConfiguration = ref('camundaProcessEngineConfigurationBean')
            }
            camundaProcessEngineConfigurationBean(SpringProcessEngineConfiguration) { beanDefinition ->
                dataSource = ref('dataSource')
                transactionManager = ref('transactionManager')
                if (Environment.current == Environment.TEST) {
                    // During test phases, use jul bridge in case no explicit user configuration exists
                    if (!SLF4JBridgeHandler.installed && !config('grails.logging.jul.usebridge')) {
                        SLF4JBridgeHandler.removeHandlersForRootLogger();
                        SLF4JBridgeHandler.install();
                    }
                    // During test phases (except functional), use MockExpressionManager
                    if (BuildScope.current in [BuildScope.ALL, BuildScope.TEST]) {
                        expressionManager = bean(MockExpressionManager)
                    }
                }
                // Now set configuration 
                (config('camunda.engine.configuration') as Map<String, Object>).each {
                    def prop = it.key.substring('camunda.engine.configuration'.length() + 1)
                    beanDefinition.setPropertyValue(prop, 
                        it.value instanceof String 
                                && !(String.class.isAssignableFrom(BeanUtils.findPropertyType(prop, SpringProcessEngineConfiguration))) 
                            ? ref(it.value) : it.value
                    )
                }
            }
        } else if (config('camunda.deployment.scenario') == 'shared') {
            camundaProcessEngineServiceBean(BpmPlatform) { beanDefinition ->
                beanDefinition.factoryMethod = 'getProcessEngineService'
            }
            camundaProcessEngineBean(camundaProcessEngineServiceBean: 'getDefaultProcessEngine')
        }
        if (springConfig.beanNames.find { it == 'camundaProcessEngineBean' }) {
            // Retrieve Camunda service API beans
            ProcessEngineImpl.class.declaredMethods.each { Method method ->
                if(method.name.startsWith('get') && method.name.endsWith('Service')) {
                    "camunda${method.name.substring(3)}Bean"(camundaProcessEngineBean: method.name)
                }
            }
            // Finally, register all Camunda beans under their default or user configured aliases
            springConfig.beanNames.findAll { it.startsWith("camunda") && it.endsWith("Bean") }.each {
                springConfig.addAlias Identifiers.beanName(it), it
            }
        }
        firstContext = false
    }
    
    static doWithWebDescriptor = { webXml ->
        // for tomcat, declare resource links in web xml
        if (config('camunda.deployment.scenario') == 'shared'
            && (config('camunda.deployment.shared.container') == 'tomcat')) {
            def element = webXml.'context-param'
            element[element.size() - 1] + {
                'resource-ref' {
                    'description'('Process Engine Service')
                    'res-ref-name'('ProcessEngineService')
                    'res-type'(ProcessEngineService.name)
                    'res-auth'('Container')
                }
            }
            element = webXml.'resource-ref'
            element[element.size() - 1] + {
                'resource-ref' {
                    'description'('Process Application Service')
                    'res-ref-name'('ProcessApplicationService')
                    'res-type'(ProcessApplicationService.name)
                    'res-auth'('Container')
                }
            }
        }
    }
    
    static onchange = { event ->
        if (event.source && event.source.file) { // ./grails-app/processes/**/*.bpmn resource changed
            if (config('camunda.deployment.scenario') != 'none' 
              && config('camunda.deployment.autoreload')) {
                RepositoryService repositoryService = event.ctx.getBean("camundaRepositoryServiceBean")
                DeploymentBuilder deploymentBuilder = repositoryService
                    .createDeployment()
                    .enableDuplicateFiltering()
                    .name("GrailsHotDeployment")
                File resource = event.source.file
                String resourceName
                try {
                    resourceName = resource.absolutePath
                } catch (IOException e) {
                    resourceName = resource.name
                }
                try {
                    deploymentBuilder.addInputStream(resourceName, resource.newInputStream())
                } catch (IOException e) {
                    throw new ProcessEngineException("couldn't auto deploy resource $resource: $e.message", e)
                }
                deploymentBuilder.deploy()
            }
        }
    }

}
