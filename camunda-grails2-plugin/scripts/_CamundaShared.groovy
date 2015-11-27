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
includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsCreateArtifacts")

/**
 * Gant target that creates a Camunda BPM process definition
 *
 * @author Martin Schimak <martin.schimak@plexiti.com>
 * @since 0.1
 */
target('create-process' : 'Creates a new Camunda BPM process definition.') {
    depends(compile, checkVersion, parseArguments)

    def constants = classLoader.loadClass("grails.plugin.camunda.Constants")
    def identifiers = classLoader.loadClass("grails.plugin.camunda.Identifiers")

    promptForName(type: constants.TYPE)

    def params = argsMap["params"] as String[]
    def process = params[0]
    def overwrite = argsMap["force"]
    def (pkg, name) = identifiers.generate(process)
    
    def path = argsMap["test"] ? "./target/cli-output" : "."
    def testPath = "${path}/test/integration"

    try {
        def file = new File("${basedir}/${path}/${constants.PROCESS_PATH}/${pkg.replace('.', '/')}/${name}${constants.TYPE}.${constants.EXTENSION}")
        if (!overwrite && file.exists()) {
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
        file = new File("${basedir}/${testPath}/${pkg.replace('.', '/')}/${name}${constants.TYPE}Spec.groovy")
        if (overwrite && file.exists()) {
            file.delete()
        }
        createArtifact(
            name: "${pkg}.${name}",
            suffix: "ProcessSpec",
            type: "Process",
            path: testPath,
            //superClass: superClass,
            templatePath:"templates/testing",
            skipPackagePrompt: true
        )
    } catch (e) {
        e.printStackTrace()
        exit 1
    }

}
