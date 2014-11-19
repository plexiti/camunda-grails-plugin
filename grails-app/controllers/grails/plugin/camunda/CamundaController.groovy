package grails.plugin.camunda

import static grails.plugin.camunda.Configuration.config

class CamundaController {
    
    def grailsApplication

    def index() {
        if (config('camunda.deployment.scenario') == 'shared') {
            redirect(url: "${request.requestURL.toString().split(request.contextPath)[0]}/camunda-welcome/index.html")
        } else {
            render("camunda BPM webapps (cockpit, tasklist,...) are only available in camunda.deployment.scenario == 'shared'.")
        }
    }

    def show() {
        def view = "/${params.folder}/${params.gsp}"
        // 'embedded' task list request
        render(view: view, layout: 'camunda.embedded')
    }

}
