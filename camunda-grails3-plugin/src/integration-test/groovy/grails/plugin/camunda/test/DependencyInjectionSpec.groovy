package grails.plugin.camunda.test

import grails.plugin.camunda.CamundaBpmVersion
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
      CamundaBpmVersion.isAtLeast('7.2.0') ? caseService : true
      CamundaBpmVersion.isAtLeast('7.2.0') ? filterService : true
  }

}