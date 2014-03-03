
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

target('default' : 'Creates a new camunda BPM process definition.') {
    depends(compile, checkVersion, parseArguments)

    def constants = classLoader.loadClass("grails.plugin.camunda.Constants")
    def identifiers = classLoader.loadClass("grails.plugin.camunda.Identifiers")

    promptForName(type: constants.TYPE)

    for (process in argsMap["params"]) {

        def (pkg, name) = identifiers.generate(process)

        try {
            def file = new File("${basedir}/${constants.PROCESS_PATH}/${pkg.replace('.', '/')}/${name}${constants.TYPE}.${constants.EXTENSION}")
            if (file.exists()) {
                if (!confirmInput("${constants.TYPE} ${name}${constants.TYPE}.${constants.EXTENSION} already exists. Overwrite?","${name}${constants.TYPE}.${constants.EXTENSION}.overwrite")) {
                    return
                }
            }
            file.parentFile?.mkdirs()
            ant.copy(file: "$camundaPluginDir/src/templates/processes/${constants.TYPE}.bpmn.template",
                    tofile: file.path, verbose: true, overwrite: true) {
                filterset {
                    filter token: 'artifact.name', value: "${name}${constants.TYPE}"
                }
            }
            event("CreatedFile", [file])
            createIntegrationTest(name: "${pkg}.${name}", suffix: constants.TYPE)
        } catch (e) {
            e.printStackTrace()
            exit 1
        }

    }

}

USAGE = """
    create-process [NAME]

where
    NAME = The package and name of the process definition, e.g. my.package.SampleProcess 
           If not provided, this command will ask you for the name.
"""

