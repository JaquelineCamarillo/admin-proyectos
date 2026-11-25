# =====================================================================
# enviar_a_sonarqube.ps1 - Ejecuta y Envia el Analisis a SonarQube Local
# =====================================================================
$ErrorActionPreference = "Continue"
Write-Host "`n=======================================================" -ForegroundColor Cyan
Write-Host " ENVIANDO ANALISIS DE CODIGO A SONARQUBE (localhost:9000)" -ForegroundColor Cyan
Write-Host "=======================================================`n" -ForegroundColor Cyan

# 1. Backend
Write-Host ">>> [1/2] Analizando Backend (Ejercicio5MyAPIGestor)..." -ForegroundColor Yellow
Set-Location "C:\admin-proyectos\Ejercicio5MyAPIGestor"
.\mvnw.cmd sonar:sonar -Dsonar.host.url="http://localhost:9000" -Dsonar.login="admin" -Dsonar.password="Admin123!"

# 2. Frontend
Write-Host "`n>>> [2/2] Analizando Frontend (Ejercicio5Cliente)..." -ForegroundColor Yellow
Set-Location "C:\admin-proyectos\Ejercicio5Cliente"
.\mvnw.cmd sonar:sonar -Dsonar.host.url="http://localhost:9000" -Dsonar.login="admin" -Dsonar.password="Admin123!"

Write-Host "`n[OK] Analisis enviado a SonarQube. Abriendo navegador..." -ForegroundColor Green
Start-Process "http://localhost:9000/projects"
