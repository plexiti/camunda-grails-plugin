package grails.plugin.camunda

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class CamundaBpmApi {

  private static markers = [
      "7.0": "org.camunda.bpm.engine.ProcessEngine",
      "7.1": "org.camunda.bpm.engine.management.JobDefinitionQuery",
      "7.2": "org.camunda.bpm.engine.CaseService",
      "7.3": "org.camunda.bpm.engine.runtime.ProcessInstanceModificationBuilder",
      "7.4": "org.camunda.bpm.dmn.engine.DmnEngine",
  ]

  /**
   * Answers, if used Camunda BPM engine supports the requested API version.
   *
   * @param   api Camunda BPM API version e.g. '7.1', '7.2' etc.
   * @return  true, if process engine supports the requested API version.          
   */
  static boolean supports(String api) {
    def apis = markers.keySet().sort()
    assert apis.contains(api) : "Unknown API version $api requested, currently just $apis are supported"
    try {
      Class.forName(markers.get(api))
    } catch (ClassNotFoundException e) {
      return false
    }
    return true
  }

}
