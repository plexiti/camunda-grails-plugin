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

import grails.util.Environment
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration
import org.camunda.bpm.engine.spring.ProcessEngineFactoryBean
import org.camunda.bpm.engine.test.mock.MockExpressionManager
import org.slf4j.bridge.SLF4JBridgeHandler

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
class CamundaPluginSupport {

    static doWithSpring = {
        if (!application.config.camunda.deployment.scenario
                || application.config.camunda.deployment.scenario == 'embedded') {
            camundaProcessEngineBean(ProcessEngineFactoryBean) {
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
                    // During test phases, use jul bridge in case no explicit user configuration exists
                    if (!SLF4JBridgeHandler.installed
                            && !application.flatConfig.containsKey('grails.logging.jul.usebridge')) {
                        SLF4JBridgeHandler.removeHandlersForRootLogger();
                        SLF4JBridgeHandler.install();
                    }
                    // During test phases (except functional), assume as default behaviour single 
                    // threaded testing - with MockExpressionManager and deactivated jobExecutor
                    if (System.properties['grails.test.phase'] != 'functional') {
                        jobExecutorActivate = false
                        expressionManager = bean(MockExpressionManager)
                    }
                }
                // Now set explicit user configuration and override our previously set defaults 
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

}
