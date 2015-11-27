package grails.plugin.camunda.test

import grails.plugin.camunda.CamundaBpmApi
import spock.lang.Specification

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
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
  def caseService
  def filterService

  void "Test successful dependency injection of standard beans"() {
    expect:
      processEngine
      processEngineConfiguration
      !processEngineService
      processApplication
      repositoryService
      taskService
      managementService
      identityService
      authorizationService
      runtimeService
      formService
      CamundaBpmApi.supports('7.2') ? caseService : true
      CamundaBpmApi.supports('7.2') ? filterService : true
  }

}