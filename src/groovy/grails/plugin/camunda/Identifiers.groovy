package grails.plugin.camunda

import grails.util.Holders

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
class Identifiers {

    static def generate(String fullName) {
        def identifier = '[a-zA-Z_][a-zA-Z0-9_]*'
        def name = fullName - ~"\\.${Constants.EXTENSION}\$"
        assert name =~ "^(${identifier}\\.)*(${identifier})\$" : "The package and name of your " +
            "new process definition (name = $name) does not qualify as a valid Java identifier. For " +
            "compatibility reasons, please choose a name which qualifies as a valid Java class name " +
            "(including package)."
        [
            name.substring(0, name.contains('.') ? name.lastIndexOf('.') : 0), 
            name.substring(name.lastIndexOf('.') + 1) - ~"$Constants.TYPE\$"
        ]
    }
    
    static def beanName(String beanName) {
        if (beanName.startsWith("camunda") && beanName.endsWith("Bean")) {
            def defaultName = beanName.substring(7, 8).toLowerCase() + beanName.substring(8, beanName.size() - 4)
            def configName = Holders.config.camunda.beans."$defaultName"
            return configName ? configName : defaultName
        }
        return beanName
    }

}
