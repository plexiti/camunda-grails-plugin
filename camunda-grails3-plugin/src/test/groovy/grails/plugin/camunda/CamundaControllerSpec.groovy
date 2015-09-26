package grails.plugin.camunda

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll
import spock.lang.Ignore

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
@TestFor(CamundaController)
class CamundaControllerSpec extends Specification {

  @Unroll
  void 'Test index() for scenario #scenario'() {
    given:
      Configuration.setConfigObjectProperty('camunda.deployment.scenario', scenario)
    when:
      controller.index()
    then:
      response.text.contains('camunda BPM')
    where:
      scenario << ['embedded']
  }

  void 'Test index() for scenario shared'() {
    given:
      Configuration.setConfigObjectProperty('camunda.deployment.scenario', 'shared')
    when:
      controller.index()
    then:
      response.redirectedUrl.contains('camunda-welcome')
  }

  @Unroll
  void 'Test show() for embedded form rendering'() {
    given:
      params.folder = folder
      params.gsp = gsp
    when:
      controller.show()
    then:
      view == "/$folder/$gsp"
    where:
      folder << ['myFolder', 'yourFolder']
      gsp << ['yourGsp', 'myGsp']
  }

}
