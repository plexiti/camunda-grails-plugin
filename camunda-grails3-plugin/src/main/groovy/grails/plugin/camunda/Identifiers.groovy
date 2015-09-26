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

import static grails.plugin.camunda.Configuration.config

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
class Identifiers {
    
    static def beanName(String beanName) {
        if (beanName.startsWith("camunda") && beanName.endsWith("Bean")) {
            def defaultName = beanName.substring(7, 8).toLowerCase() + beanName.substring(8, beanName.size() - 4)
            def configName = config("camunda.beans.$defaultName")
            return configName ?: defaultName
        }
        return beanName
    }

}
