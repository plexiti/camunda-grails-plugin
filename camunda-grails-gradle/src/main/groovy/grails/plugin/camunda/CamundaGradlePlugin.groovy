package grails.plugin.camunda

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
class CamundaGradlePlugin implements Plugin<Project> {

    void apply(Project project) {
        project.apply plugin: "java"
        project.sourceSets {
            integrationTest {
                resources {
                    srcDir Constants.PROCESS_PATH
                }
                output.resourcesDir = output.classesDir
            }
            main {
                resources {
                    srcDir Constants.PROCESS_PATH
                }
            }
        }
    }

}

