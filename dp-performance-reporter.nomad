job "dp-performance-reporter" {
  datacenters = ["eu-west-1"]
  region      = "eu"
  type        = "service"

  update {
    stagger      = "90s"
    max_parallel = 1
  }

  group "publishing" {
    count = "{{PUBLISHING_TASK_COUNT}}"

    constraint {
      attribute = "${node.class}"
      value     = "publishing"
    }

    task "dp-performance-reporter" {
      driver = "docker"

      artifact {
        source = "s3::https://s3-eu-west-1.amazonaws.com/{{DEPLOYMENT_BUCKET}}/dp-performance-reporter/{{REVISION}}.tar.gz"
      }

      config {
        command = "${NOMAD_TASK_DIR}/start-task"

        args = [
          "java",
          "-cp target/dependency/*:target/classes/",
          "com.onsdigital.performance.reporter.Main",
        ]

        image = "{{ECR_URL}}:concourse-{{REVISION}}"
      }

      service {
        name = "dp-performance-reporter"
        tags = ["publishing"]
      }

      resources {
        cpu    = "{{PUBLISHING_RESOURCE_CPU}}"
        memory = "{{PUBLISHING_RESOURCE_MEM}}"
      }

      template {
        source      = "${NOMAD_TASK_DIR}/vars-template"
        destination = "${NOMAD_TASK_DIR}/vars"
      }

      vault {
        policies = ["dp-performance-reporter"]
      }
    }
  }
}
