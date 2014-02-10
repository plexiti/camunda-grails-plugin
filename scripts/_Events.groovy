/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */

def processesDir = "${basedir}/grails-app/processes"

eventTestPhaseStart = { args ->
    System.properties["grails.test.phase"] = args
}

eventCreateWarStart = { warName, stagingDir ->
    if ((processesDir as File).exists()) {
        ant.copy(todir: "${stagingDir}/WEB-INF/classes") {
            fileset(dir: processesDir) {
                include(name: "**/*.bpmn")
            }
        }
    }
}
