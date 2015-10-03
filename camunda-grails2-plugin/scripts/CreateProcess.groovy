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

USAGE = """| Command: create-process

| Description:
Creates a new Camunda BPM process definition.

| Usage:
grails create-process [NAME]

| Arguments:
* Name - The package and name of the process definition, e.g. my.package.SampleProcess (REQUIRED)

| Flags:
* force - Whether to overwrite existing files
"""
