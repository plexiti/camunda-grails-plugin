/*
 * Copyright 2015 Martin Schimak - plexiti GmbH
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
 * Grails script that creates a Camunda BPM process definition
 *
 * @author Martin Schimak <martin.schimak@plexiti.com>
 * @since 0.1 (migrated to a Grails 3.x script with 0.6)
 */

description ("Creates a new Camunda BPM process definition.") {
  usage "grails create-process [NAME]"
  argument name:'Name', description:"The package and name of the process definition, e.g. my.package.SampleProcess"
  flag name:'force', description:"Whether to overwrite existing files"
}

def generate = { String fullName, String type, String extension ->
  def identifier = '[a-zA-Z_][a-zA-Z0-9_]*'
  def name = fullName - ~"\\.${extension}\$"
  assert name =~ "^(${identifier}\\.)*(${identifier})\$" : "The package and name of your " +
    "new process definition (name = $name) does not qualify as a valid Java identifier. For " +
    "compatibility reasons, please choose a name which qualifies as a valid Java class name " +
    "(including package)."
  [
    name.substring(0, name.contains('.') ? name.lastIndexOf('.') : 0),
    name.substring(name.lastIndexOf('.') + 1) - ~"$type\$"
  ]
}

def path = "grails-app/processes"
def type = "Process"
def extension = "bpmn"

def (pkg, name) = generate(args[0], type, extension)
def artifact = "${pkg}.${name}${type}"
def model = model(artifact)
def overwrite = flag('force') ? true : false

render(
  template: "processes/${type}.${extension}.template",
  destination: file( "${path}/${artifact.replace('.', '/')}.${extension}"),
  model: model,
  overwrite: overwrite
)
render(
  template: "testing/${type}.groovy.template",
  destination: file( "src/integration-test/groovy/${artifact.replace('.', '/')}Spec.groovy"),
  model: model,
  overwrite: overwrite
)
