package grails.plugin.camunda.test

import grails.test.mixin.integration.Integration
import spock.lang.Specification

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
@Integration
class DependencyInjectionSpec extends Specification {

  def processEngine
  def processEngineConfiguration
  def processEngineService
  def processApplication

  def runtimeService
  def repositoryService
  def taskService
  def managementService
  def identityService
  def authorizationService
  def formService

  def historyService
  def camundaHistoryServiceBean
  def alternativeHistoryServiceName

  void "Test successful dependency injection of standard beans"() {
    expect:
      processEngine
      processEngineConfiguration
      !processEngineService
      !processApplication
      repositoryService
      taskService
      managementService
      identityService
      authorizationService
      runtimeService
      formService
  }

  void "Test alternative bean names"() {
    expect:
      camundaHistoryServiceBean
      !historyService
      alternativeHistoryServiceName
  }

}