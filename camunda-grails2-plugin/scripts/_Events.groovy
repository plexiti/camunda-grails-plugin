/*
 * Copyright 2014 Martin Schimak - plexiti GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */

// when testing, remember the test phase identifier
eventTestPhaseStart = { phase ->
    System.properties['grails.test.phase'] = phase
}

eventTestPhaseEnd = { phase ->
    System.clearProperty('grails.test.phase')
}

// when creating a war
eventCreateWarStart = { warName, stagingDir ->
    def constants = classLoader.loadClass("grails.plugin.camunda.Constants")
    def config = classLoader.loadClass("grails.plugin.camunda.Configuration").&config
    // 1) include the resources in the processes dir
    def processesDir = "${basedir}/${constants.PROCESS_PATH}"
    if ((processesDir as File).exists()) {
        ant.copy(todir: "${stagingDir}/WEB-INF/classes") {
            fileset(dir: processesDir) {
                include(name: "**")
                include(name: "META-INF/**")
            }
        }
    }
    // 2) support 'shared' deployment scenario
    if (config('camunda.deployment.scenario') == 'shared') {
        // create empty processes.xml but respect 'grails-app/conf/META-INF/processes.xml'
        ant.mkdir(dir: "${stagingDir}/WEB-INF/classes/META-INF")
        ant.touch(file: "${stagingDir}/WEB-INF/classes/META-INF/processes.xml")
        // for tomcat, provide resource links, but respect 'web-app/META-INF/context.xml'
        if (config('camunda.deployment.shared.container') == 'tomcat') {
            ant.mkdir(dir: "${stagingDir}/META-INF")
            ant.copy(file: "${camundaPluginDir}/web-app/META-INF/context.xml", todir: "${stagingDir}/META-INF")
        }
        // do not bundle libraries provided by container
        ant.delete() {
            fileset(dir: "${stagingDir}/WEB-INF/lib") {
                config('camunda.deployment.shared.war.excludes').each { String exclude ->
                    include(name: exclude)
                }
                config('camunda.deployment.shared.war.includes').each { String include ->
                    exclude(name: include)
                }
            }
        }
    }
}

// when compiling, clean up *.bpmn from dev tomcat work dir
// (avoids camunda's duplicate bpmn deployment id error message)
eventCompileStart = { type ->
    def tomcatDir = "${projectWorkDir}/tomcat"
    if ((tomcatDir as File).exists()) {
        ant.delete() {
            fileset(dir: tomcatDir) {
                include(name: "**/*.bpmn")
                include(name: "**/*.bpmn20.xml")
            }
        }
    }
}
