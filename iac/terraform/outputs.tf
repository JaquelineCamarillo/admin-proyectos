# =====================================================================
# outputs.tf - Salidas del aprovisionamiento de infraestructura IaC
# =====================================================================

output "frontend_url" {
  description = "Punto de acceso web del Gestor de Proyectos"
  value       = "http://localhost:${var.frontend_port}"
}

output "backend_api_url" {
  description = "Punto de acceso API REST de microservicios"
  value       = "http://localhost:${var.backend_port}"
}

output "backend_swagger_url" {
  description = "Documentación interactiva Swagger UI"
  value       = "http://localhost:${var.backend_port}/swagger-ui.html"
}

output "grafana_dashboard_url" {
  description = "Tablero de observabilidad Grafana"
  value       = "http://localhost:${var.grafana_port}"
}

output "prometheus_metrics_url" {
  description = "Servidor de scraping de métricas Prometheus"
  value       = "http://localhost:${var.prometheus_port}"
}

output "database_endpoint" {
  description = "Endpoint de conexión MariaDB"
  value       = "localhost:3306"
}
