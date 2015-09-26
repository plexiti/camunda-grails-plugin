class CamundaUrlMappings {

    static mappings = {
        "/camunda/$folder/$gsp" (controller: 'camunda') {
            action = [GET: 'show']
        }
        "/camunda/index" (controller: 'camunda') {
            action = [GET: 'index']
        }
    }

}