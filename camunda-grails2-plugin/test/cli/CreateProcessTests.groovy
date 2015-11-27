import grails.test.AbstractCliTestCase

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
class CreateProcessTests extends AbstractCliTestCase {

    void testCreateProcess() {
        
        this.timeout = 5 * 60 * 1000

        def bpmnFile = new File("${workDir}/target/cli-output/grails-app/processes/com/plexiti/SampleProcess.bpmn")
        def testFile = new File("${workDir}/target/cli-output/test/integration/com/plexiti/SampleProcessSpec.groovy")

        if (!bpmnFile.exists() && !testFile.exists()) {
            execute(["create-process", "com.plexiti.SampleProcess", "--force", "--test"])
            assert waitForProcess() == 0
            verifyHeader()
        }

        try {
            assert (new String(bpmnFile.readBytes()) =~ /SampleProcess/).size() == 3 // number of expected replacements
            assert (new String(testFile.readBytes()) =~ /SampleProcess/).size() == 11 // number of expected occurrences
            assert (new String(testFile.readBytes()) =~ /SampleProcessSpec/).size() == 1 // number of expected occurrences
        } finally {
            bpmnFile.delete()
            testFile.delete()
        }
        
    }
    
}
