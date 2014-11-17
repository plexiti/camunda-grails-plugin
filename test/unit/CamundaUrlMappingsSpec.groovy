import grails.plugin.camunda.CamundaController
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.web.UrlMappingsUnitTestMixin
import grails.util.Mixin
import spock.lang.Specification

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
@TestFor(CamundaUrlMappings)
@Mock([CamundaController])
@Mixin(UrlMappingsUnitTestMixin)
class CamundaUrlMappingsSpec extends Specification {

  void "Test mapping for show()"() {
    expect:
      assertForwardUrlMapping("/camunda/test/simple1", controller: 'camunda', action: 'show') {
          folder = 'test'
          gsp = 'simple1'
      }
  }

  void "Test mapping for index()"() {
    expect:
      assertForwardUrlMapping("/camunda/index", controller: 'camunda', action: 'index')
  }

}
