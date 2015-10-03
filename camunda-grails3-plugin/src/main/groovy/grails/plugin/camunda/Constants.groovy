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
package grails.plugin.camunda

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface Constants {

    def TYPE = "Process";
    def EXTENSION = "bpmn";
    def PROCESS_PATH = "grails-app/processes";

    /**
     * Generate proper package and name for a given full name
     * @param fullName the full name of the process definition
     */
    def generate = { String fullName ->
        def identifier = '[a-zA-Z_][a-zA-Z0-9_]*'
        def name = fullName - ~"\\.${EXTENSION}\$"
        assert name =~ "^(${identifier}\\.)*(${identifier})\$" : "The package and name of your " +
          "new process definition (name = $name) does not qualify as a valid Java identifier. For " +
          "compatibility reasons, please choose a name which qualifies as a valid Java class name " +
          "(including package)."
        [
          name.substring(0, name.contains('.') ? name.lastIndexOf('.') : 0),
          name.substring(name.lastIndexOf('.') + 1) - ~"$TYPE\$"
        ]
    }
    
}