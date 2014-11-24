package grails.plugin.camunda

import grails.util.Environment
import grails.util.Holders
import org.camunda.bpm.application.AbstractProcessApplication
import org.camunda.bpm.engine.spring.application.SpringServletProcessApplication

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 * @since 0.4.0
 */
class Configuration {
  
  static def defaults = [
    /* By default work with an 'embedded' engine in dev and test environments, in all 
     * others with a 'shared' engine, except an explicit embedded engine configuration 
     * already exists (last case mainly for backwards compatibility 0.1.0 -> 0.3.0). */
    'camunda.deployment.scenario' : {
        (Environment.current in [ Environment.DEVELOPMENT, Environment.TEST ] 
          || config('camunda.engine.configuration') ? 'embedded' : 'shared')
    },
    /* By default use org.camunda.bpm.engine.spring.application.SpringServletProcessApplication
     * (relevant for shared scenarios only). */
    'camunda.deployment.application' : SpringServletProcessApplication,
    /* By default assume 'tomcat' to be the target servlet container (relevant for shared 
     * scenarios only). */ 
    'camunda.deployment.container' : 'tomcat',
    /* By default auto reload bpmn deployment in 'dev' and 'test' environments, for all
     * others disable it. */
    'camunda.deployment.autoreload' : {
      Environment.current in [ Environment.DEVELOPMENT, Environment.TEST ]
    },
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
  static def validators = [
    'camunda.deployment.scenario' : { property, value ->
      def allowed = ['embedded', 'shared', 'none']
      assert value in allowed :
        "Config property $property must be one of $allowed, instead it was: '$value'"
    },
    'camunda.deployment.application' : { property, value ->
      assert AbstractProcessApplication.isAssignableFrom(value as Class) :
        "Config property $property must be assignable from ${AbstractProcessApplication.name}, instead it was: '$value'"
    },
    'camunda.deployment.container' : { property, value ->
      def allowed = ['tomcat']
      assert value in allowed :
      "Config property $property must be one of $allowed, instead it was: '$value'"
    },
    'camunda.deployment.autoreload' : { property, value ->
      assert value instanceof Boolean :
      "Config property $property must be instance of ${Boolean.class.name}, instead it was: '$value'"
    }
  ]

  /**
   * Type conversion for string values exists for (non-string) plugin specific configuration 
   * only, not for configuration dynamically used for camunda provided configuration beans.
   */
  static def converters = [
    'camunda.deployment.application' : { Class.forName(it as String) },
    'camunda.deployment.autoreload' : { it == 'true' ? true : (it == 'false' ? false : it) }
  ]

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
    try { // In case we deal with a string value and know a converter, we use it now
      value = value instanceof String && converters.containsKey(property) ? 
      converters.get(property).call(value) : value 
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
