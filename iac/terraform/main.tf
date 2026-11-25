# =====================================================================
# main.tf - Aprovisionamiento automatizado de infraestructura DevOps
# Proyecto Integrador: Circuito DevOps con IaC · GIDS6102-E
# =====================================================================

# 1. Red virtual aislada para los microservicios y observabilidad
resource "docker_network" "devops_network" {
  name   = "admin_proyectos_network_${var.environment}"
  driver = "bridge"
}

# 2. Volumen persistente para datos de MariaDB
resource "docker_volume" "mariadb_data" {
  name = "admin_proyectos_mariadb_data_${var.environment}"
}

# 3. Contenedor de Base de Datos MariaDB 11 con Dump inicial
resource "docker_container" "database" {
  name  = "mariadb-${var.environment}"
  image = "mariadb:11.4"

  env = [
    "MYSQL_ROOT_PASSWORD=${var.db_password}",
    "MYSQL_DATABASE=${var.db_name}"
  ]

  ports {
    internal = 3306
    external = 3306
  }

  volumes {
    volume_name    = docker_volume.mariadb_data.name
    container_path = "/var/lib/mysql"
  }

  networks_advanced {
    name = docker_network.devops_network.name
  }

  restart = "always"
}

# 4. Microservicio Backend REST (Spring Boot 2.7 + Java 21)
resource "docker_container" "backend" {
  name  = "backend-${var.environment}"
  image = "admin-proyectos-backend:latest"

  env = [
    "PORT=${var.backend_port}",
    "SPRING_DATASOURCE_URL=jdbc:mariadb://${docker_container.database.name}:3306/${var.db_name}",
    "DB_USER=root",
    "DB_PASSWORD=${var.db_password}"
  ]

  ports {
    internal = var.backend_port
    external = var.backend_port
  }

  networks_advanced {
    name = docker_network.devops_network.name
  }

  depends_on = [docker_container.database]
  restart    = "always"
}

# 5. Microservicio Frontend Web (Spring Boot 3.2 + Java 21)
resource "docker_container" "frontend" {
  name  = "frontend-${var.environment}"
  image = "admin-proyectos-frontend:latest"

  env = [
    "PORT=${var.frontend_port}",
    "API_URL=http://${docker_container.backend.name}:${var.backend_port}"
  ]

  ports {
    internal = var.frontend_port
    external = var.frontend_port
  }

  networks_advanced {
    name = docker_network.devops_network.name
  }

  depends_on = [docker_container.backend]
  restart    = "always"
}

# 6. Monitoreo Prometheus
resource "docker_container" "prometheus" {
  name  = "prometheus-${var.environment}"
  image = "prom/prometheus:v2.51.0"

  ports {
    internal = 9090
    external = var.prometheus_port
  }

  networks_advanced {
    name = docker_network.devops_network.name
  }

  restart = "always"
}

# 7. Tablero Grafana
resource "docker_container" "grafana" {
  name  = "grafana-${var.environment}"
  image = "grafana/grafana:10.4.1"

  env = [
    "GF_SECURITY_ADMIN_USER=admin",
    "GF_SECURITY_ADMIN_PASSWORD=admin"
  ]

  ports {
    internal = 3000
    external = var.grafana_port
  }

  networks_advanced {
    name = docker_network.devops_network.name
  }

  depends_on = [docker_container.prometheus]
  restart    = "always"
}
