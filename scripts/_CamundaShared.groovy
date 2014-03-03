includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsCreateArtifacts")

/**
 * Gant target that creates a camunda BPM process definition
 *
 * @author Martin Schimak <martin.schimak@plexiti.com>
 * @since 0.1
 */
target('create-process' : 'Creates a new camunda BPM process definition.') {
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
