# =====================================================================
# ejecutar_pruebas_rendimiento.ps1 - Ejecutor de Pruebas de Carga
# Proyecto Integrador DevOps + IaC · UTNG
# =====================================================================
param(
    [string]$BackendUrl = "http://localhost:8081",
    [string]$FrontendUrl = "http://localhost:8080",
    [int]$TotalRequests = 100
)

Write-Host "`n=======================================================" -ForegroundColor Cyan
Write-Host " INICIANDO PRUEBAS DE CARGA Y EVALUACION DE RENDIMIENTO" -ForegroundColor Cyan
Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host " Backend Target: $BackendUrl"
Write-Host " Frontend Target: $FrontendUrl"
Write-Host " Peticiones por endpoint: $TotalRequests`n"

$endpoints = @(
    @{ Name = "Backend Healthcheck (/actuator/health)"; Url = "$BackendUrl/actuator/health" },
    @{ Name = "Backend Metrics (/actuator/prometheus)"; Url = "$BackendUrl/actuator/prometheus" },
    @{ Name = "API Usuarios (/usuario/all)"; Url = "$BackendUrl/usuario/all" },
    @{ Name = "Frontend Home (/)"; Url = "$FrontendUrl/" }
)

$reportLines = @()
$reportLines += "# Reporte de Evaluación de Rendimiento y Carga"
$reportLines += "Fecha de ejecución: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')`n"
$reportLines += "| Endpoint | Peticiones | Errores | Tasa Error | Latencia Min (ms) | Latencia Media (ms) | Latencia p95 (ms) | Latencia Max (ms) | Throughput (req/s) |"
$reportLines += "| :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: |"

foreach ($ep in $endpoints) {
    Write-Host "Ejecutando prueba sobre: $($ep.Name)..." -ForegroundColor Yellow
    $times = @()
    $errors = 0
    $swTotal = [System.Diagnostics.Stopwatch]::StartNew()

    for ($i = 1; $i -le $TotalRequests; $i++) {
        $sw = [System.Diagnostics.Stopwatch]::StartNew()
        try {
            $resp = Invoke-WebRequest -Uri $ep.Url -UseBasicParsing -TimeoutSec 5 -ErrorAction Stop
            $sw.Stop()
            if ($resp.StatusCode -ge 200 -and $resp.StatusCode -lt 400) {
                $times += $sw.ElapsedMilliseconds
            } else {
                $errors++
            }
        } catch {
            $sw.Stop()
            $errors++
        }
    }
    $swTotal.Stop()

    $totalSeconds = [Math]::Max(0.001, $swTotal.Elapsed.TotalSeconds)
    $throughput = [Math]::Round(($TotalRequests / $totalSeconds), 2)
    $errorRate = [Math]::Round((($errors / $TotalRequests) * 100), 2)

    if ($times.Count -gt 0) {
        $sorted = $times | Sort-Object
        $min = ($sorted | Measure-Object -Minimum).Minimum
        $avg = [Math]::Round(($sorted | Measure-Object -Average).Average, 2)
        $max = ($sorted | Measure-Object -Maximum).Maximum
        $p95Index = [Math]::Floor($sorted.Count * 0.95)
        $p95 = $sorted[$p95Index]
    } else {
        $min = 0; $avg = 0; $p95 = 0; $max = 0
    }

    Write-Host "   -> Completado: Min: ${min}ms | Avg: ${avg}ms | p95: ${p95}ms | Max: ${max}ms | Throughput: ${throughput} req/s | Errores: $errors ($errorRate%)" -ForegroundColor Green
    $reportLines += "| $($ep.Name) | $TotalRequests | $errors | $errorRate% | $min | $avg | $p95 | $max | $throughput |"
}

$outputDir = "C:\admin-proyectos\docs\ProyectoIntegrador"
if (-not (Test-Path $outputDir)) { New-Item -ItemType Directory -Path $outputDir -Force | Out-Null }
$outputPath = Join-Path $outputDir "Reporte_Rendimiento_Generado.md"
$reportLines | Out-File -FilePath $outputPath -Encoding utf8

Write-Host "`nReporte guardado en: $outputPath" -ForegroundColor Cyan
Write-Host "=======================================================`n" -ForegroundColor Cyan
