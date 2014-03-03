/**
 * Gant script that creates a camunda BPM process definition
 *
 * @author Martin Schimak <martin.schimak@plexiti.com>
 * @since 0.1
 */
includeTargets << new File(camundaPluginDir, "scripts/_CamundaShared.groovy")

target('default' : 'Creates a new camunda BPM process definition.') {
    "create-process"()    
}

USAGE = """
    create-process [NAME]

where
    NAME = The package and name of the process definition, e.g. my.package.SampleProcess 
           If not provided, this command will ask you for the name.
"""
