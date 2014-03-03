import grails.test.AbstractCliTestCase

class CreateProcessTests extends AbstractCliTestCase {

    void testCreateProcess() {

        def bpmnFile = new File("${workDir}/grails-app/processes/com/plexiti/SampleProcess.bpmn")
        def testFile = new File("${workDir}/test/integration/com/plexiti/SampleProcessSpec.groovy")
        assert !bpmnFile.exists()
        assert !testFile.exists()

        try {
            execute(["create-process", "com.plexiti.SampleProcess"])
            assert waitForProcess() == 0
            verifyHeader()
            assert bpmnFile.exists()
            assert testFile.exists()
        } finally {
            if (bpmnFile.exists())
                bpmnFile.delete()
            if (testFile.exists())
                testFile.delete()
        }

    }
    
}
