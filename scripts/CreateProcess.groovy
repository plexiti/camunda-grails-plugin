import groovy.transform.Field

/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Gant script that creates a camunda BPM process definition
 *
 * @author Martin Schimak <martin.schimak@plexiti.com>
 * @since 0.1
 */

includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsCreateArtifacts")

@Field static def type = 'Process'
@Field static def ext = 'bpmn'
@Field static def processPath = 'grails-app/processes'

target('default' : 'Creates a new camunda BPM process definition.') {
    depends(checkVersion, parseArguments)

    promptForName(type: type)

    for (process in argsMap["params"]) {

        def (pkg, name) = identifiers(process)

        try {
            def file = new File("${basedir}/${processPath}/${pkg.replace('.', '/')}${name}${type}.${ext}")
            if (file.exists()) {
                if (!confirmInput("${type} ${name}${type}.${ext} already exists. Overwrite?","${name}${type}.${ext}.overwrite")) {
                    return
                }
            }
            file.parentFile?.mkdirs()
            ant.copy(file: "$camundaPluginDir/src/templates/processes/${type}.bpmn.template",
                    tofile: file.path, verbose: true, overwrite: true) {
                filterset {
                    filter token: 'artifact.name', value: "${name}${type}"
                }
            }
            event("CreatedFile", [file])
            createIntegrationTest(name: "${pkg}${name}", suffix: type)
        } catch (e) {
            e.printStackTrace()
            exit 1
        }

    }

}

static def identifiers(String fullName) {
    def identifier = '[a-zA-Z_][a-zA-Z0-9_]*'
    def name = fullName - ~"\\.${ext}\$"
    assert name =~ "^(${identifier}\\.)*(${identifier})\$" : "The package and name of your " +
        "new process definition (name = $name) does not qualify as a valid Java identifier. For " +
        "compatibility reasons, please choose a name which would qualify as a valid Java class name."
    [name.substring(0, name.lastIndexOf('.') + 1), name.substring(name.lastIndexOf('.') + 1) - ~"$type\$"]
}

USAGE = """
    create-process [NAME]

where
    NAME = The package and name of the process definition, e.g. my.package.SampleProcess 
           If not provided, this command will ask you for the name.
"""

