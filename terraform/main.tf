terraform {
  required_providers {
    docker = {
      source  = "kreuzwerker/docker"
      version = "~> 3.0.2"
    }
  }
}


provider "docker" {}

# resource "docker_network" "my_network" {
#   name = "my_project_network"
# }

resource "docker_image" "backend" {
  name         = "my-backend:latest"
  build {
    context    = "${path.module}/../backend"
    dockerfile = "Dockerfile"
  }
}

resource "docker_container" "backend" {
  name  = "backend"
  image = docker_image.backend.image_id
  # networks_advanced {
  #   name = docker_network.my_network.name
  # }
  ports {
    internal = 8080
    external = 8080
  }
  env = [
    "DATABASE_URL=postgres://user:pass@db:5432/mydb"
  ]
}

resource "docker_image" "frontend" {
  name         = "my-frontend:latest"
  build {
    context    = "${path.module}/../frontend"
    dockerfile = "Dockerfile"
  }
}

resource "docker_container" "frontend" {
  name  = "frontend"
  image = docker_image.frontend.image_id
  # networks_advanced {
  ports {
    internal = 80
    external = 3000
  }
}
