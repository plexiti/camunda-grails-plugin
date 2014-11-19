package grails.plugin.camunda

import grails.util.Environment
import grails.util.Holders
import org.camunda.bpm.application.AbstractProcessApplication
import org.camunda.bpm.engine.spring.application.SpringServletProcessApplication

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
class Configuration {
  
  static def defaults = [
    'camunda.deployment.scenario' : {
      Holders.getGrailsApplication().config.camunda.engine.configuration ? 'embedded' :
        (Environment.current in [ Environment.DEVELOPMENT, Environment.TEST ] ? 'embedded' : 'shared')
    },
    'camunda.deployment.application' : SpringServletProcessApplication,
    'camunda.deployment.container' : 'tomcat',
    'camunda.deployment.autoreload' : {
      Environment.current in [ Environment.DEVELOPMENT, Environment.TEST ]
    }
  ]

  static def validators = [
    'camunda.deployment.scenario' : { property, value ->
      def allowed = ['embedded', 'shared']
      assert value in allowed :
        "Config property $property must be one of $allowed, instead it was: $value"
    },
    'camunda.deployment.application' : { property, value ->
      assert AbstractProcessApplication.isAssignableFrom(value as Class) :
        "Config property $property must be assignable from ${AbstractProcessApplication.name}, instead it was: $value"
    },
    'camunda.deployment.container' : { property, value ->
      def allowed = ['tomcat']
      assert value in allowed :
      "Config property $property must be one of $allowed, instead it was: $value"
    },
    'camunda.deployment.autoreload' : { property, value ->
      assert value instanceof Boolean :
      "Config property $property must be instance of ${Boolean.class.name}, instead it was: $value"
    }
  ]

  static def converters = [
    'camunda.deployment.scenario' : { it },
    'camunda.deployment.application' : { Class.forName(it as String) },
    'camunda.deployment.container' : { it },
    'camunda.deployment.autoreload' : { it == 'true' ? true : (it == 'false' ? false : it) }
  ]

  static def config(String property, config = Holders.getGrailsApplication()?.config) {
    assert config
    def value = System.getProperty(property)
    value = value == null ? config.flatten().get(property) : value
    value = value == null ? defaults.get(property) : value
    value = value instanceof Closure ? value.call() : value
    try { value = value instanceof String ? converters.get(property).call(value) : value } catch (RuntimeException e) {}
    validators.get(property).call(property, value)
    return value
  }
  
}
