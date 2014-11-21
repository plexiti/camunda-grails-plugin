package grails.plugin.camunda.test

import grails.plugin.camunda.Configuration
import grails.util.Environment
import org.camunda.bpm.application.impl.ServletProcessApplication
import org.camunda.bpm.engine.spring.application.SpringServletProcessApplication
import spock.lang.Ignore
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
      property                         | value
      'camunda.deployment.scenario'    | 'embedded'
      'camunda.deployment.application' | SpringServletProcessApplication
      'camunda.deployment.container'   | 'tomcat'
      'camunda.deployment.autoreload'  | Environment.current in [ Environment.DEVELOPMENT, Environment.TEST ]
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
        'camunda.deployment.container',
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
      property                         | value
      'camunda.deployment.scenario'    | 'something'
      'camunda.deployment.application' | getClass()
      'camunda.deployment.container'   | 'jboss'
      'camunda.deployment.autoreload'  | 'untrue'
  }
  
  @Unroll
  def "Test that property '#property' has configured value '#value'."() {
    when:
      grailsApplication.config."$property" = value
    then:
      Configuration.config(property) == value
    where:
      property                         | value
      'camunda.deployment.scenario'    | 'shared'
      'camunda.deployment.application' | ServletProcessApplication
      'camunda.deployment.container'   | 'tomcat'
      'camunda.deployment.autoreload'  | false
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
      property                         | value                           | overridden                     | actual
      'camunda.deployment.scenario'    | 'shared'                        | 'embedded'                     | 'embedded'
      'camunda.deployment.application' | SpringServletProcessApplication | ServletProcessApplication.name | ServletProcessApplication
      'camunda.deployment.container'   | 'jboss'                         | 'tomcat'                       | 'tomcat'
      'camunda.deployment.autoreload'  | true                            | 'false'                        | false
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
      'camunda.deployment.scenario'    | 'embedded'
      'camunda.deployment.application' | SpringServletProcessApplication
      'camunda.deployment.container'   | 'tomcat'
      'camunda.deployment.autoreload'  | Environment.current in [ Environment.DEVELOPMENT, Environment.TEST ]
  }

  def "Test that a property with overriden subproperties returns an expected map."() {
    given:
      System.setProperty('camunda.deployment.test', 'test')
      System.setProperty('camunda.deployment.scenario', 'shared')
    when:
      def deployment = Configuration.config('camunda.deployment') as Map
    then:
      deployment instanceof Map
      deployment.size() == 5
      deployment.keySet() == [
        'camunda.deployment.test',
        'camunda.deployment.scenario',
        'camunda.deployment.application',
        'camunda.deployment.container',
        'camunda.deployment.autoreload',
      ].toSet()
      deployment['camunda.deployment.scenario'] == 'shared'
  }

}
