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

    def pluginExcludes = [ "grails-app/processes/" ]

    def doWithSpring = CamundaPluginSupport.doWithSpring

}
