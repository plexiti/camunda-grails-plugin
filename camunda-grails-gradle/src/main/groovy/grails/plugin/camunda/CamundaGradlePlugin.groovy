package grails.plugin.camunda

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.WarPlugin

import static grails.plugin.camunda.Configuration.config

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
class CamundaGradlePlugin implements Plugin<Project> {

  void apply(Project project) {
    project.getPlugins().apply(JavaPlugin.class);
    // TODO Depends on Grails 'Gradle Plugin' or Grails 'Plugin Gradle Plugin' 
    // TODO Depends on Grails Integration Test Plugin and existence of such tests
    project.integrationTestClasses {
      doFirst {
        processResources(project, "${project.sourceSets.integrationTest.output.classesDir}")
      }
    }
    project.testClasses {
      doFirst {
        processResources(project, "${project.sourceSets.test.output.classesDir}")
      }
    }
    project.classes {
      doFirst {
        processResources(project, "${project.sourceSets.main.output.classesDir}")
      }
    }
    // TODO Depends on Grails 'War' Plugin applied before, if it is defined for the project
    if (project.plugins.hasPlugin(WarPlugin))
      war(project) 
  }

  static void war(Project project) {
    def stagingDir = "${project.buildDir}/tmp/camunda"
    project.war {
      doFirst {
        project.ant.mkdir(dir: stagingDir)
        // support 'shared' deployment scenario
        // TODO Depends on accessibility of grails configuration
        // if (config('camunda.deployment.scenario') == 'shared') { 
          // if (config('camunda.deployment.shared.container') == 'tomcat') {
            // for tomcat, provide resource links, but respect 'web-app/META-INF/context.xml'
            if (!new File("${project.projectDir}/src/main/webapp/META-INF/context.xml").exists()) {
              project.ant.mkdir(dir: "${stagingDir}/META-INF")
              def inputStream
              new FileOutputStream("${stagingDir}/META-INF/context.xml") <<
                (inputStream = getClass().getResourceAsStream('/META-INF/containers/tomcat-context.xml'))
              inputStream.close()
            }
          // }
        // }
      } 
      from stagingDir
      /* TODO Do not bundle libraries provided by container
      project.ant.delete() {
        fileset(dir: "${stagingDir}/WEB-INF/lib") {
          config('camunda.deployment.shared.war.excludes').each { String exclude ->
            include(name: exclude)
          }
          config('camunda.deployment.shared.war.includes').each { String include ->
            exclude(name: include)
          }
        }
      }
      */
    }
  }

  static void processResources(Project project, String classesDir) {
    def processesDir = "${project.projectDir}/${Constants.PROCESS_PATH}"
    // copy resources to classes dir
    project.ant.mkdir(dir: classesDir)
    project.ant.copy(todir: classesDir) {
      fileset(dir: processesDir){
        include(name: "**")
        include(name: "META-INF/**")
      }
    }
    // create empty processes.xml but respect 'grails-app/META-INF/processes.xml'
    project.ant.mkdir(dir: "${classesDir}/META-INF")
    project.ant.touch(file: "${classesDir}/META-INF/processes.xml")
  }

}

