package grails.plugin.camunda.test

import grails.plugin.camunda.CamundaBpmApi
import grails.test.AbstractCliTestCase
import groovy.xml.Namespace
import org.camunda.bpm.ProcessApplicationService
import org.camunda.bpm.ProcessEngineService

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
class CreateWarTests extends AbstractCliTestCase {

    void "test development war file"() {
        execute(["dev", "-Dgrails.war.exploded=true", camundaVersionProperty, "war"]) {
            assert new File("$targetDir/WEB-INF/classes/META-INF/processes.xml").exists()
            assert !new File("$targetDir/META-INF/context.xml").exists()
            assert jars.find { it.startsWith('groovy-all-') }
            assert jars.count { it.startsWith('camunda-') } == numberOfJars
            assert !webXml.depthFirst().find { Node node ->
                node.name() == javaee.'res-type' && node.text() == ProcessEngineService.name
            }
            assert !webXml.depthFirst().find { Node node ->
                node.name() == javaee.'res-type' && node.text() == ProcessApplicationService.name
            }
        }
    }

    void "test production war file"() {
        execute(["prod", "-Dgrails.war.exploded=true", camundaVersionProperty, "war"]) {
            assert new File("$targetDir/META-INF/context.xml").exists()
            assert new File("$targetDir/WEB-INF/classes/META-INF/processes.xml").exists()
            assert !jars.find { it.startsWith('groovy-all') }
            assert !jars.find { it.startsWith('camunda-') && !it.startsWith('camunda-engine-spring-') }
            assert webXml.depthFirst().find { Node node ->
                node.name() == javaee.'res-type' && node.text() == ProcessEngineService.name
            }
            assert webXml.depthFirst().find { Node node ->
                node.name() == javaee.'res-type' && node.text() == ProcessApplicationService.name
            }
        }
    }

    def javaee = new Namespace("http://java.sun.com/xml/ns/javaee", 'javaee')
    File stageDir = new File("$outputDir/../work/stage")
    File targetDir
    List<String> jars = []
    Node webXml
    def camundaVersionProperty = '-Dcamunda-bpm.version=' + System.getProperty('camunda-bpm.version') ?: '7.4.0'
    def numberOfJars = CamundaBpmApi.supports('7.4') ? 12 : 5
    
    void setUp() {
        timeout = 10 * 60 * 1000
    }
    
    void tearDown() {
        if (stageDir.exists())
            stageDir.deleteDir()
        if (targetDir.exists())
            targetDir.deleteDir()
    }
    
    void execute(List<String> params, Closure asserts) {
        targetDir = new File(new File("${outputDir}/../stage-${params[0]}").canonicalPath)
        if (!targetDir.exists()) {
            super.execute(params)
            assert waitForProcess() == 0
            verifyHeader()
            stageDir.renameTo(targetDir)
        }
        new File("$targetDir/WEB-INF/lib").eachFileRecurse { jars.add(it.name) }
        webXml = new XmlParser().parse(new File("$targetDir/WEB-INF/web.xml"))
        asserts.call()
    }

}
