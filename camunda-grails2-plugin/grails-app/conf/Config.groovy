// configuration for plugin testing - will not be included in the plugin zip

grails.doc.title = "camunda Grails plugin"
grails.doc.logo = """<span style="white-space: nowrap"><img alt="camunda Logo" title="camunda Grails plugin" src="http://plexiti.github.io/camunda-grails-plugin/img/camunda.png" style="margin-right: 10px"/><span style="vertical-align: middle; font-size: 2.5em">camunda Grails plugin</span><br/><br/></span>"""
grails.doc.sponsorLogo = """<span style="vertical-align: middle"><a href="http://plexiti.com" target="_blank"><img alt="plexiti Logo" title="plexiti GmbH" src="http://plexiti.github.io/camunda-grails-plugin/img/plexiti.png"/></a></span>"""
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
    root {
        error()
    }
}
