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
            assert (new String(bpmnFile.readBytes()) =~ /SampleProcess/).size() == 4 // number of expected replacements
            assert testFile.exists()
            assert (new String(testFile.readBytes()) =~ /SampleProcess/).size() == 15 // number of expected occurrences
            assert (new String(testFile.readBytes()) =~ /SampleProcessSpec/).size() == 1 // number of expected occurrences
        } finally {
            if (bpmnFile.exists())
                bpmnFile.delete()
            if (testFile.exists())
                testFile.delete()
        }

    }
    
}
