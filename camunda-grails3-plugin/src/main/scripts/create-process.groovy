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

import grails.plugin.camunda.Constants

/**
 * Grails script that creates a Camunda BPM process definition
 *
 * @author Martin Schimak <martin.schimak@plexiti.com>
 * @since 0.1 (migrated to a Grails 3.x script with 0.6)
 */
description ("Creates a new Camunda BPM process definition.") {
  usage "grails create-process [NAME]"
  argument name:'Name', description: "The package and name of the process definition, e.g. my.package.SampleProcess"
  flag name:'force', description: "Whether to overwrite existing files"
}

def classLoader = this.class.classLoader
def constants = classLoader.loadClass("grails.plugin.camunda.Constants") as Constants

def path = constants.PROCESS_PATH
def type = constants.TYPE
def extension = constants.EXTENSION

def (pkg, name) = constants.generate(args[0])
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
