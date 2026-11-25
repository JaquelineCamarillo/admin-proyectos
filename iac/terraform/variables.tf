# =====================================================================
# variables.tf - Parámetros configurables de la infraestructura IaC
# =====================================================================

variable "environment" {
  description = "Ambiente de despliegue (dev, staging, prod)"
  type        = string
  default     = "prod"
}

variable "docker_host" {
  description = "Socket o pipe del daemon de Docker"
  type        = string
  default     = "npipe:////./pipe/docker_engine"
}

variable "db_password" {
  description = "Contraseña de la base de datos MariaDB (nunca hardcodear)"
  type        = string
  default     = "root"
  sensitive   = true
}

variable "db_name" {
  description = "Nombre de la base de datos relacional"
  type        = string
  default     = "dbgestor"
}

variable "backend_port" {
  description = "Puerto expuesto por el microservicio backend"
  type        = number
  default     = 8081
}

variable "frontend_port" {
  description = "Puerto expuesto por el microservicio frontend"
  type        = number
  default     = 8080
}

variable "grafana_port" {
  description = "Puerto expuesto para el tablero Grafana"
  type        = number
  default     = 3000
}

variable "prometheus_port" {
  description = "Puerto expuesto para métricas Prometheus"
  type        = number
  default     = 9095
}
