// configuration for plugin testing - will not be included in the plugin zip

grails.doc.title = "camunda Grails plugin"
grails.doc.subtitle = """The camunda Grails plugin integrates the camunda BPM platform with the Grails web application framework."""
grails.doc.copyright = """\
<div>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">
<img alt="Creative Commons 4.0" style="border-width:0; vertical-align: middle" src="http://i.creativecommons.org/l/by/4.0/80x15.png"></img></a>
<span style="vertical-align: middle"><a href="http://github.com/martinschimak" property="cc:attributionName" rel="cc:attributionURL">Martin Schimak</a>, 
<a href="http://plexiti.com" property="cc:attributionName" rel="cc:attributionURL">plexiti GmbH</a></span>
</div>
"""
grails.doc.images = new File("src/docs/images")

log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'
}
