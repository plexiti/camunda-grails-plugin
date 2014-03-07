import grails.plugin.camunda.CamundaPluginSupport

class CamundaGrailsPlugin {

    def title = "camunda BPM Plugin"
    def version = "0.1.0-SNAPSHOT"
    def author = "Martin Schimak"
    def authorEmail = "martin.schimak@plexiti.com"
    def organization = [ name: "plexiti GmbH", url: "http://plexiti.com" ]
    def license = "APACHE"

    def description = '''\
This plugin integrates the camunda BPM platform with Grails. camunda BPM is a flexible framework for 
workflow and process automation. It's core is a native BPMN 2.0 process engine that runs inside the Java 
Virtual Machine. It is a perfect match for the Spring Framework - and therefore for Grails, too.'''

    def documentation = "http://plexiti.github.io/camunda-grails-plugin"
    def scm = [ url: "https://github.com/plexiti/camunda-grails-plugin" ]
    def issueManagement = [ system: "github", url: "https://github.com/plexiti/camunda-grails-plugin/issues" ]
    def grailsVersion = "2.3 > *"

    def pluginExcludes = [ "grails-app/views/error.gsp", "grails-app/processes/" ]

    def doWithSpring = CamundaPluginSupport.doWithSpring

}
