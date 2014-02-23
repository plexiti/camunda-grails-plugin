import grails.test.AbstractCliTestCase

class CreateProcessTests extends AbstractCliTestCase {

    void testCreateProcess() {
        
        //workDir = new File("$workDir/target/test-app")
        //execute(["create-app", "--inplace", "test-app"])

        def pkg = "com.plexiti"
        def processName = "SampleProcess"

        def pkgPath = pkg.replace('.', '/')

        def bpmnFile = new File("${workDir}/grails-app/processes/${pkgPath}/${processName}.bpmn")
        def testFile = new File("${workDir}/test/integration/${pkgPath}/${processName}Spec.groovy")

        assert !bpmnFile.exists()
        assert !testFile.exists()

        execute(["create-process", "com.plexiti.SampleProcess"])

        assert waitForProcess() == 0
        verifyHeader()
        assert bpmnFile.exists()
        assert testFile.exists()
        
        bpmnFile.delete()
        testFile.delete()
        
    }
    
}
