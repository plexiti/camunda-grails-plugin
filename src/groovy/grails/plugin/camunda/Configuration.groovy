package grails.plugin.camunda

import grails.util.Environment
import grails.util.Holders
import org.camunda.bpm.application.AbstractProcessApplication
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration
import org.camunda.bpm.engine.spring.application.SpringServletProcessApplication
import org.springframework.beans.BeanUtils

import javax.xml.bind.DatatypeConverter

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 * @since 0.4.0
 */
class Configuration {
  
  private static def defaults = [
    /* By default works with an 'embedded' engine in case an engine configuration exists. 
     * This in turn is by default the case for the dev and test environments, see below. */
    'camunda.deployment.scenario' : {
        config('camunda.engine.configuration') ? 'embedded' : 'shared'
    },
    /* By default use org.camunda.bpm.engine.spring.application.SpringServletProcessApplication
     * (relevant for shared scenarios only). */
    'camunda.deployment.application' : SpringServletProcessApplication,
    /* By default auto reload bpmn deployment in 'dev' and 'test' environments, for all
     * others disable it. */
    'camunda.deployment.autoreload' : {
      Environment.current in [ Environment.DEVELOPMENT, Environment.TEST ]
    },
    /* By default assume 'tomcat' to be the target servlet container (relevant for shared 
     * scenarios only). */ 
    'camunda.deployment.shared.container' : 'tomcat',
    /* By default update database schema for environments 'dev' and 'test', for all 
     * others don't touch camunda's default. */
    'camunda.engine.configuration.databaseSchemaUpdate' : {
      Environment.current in [ Environment.DEVELOPMENT, Environment.TEST ] ? true : null
    },
    /* By default turn off job executor to enable single threaded testing during test 
     * phases (except functional), for all other cases enable it in 'dev' and 'test',
     * for all others don't touch camunda's default. */
    'camunda.engine.configuration.jobExecutorActivate' : {
      config('grails.test.phase') && config('grails.test.phase') != 'functional' ? false : 
        (Environment.current in [ Environment.DEVELOPMENT, Environment.TEST ] ? true : null)
    },
    /* By default deploy bpmn resources in classpath for environments 'dev' and 'test', 
     * for all others don't touch camunda's default. */
    'camunda.engine.configuration.deploymentResources' : {
      Environment.current in [ Environment.DEVELOPMENT, Environment.TEST ] ?
        ['classpath:/**/*.bpmn', 'classpath:/**/*.bpmn20.xml'] :
        null
    },
  ]

  /**
   * Validators exist for plugin specific configuration only, not for 
   * configuration dynamically used for camunda provided configuration beans.
   */
  private static def validators = [
    'camunda.deployment.scenario' : { property, value ->
      def allowed = ['embedded', 'shared', 'none']
      assert value in allowed :
        "Config property $property must be one of $allowed, instead it was: '$value'"
    },
    'camunda.deployment.application' : { property, value ->
      assert AbstractProcessApplication.isAssignableFrom(value as Class) :
        "Config property $property must be assignable from ${AbstractProcessApplication.name}, instead it was: '$value'"
    },
    'camunda.deployment.autoreload' : { property, value ->
      assert value instanceof Boolean :
        "Config property $property must be instance of ${Boolean.class.name}, instead it was: '$value'"
    },
    'camunda.deployment.shared.container' : { property, value ->
      def allowed = ['tomcat']
      assert value in allowed :
      "Config property $property must be one of $allowed, instead it was: '$value'"
    },
  ]

  /**
   * Type conversion for string values exists for (non-string) configuration only
   */
  private static def converters = [
    'camunda.deployment.application' : { String p, String v -> 
      Class.forName(v) 
    },
    'camunda.deployment.autoreload' : { String p, String v ->
      v == 'true' ? true : (v == 'false' ? false : v) 
    },
    'camunda.engine.configuration' : { String p, String v ->
      def name = BeanUtils.findPropertyType(p, SpringProcessEngineConfiguration).simpleName
      name = name.substring(0, 1).toUpperCase() + name.substring(1)
      return DatatypeConverter."parse$name"(v)
    }
  ]
  
  private static def convert(String key, String value, String prop = null) {
    if (converters.containsKey(key)) {
      return converters.get(key).call(prop, value)
    } else if (key.contains('.')) {
      def idx = key.lastIndexOf('.')
      return convert(key.substring(0, idx), value, key.substring(idx + 1))
    } else {
      return value
    }
  }

  static def config(String property, ConfigObject conf = Holders.getGrailsApplication()?.config) {
    assert conf
    // First look, whether a system property exists
    def value = System.getProperty(property)
    // If not, look, whether a grails configuration property exists
    value = value == null ? conf.flatten().get(property) : value
    // If not, look, whether a default value exists
    value = value == null ? defaults.get(property) : value
    // In case the value we found is a closure, we evaluate it now
    value = value instanceof Closure ? value.call() : value
    try { // In case we deal with a string value we try to convert it now
      value = value instanceof String ? convert(property, value) : value
    } catch (RuntimeException e) {}
    if (value != null) {
    // In case we have a value and know a validator, we use it now
      validators.get(property)?.call(property, value)
    } else {
      // In case we still don't have a value, it could be that we were looking for a 
      // parent configuration property of many children, so we try to build it by
      // putting all keys into a set
      def keys = defaults.keySet().toList()
      keys.addAll(System.properties.stringPropertyNames())
      keys.addAll(Holders.getGrailsApplication().config.flatten().keySet().collect { it.toString() })
      value = [:]
      // then we recursively evaluate the config value of all matching keys, but just 
      // use those which are set (don't evaluate to null)
      keys.findAll { it.startsWith("${property}.") }.each { prop -> 
        def v = config(prop)
        if (v != null) 
          value[prop] = v 
      }
      // In case we found such values, we were looking for a parent, in case we did 
      // not we were looking for a configuration value which evaluates to null (either 
      // parent or child)
      value = value ?: null
    }
    return value
  }
  
}
