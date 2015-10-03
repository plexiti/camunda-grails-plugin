environments {
  development {
    grails.logging.jul.usebridge = true
  }
  test {
    grails.logging.jul.usebridge = true
    camunda {
      beans {
        historyService = 'alternativeHistoryServiceName'
      }
    }
  }
  production {
    grails.logging.jul.usebridge = false
  }
  embedded {
    grails.logging.jul.usebridge = true
    camunda {
      engine {
        configuration {
          databaseSchemaUpdate = true
          jobExecutorActivate = true
          deploymentResources = ['classpath:/**/*.bpmn', 'classpath:/**/*.bpmn20.xml']
        }
      }
    }
  }
  shared {
    grails.logging.jul.usebridge = true
  }
}