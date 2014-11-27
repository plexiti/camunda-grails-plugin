package grails.plugin.camunda.test

import grails.plugin.camunda.Configuration
import grails.util.Environment
import org.camunda.bpm.application.impl.ServletProcessApplication
import org.camunda.bpm.engine.spring.application.SpringServletProcessApplication
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
class ConfigurationSpec extends Specification {
  
  def grailsApplication
  
  @Unroll
  def "Test that property '#property' has default value '#value'."() {
    expect:
      Configuration.config(property) == value
    where:
      property                                            | value
      'camunda.deployment.scenario'                       | 'embedded'
      'camunda.deployment.application'                    | SpringServletProcessApplication
      'camunda.deployment.shared.container'               | 'tomcat'
      'camunda.deployment.autoreload'                     | Environment.current in [Environment.DEVELOPMENT, Environment.TEST]
      'camunda.engine.configuration.databaseSchemaUpdate' | { Environment.current in [Environment.DEVELOPMENT, Environment.TEST] ? true : null }.call()
      'camunda.engine.configuration.jobExecutorActivate'  | false
      'camunda.engine.configuration.deploymentResources'  | { Environment.current in [Environment.DEVELOPMENT, Environment.TEST] ? ['classpath:/**/*.bpmn', 'classpath:/**/*.bpmn20.xml'] : null }.call()
  }

  def "Test that a property with subproperties returns an expected map."() {
    when:
      def deployment = Configuration.config('camunda.deployment') as Map
    then:
      deployment instanceof Map
      deployment.size() == 4
      deployment.keySet() == [
        'camunda.deployment.scenario',
        'camunda.deployment.application',
        'camunda.deployment.shared.container',
        'camunda.deployment.autoreload',
      ].toSet()
      deployment['camunda.deployment.autoreload'] == Environment.current in [ Environment.DEVELOPMENT, Environment.TEST ]
  }

  @Unroll
  def "Test that property '#property' is not allowed to have value '#value'."() {
    given:
      grailsApplication.config."$property" = value
    when:
      Configuration.config(property) == value
    then:
      thrown(AssertionError)
    where:
      property                              | value
      'camunda.deployment.scenario'         | 'something'
      'camunda.deployment.application'      | getClass()
      'camunda.deployment.shared.container' | 'jboss'
      'camunda.deployment.autoreload'       | 'untrue'
  }
  
  @Unroll
  def "Test that property '#property' has configured value '#value'."() {
    when:
      grailsApplication.config."$property" = value
    then:
      Configuration.config(property) == value
    where:
      property                                            | value
      'camunda.deployment.scenario'                       | 'shared'
      'camunda.deployment.application'                    | ServletProcessApplication
      'camunda.deployment.shared.container'               | 'tomcat'
      'camunda.deployment.autoreload'                     | false
      'camunda.engine.configuration.databaseSchemaUpdate' | false
      'camunda.engine.configuration.jobExecutorActivate'  | true
      'camunda.engine.configuration.deploymentResources'  | ['classpath:/**/*.bpmn']
  }

  @Unroll
  def "Test that property '#property' can be overridden by system property '#overridden'."() {
    given:
      grailsApplication.config."$property" = value
    when:
      System.setProperty(property, overridden)
    then:
      Configuration.config(property) == actual
    where:
      property                                            | value                           | overridden                     | actual
      'camunda.deployment.scenario'                       | 'shared'                        | 'embedded'                     | 'embedded'
      'camunda.deployment.application'                    | SpringServletProcessApplication | ServletProcessApplication.name | ServletProcessApplication
      'camunda.deployment.shared.container'               | 'jboss'                         | 'tomcat'                       | 'tomcat'
      'camunda.deployment.autoreload'                     | true                            | 'false'                        | false
      'camunda.engine.configuration.jobExecutorActivate'  | true                            | 'false'                        | false
  }

  @Unroll
  def "Test that property '#property' has default value '#value' when explicitely set to null."() {
    when:
      System.clearProperty(property)
      grailsApplication.config."$property" = null
    then:
      Configuration.config(property) == value
    where:
      property                         | value
      'camunda.deployment.scenario'                       | 'embedded'
      'camunda.deployment.application'                    | SpringServletProcessApplication
      'camunda.deployment.shared.container'               | 'tomcat'
      'camunda.deployment.autoreload'                     | Environment.current in [Environment.DEVELOPMENT, Environment.TEST]
      'camunda.engine.configuration.databaseSchemaUpdate' | { Environment.current in [Environment.DEVELOPMENT, Environment.TEST] ? true : null }.call()
      'camunda.engine.configuration.jobExecutorActivate'  | false
      'camunda.engine.configuration.deploymentResources'  | { Environment.current in [Environment.DEVELOPMENT, Environment.TEST] ? ['classpath:/**/*.bpmn', 'classpath:/**/*.bpmn20.xml'] : null }.call()
  }

  def "Test that a property with overriden subproperties returns an expected map."() {
    given:
      System.setProperty('camunda.deployment.test', 'test')
      System.setProperty('camunda.deployment.scenario', 'shared')
    when:
      def deployment = Configuration.config('camunda') as Map
    then:
      deployment instanceof Map
      deployment.size() == 8
      deployment.keySet() == [
        'camunda.deployment.test',
        'camunda.deployment.scenario',
        'camunda.deployment.application',
        'camunda.deployment.shared.container',
        'camunda.deployment.autoreload',
        'camunda.engine.configuration.jobExecutorActivate',
        'camunda.engine.configuration.databaseSchemaUpdate',
        'camunda.engine.configuration.deploymentResources',
      ].toSet()
      deployment['camunda.deployment.scenario'] == 'shared'
  }

}
