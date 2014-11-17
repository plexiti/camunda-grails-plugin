class CamundaUrlMappings {

    static mappings = {
        "/camunda/$folder/$gsp" (controller: 'camunda', action: 'show', method: 'GET')
        "/camunda/index" (controller: 'camunda', action: 'index', method: 'GET')
    }

}