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
def processesDir = "${basedir}/grails-app/processes"

// when testing, remember the test phase identifier
eventTestPhaseStart = { args ->
    System.properties["grails.test.phase"] = args
}

// when creating a war, include the *.bpmn resources
eventCreateWarStart = { warName, stagingDir ->
    if ((processesDir as File).exists()) {
        ant.copy(todir: "${stagingDir}/WEB-INF/classes") {
            fileset(dir: processesDir) {
                include(name: "**/*.bpmn")
            }
        }
    }
}

// when compiling, clean up *.bpmn from dev tomcat work dir
// (avoids camunda's duplicate bpmn deployment id error message)
eventCompileStart = { type ->
    def tomcatWorkDir = "${projectWorkDir}/tomcat/work"
    if ((tomcatWorkDir as File).exists()) {
        ant.delete() {
            fileset(dir: tomcatWorkDir) {
                include(name: "**/*.bpmn")
            }
        }
    }
}
