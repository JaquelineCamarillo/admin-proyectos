# =====================================================================
#  levantar_todo.ps1 - Arranca todo el proyecto Admin Proyectos
#  Uso:  cd C:\admin-proyectos
#        powershell -ExecutionPolicy Bypass -File .\levantar_todo.ps1
# =====================================================================
$ErrorActionPreference = "Continue"
$raiz = "C:\admin-proyectos"

function Esperar-Puerto($puerto, $nombre, $segundos = 180) {
    Write-Host "   Esperando a $nombre en el puerto $puerto..." -NoNewline
    $limite = (Get-Date).AddSeconds($segundos)
    while ((Get-Date) -lt $limite) {
        if (Test-NetConnection localhost -Port $puerto -InformationLevel Quiet -WarningAction SilentlyContinue) {
            Write-Host " LISTO" -ForegroundColor Green; return $true
        }
        Write-Host "." -NoNewline; Start-Sleep -Seconds 3
    }
    Write-Host " NO RESPONDIO" -ForegroundColor Red; return $false
}

Write-Host "`n=== 1. Docker Desktop ===" -ForegroundColor Cyan
docker info *> $null
if ($LASTEXITCODE -ne 0) {
    Write-Host "   Abriendo Docker Desktop..."
    Start-Process "C:\Program Files\Docker\Docker\Docker Desktop.exe"
    do { Start-Sleep -Seconds 5; docker info *> $null; Write-Host "." -NoNewline } while ($LASTEXITCODE -ne 0)
    Write-Host " LISTO" -ForegroundColor Green
} else { Write-Host "   Docker ya esta corriendo" -ForegroundColor Green }

Write-Host "`n=== 2. Contenedores (Jenkins, SonarQube, MariaDB CI) ===" -ForegroundColor Cyan
$existentes = docker ps -a --format "{{.Names}}"
foreach ($c in @("jenkins", "sonarqube", "mariadb-ci")) {
    if ($existentes -contains $c) { docker start $c | Out-Null; Write-Host "   $c iniciado" -ForegroundColor Green }
    else { Write-Host "   $c no existe (se omite)" -ForegroundColor Yellow }
}

Write-Host "`n=== 3. MariaDB local (base dbgestor) ===" -ForegroundColor Cyan
$svc = Get-Service | Where-Object { $_.Name -like "MariaDB*" -or $_.Name -like "MySQL*" } | Select-Object -First 1
if ($svc) {
    if ($svc.Status -ne "Running") {
        try { Start-Service $svc.Name -ErrorAction Stop; Write-Host "   Servicio $($svc.Name) iniciado" -ForegroundColor Green }
        catch { Write-Host "   No se pudo iniciar $($svc.Name): abre PowerShell como Administrador y ejecuta: Start-Service $($svc.Name)" -ForegroundColor Red }
    } else { Write-Host "   Servicio $($svc.Name) ya esta corriendo" -ForegroundColor Green }
} else { Write-Host "   No encontre el servicio de MariaDB. Abre HeidiSQL y verifica que conecte." -ForegroundColor Yellow }
Esperar-Puerto 3306 "MariaDB" 30 | Out-Null

Write-Host "`n=== 4. API Ejercicio5MyAPIGestor (8081) ===" -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "`$host.UI.RawUI.WindowTitle='API 8081'; cd '$raiz\Ejercicio5MyAPIGestor'; `$env:DB_PASSWORD='root'; .\mvnw spring-boot:run"
Esperar-Puerto 8081 "la API" | Out-Null

Write-Host "`n=== 5. Cliente Ejercicio5Cliente (8080) ===" -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "`$host.UI.RawUI.WindowTitle='Cliente 8080'; cd '$raiz\Ejercicio5Cliente'; .\mvnw spring-boot:run"
Esperar-Puerto 8080 "el Cliente" | Out-Null

Write-Host "`n=== 6. Esperando Jenkins (9090) y SonarQube (9000) ===" -ForegroundColor Cyan
Esperar-Puerto 9090 "Jenkins" | Out-Null
Esperar-Puerto 9000 "SonarQube" | Out-Null

Write-Host "`n=== 7. Abriendo el navegador ===" -ForegroundColor Cyan
$urls = @(
    "http://localhost:9090/",
    "http://localhost:9000/projects",
    "http://localhost:8081/swagger-ui.html",
    "http://localhost:8081/actuator/health",
    "http://localhost:8080/",
    "http://localhost:3000/",
    "http://localhost:9095/",
    "https://github.com/JaquelineCamarillo/admin-proyectos"
)
foreach ($u in $urls) { Start-Process $u; Start-Sleep -Milliseconds 700 }

Write-Host "`nTODO LISTO. Jenkins y SonarQube: usuario admin / Admin123!`n" -ForegroundColor Green
