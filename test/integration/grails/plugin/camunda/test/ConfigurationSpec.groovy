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
  
  def init(String property) {
    System.clearProperty(property)
    Configuration.clearConfigObjectProperty(property)
    assert !System.hasProperty(property)
    assert !Configuration.configObjectContainsKey(property)
    assert !grailsApplication.config.flatten().containsKey(property)
  }
  
  def setup() {
    [
      'camunda.deployment.test',
      'camunda.deployment.scenario',
      'camunda.deployment.application',
      'camunda.deployment.shared.container',
      'camunda.deployment.autoreload',
      'camunda.engine.configuration.jobExecutorActivate',
      'camunda.engine.configuration.databaseSchemaUpdate',
      'camunda.engine.configuration.deploymentResources',
    ].each { init(it) }
  }
  
  def "Test programmatical retrieval of configuration values"() {
    expect:
      Configuration.getConfigObjectProperty('grails.doc.title') == 'camunda Grails plugin'
    and:
      Configuration.getConfigObjectProperty('camunda.deployment.scenario') == null
    and:
      !Configuration.configObjectContainsKey('camunda.deployment.scenario')
    when:
      grailsApplication.config.camunda.deployment.scenario = 'embedded'
    then:
      Configuration.getConfigObjectProperty('camunda.deployment.scenario') == 'embedded'
    and:
      Configuration.configObjectContainsKey('camunda.deployment.scenario')
    when:
      grailsApplication.config.camunda.deployment.scenario = null
    then:
      Configuration.getConfigObjectProperty('camunda.deployment.scenario') == null
    and:
      Configuration.configObjectContainsKey('camunda.deployment.scenario')
    and:
      grailsApplication.config.flatten().containsKey('camunda.deployment.scenario')
  }

  def "Test programmatical change of configuration values"() {
    expect:
      Configuration.getConfigObjectProperty('camunda.deployment.scenario') == null
    and:
      !Configuration.configObjectContainsKey('camunda.deployment.scenario')
    when:
      Configuration.setConfigObjectProperty('camunda.deployment.scenario', 'embedded')
    then:
      Configuration.getConfigObjectProperty('camunda.deployment.scenario') == 'embedded'
    and:
      Configuration.configObjectContainsKey('camunda.deployment.scenario')
    when:
      Configuration.setConfigObjectProperty('camunda.deployment.scenario', null)
    then:
      Configuration.getConfigObjectProperty('camunda.deployment.scenario') == null
    and:
      Configuration.configObjectContainsKey('camunda.deployment.scenario')
    and:
      grailsApplication.config.flatten().containsKey('camunda.deployment.scenario')
  }

  def "Test programmatical clearing of configuration values"() {
    expect:
      Configuration.getConfigObjectProperty('camunda.deployment.scenario') == null
    and:
      !Configuration.configObjectContainsKey('camunda.deployment.scenario')
    when:
      Configuration.setConfigObjectProperty('camunda.deployment.scenario', 'embedded')
    then:
      Configuration.getConfigObjectProperty('camunda.deployment.scenario') == 'embedded'
    and:
      Configuration.configObjectContainsKey('camunda.deployment.scenario')
    when:
      Configuration.clearConfigObjectProperty('camunda.deployment.scenario')
    then:
      Configuration.getConfigObjectProperty('camunda.deployment.scenario') == null
    and:
      !Configuration.configObjectContainsKey('camunda.deployment.scenario')
    and:
      !grailsApplication.config.flatten().containsKey('camunda.deployment.scenario')
  }
  
  def "Test to directly programmatically set a value hithereto unknown"() {
    when:
      Configuration.setConfigObjectProperty('x.y.z', 'jboss')
    then:
      grailsApplication.config.x.y.z == 'jboss'
  }

  @Unroll
  def "Test that property '#property' has default value '#value'."() {
    expect:
      Configuration.config(property) == value
    where:
      property                                            | value
      'camunda.deployment.scenario'                       | 'embedded'
      'camunda.deployment.application'                    | SpringServletProcessApplication
      'camunda.deployment.autoreload'                     | Environment.current in [Environment.DEVELOPMENT, Environment.TEST]
      'camunda.deployment.shared.container'               | 'tomcat'
      'camunda.deployment.shared.war.excludes'            | ['camunda-*.jar', 'groovy-all-*.jar']
      'camunda.deployment.shared.war.includes'            | ['camunda-engine-spring-*.jar']
      'camunda.engine.configuration.databaseSchemaUpdate' | { Environment.current in [Environment.DEVELOPMENT, Environment.TEST] ? true : null }.call()
      'camunda.engine.configuration.jobExecutorActivate'  | false
      'camunda.engine.configuration.deploymentResources'  | { Environment.current in [Environment.DEVELOPMENT, Environment.TEST] ? ['classpath:/**/*.bpmn', 'classpath:/**/*.bpmn20.xml'] : null }.call()
  }

  def "Test that a property with subproperties returns an expected map."() {
    when:
      def deployment = Configuration.config('camunda.deployment') as Map
    then:
      deployment instanceof Map
      deployment.keySet() == [
        'camunda.deployment.scenario',
        'camunda.deployment.application',
        'camunda.deployment.autoreload',
        'camunda.deployment.shared.container',
        'camunda.deployment.shared.war.excludes',
        'camunda.deployment.shared.war.includes',
      ].toSet()
      deployment['camunda.deployment.autoreload'] == Environment.current in [ Environment.DEVELOPMENT, Environment.TEST ]
  }

  @Unroll
  def "Test that property '#property' is not allowed to have value '#value'."() {
    given:
      Configuration.setConfigObjectProperty(property, value)
    when:
      Configuration.config(property) == value
    then:
      thrown(AssertionError)
    where:
      property                                 | value
      'camunda.deployment.scenario'            | 'something'
      'camunda.deployment.application'         | getClass()
      'camunda.deployment.autoreload'          | 'untrue'
      'camunda.deployment.shared.container'    | 'jboss'
      'camunda.deployment.shared.war.excludes' | 'abc'
      'camunda.deployment.shared.war.includes' | ['abc'.class]
  }

  @Unroll
  def "Test that property '#property' has configured value '#value'."() {
    when:
      Configuration.setConfigObjectProperty(property, value)
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
      'camunda.deployment.shared.war.excludes'            | ['abc']
      'camunda.deployment.shared.war.includes'            | ['abc', 'def']
  }

  @Unroll
  def "Test that property '#property' can be overridden by system property '#overridden'."() {
    given:
      Configuration.setConfigObjectProperty(property, value)
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
  def "Test that property '#property' has value null when explicitely configured to null."() {
    when:
      Configuration.setConfigObjectProperty(property, null)
    then:
      Configuration.config(property) == null
    where:
      property << [
        'camunda.engine.configuration.databaseSchemaUpdate',
        'camunda.engine.configuration.jobExecutorActivate',
        'camunda.engine.configuration.deploymentResources',      
      ]                                            
  }

  @Unroll
  def "Test that property '#property' has value null when explicitely overridden with empty system property."() {
    given:
      Configuration.setConfigObjectProperty(property, value)
    when:
      System.setProperty(property, '')
    then:
      Configuration.config(property) == null
    where:
      property                                            | value
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
      deployment.keySet() == [
        'camunda.deployment.test',
        'camunda.deployment.scenario',
        'camunda.deployment.application',
        'camunda.deployment.shared.container',
        'camunda.deployment.autoreload',
        'camunda.engine.configuration.jobExecutorActivate',
        'camunda.engine.configuration.databaseSchemaUpdate',
        'camunda.engine.configuration.deploymentResources',
        'camunda.deployment.shared.war.excludes',
        'camunda.deployment.shared.war.includes',
      ].toSet()
      deployment['camunda.deployment.scenario'] == 'shared'
  }

}
