package grails.plugin.camunda.test

import grails.test.AbstractCliTestCase
import groovy.xml.Namespace
import org.camunda.bpm.ProcessApplicationService
import org.camunda.bpm.ProcessEngineService

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
class CreateWarTests extends AbstractCliTestCase {

    void "test development war file"() {
        execute(["dev", "-Dgrails.war.exploded=true", "war"]) {
            assert new File("$stageDir/WEB-INF/classes/META-INF/processes.xml").exists()
            assert !new File("$stageDir/META-INF/context.xml").exists()
            assert jars.find { it.startsWith('groovy-all-') }
            assert jars.count { it.startsWith('camunda-') } == 5
            assert !webXml.depthFirst().find { Node node ->
                node.name() == javaee.'res-type' && node.text() == ProcessEngineService.name
            }
            assert !webXml.depthFirst().find { Node node ->
                node.name() == javaee.'res-type' && node.text() == ProcessApplicationService.name
            }
        }
    }

    void "test production war file"() {
        execute(["prod", "-Dgrails.war.exploded=true", "war"]) {
            assert new File("$stageDir/META-INF/context.xml").exists()
            assert new File("$stageDir/WEB-INF/classes/META-INF/processes.xml").exists()
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
    List<String> jars = []
    Node webXml
    
    void setUp() {
        timeout = 10 * 60 * 1000
        if (stageDir.exists())
            stageDir.deleteDir()
    }
    
    void tearDown() {
        if (stageDir.exists())
            stageDir.deleteDir()
    }
    
    void execute(List<String> params, Closure asserts) {
        super.execute(params)
        assert waitForProcess() == 0
        verifyHeader()
        new File("$stageDir/WEB-INF/lib").eachFileRecurse { jars.add(it.name) }
        webXml = new XmlParser().parse(new File("$stageDir/WEB-INF/web.xml"))
        asserts.call()
    }

}
