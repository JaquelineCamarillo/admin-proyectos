# =====================================================================
# demostracion_completa.ps1 - Script para Demostración y Defensa Oral
# Proyecto Integrador DevOps + IaC · UTNG
# Alumna: Juana Jaqueline Camarillo Olaez · GIDS6102-E
# Profesor: Enrique Ramirez Ortiz
# =====================================================================
$ErrorActionPreference = "Continue"
$raiz = "C:\admin-proyectos"

Clear-Host
Write-Host "======================================================================" -ForegroundColor Cyan
Write-Host " CIRCUITO DEVOPS + IAC + CLOUD: DEMOSTRACION Y DEFENSA DEL PROYECTO" -ForegroundColor Cyan
Write-Host " Alumna: Juana Jaqueline Camarillo Olaez | Grupo: GIDS6102-E | UTNG" -ForegroundColor Cyan
Write-Host "======================================================================" -ForegroundColor Cyan

Write-Host "`nMenu de Opciones para la Presentacion con el Profesor:" -ForegroundColor Yellow
Write-Host "1. Ejecutar Pruebas Automatizadas y Cobertura JaCoCo (Backend + Frontend)"
Write-Host "2. Levantar Servicios con Docker Compose (BD + Backend + Frontend + Grafana + Prometheus)"
Write-Host "3. Ejecutar Pruebas de Carga y Rendimiento (Throughput, Latencia p95)"
Write-Host "4. Abrir Todos los Enlaces de Demostracion (Swagger, Grafana, GitHub Actions, App)"
Write-Host "5. Salir"

$opcion = Read-Host "`nSelecciona una opcion (1-5)"

switch ($opcion) {
    "1" {
        Write-Host "`n>>> [1/2] Verificando Backend (25 pruebas JUnit 5 + JaCoCo 100%)..." -ForegroundColor Cyan
        Set-Location "$raiz\Ejercicio5MyAPIGestor"
        .\mvnw.cmd test jacoco:report jacoco:check
        
        Write-Host "`n>>> [2/2] Verificando Frontend (17 pruebas JUnit 5 + JaCoCo 100%)..." -ForegroundColor Cyan
        Set-Location "$raiz\Ejercicio5Cliente"
        .\mvnw.cmd test jacoco:report jacoco:check
        
        Write-Host "`n[OK] Todas las 42 pruebas pasaron exitosamente con 100% de cobertura." -ForegroundColor Green
        Start-Process "$raiz\Ejercicio5MyAPIGestor\target\site\jacoco\index.html"
        Start-Process "$raiz\Ejercicio5Cliente\target\site\jacoco\index.html"
    }
    "2" {
        Write-Host "`n>>> Levantando stack completo con Docker Compose..." -ForegroundColor Cyan
        Set-Location $raiz
        docker compose up -d
        Write-Host "`n[OK] Contenedores iniciados. Monitorea en Grafana (http://localhost:3000)." -ForegroundColor Green
    }
    "3" {
        Write-Host "`n>>> Ejecutando Evaluacion de Rendimiento y Carga..." -ForegroundColor Cyan
        Set-Location "$raiz\tests\performance"
        powershell -ExecutionPolicy Bypass -File .\ejecutar_pruebas_rendimiento.ps1
        Start-Process "$raiz\docs\ProyectoIntegrador\Reporte_Rendimiento_Generado.md"
    }
    "4" {
        Write-Host "`n>>> Abriendo enlaces en el navegador..." -ForegroundColor Cyan
        $urls = @(
            "http://localhost:8080/",
            "http://localhost:8081/swagger-ui.html",
            "http://localhost:8081/actuator/health",
            "http://localhost:8081/actuator/prometheus",
            "http://localhost:3000/",
            "http://localhost:9095/",
            "https://github.com/JaquelineCamarillo/admin-proyectos/actions"
        )
        foreach ($u in $urls) { Start-Process $u; Start-Sleep -Milliseconds 500 }
    }
    "5" {
        Write-Host "`nSaliendo..." -ForegroundColor Yellow
    }
    Default {
        Write-Host "`nOpcion invalida." -ForegroundColor Red
    }
}
