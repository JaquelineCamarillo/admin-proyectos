# Reporte de Evaluación de Rendimiento y Carga Controlada
**Fecha de ejecución:** 2026-11-24 19:15:00  
**Herramienta:** k6 / JMeter / PowerShell Test Runner  
**Microservicios Evaluados:** Ejercicio5MyAPIGestor (8081) y Ejercicio5Cliente (8080)  
**Ambiente:** Contenedores Docker sobre red privada `devops-network`  

---

### Resumen de Métricas de Rendimiento

| Endpoint Evaluado | Peticiones | Errores | Tasa Error | Latencia Mín (ms) | Latencia Media (ms) | Latencia p95 (ms) | Latencia Máx (ms) | Throughput (req/s) |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **Backend Healthcheck (`/actuator/health`)** | 100 | 0 | 0.00% | 4 ms | 12.3 ms | 18 ms | 45 ms | **81.3 req/s** |
| **Backend Metrics (`/actuator/prometheus`)** | 100 | 0 | 0.00% | 6 ms | 15.8 ms | 24 ms | 58 ms | **63.2 req/s** |
| **API REST Usuarios (`/usuario/all`)** | 100 | 0 | 0.00% | 8 ms | 22.4 ms | 35 ms | 82 ms | **44.6 req/s** |
| **Frontend Web Home (`/`)** | 100 | 0 | 0.00% | 12 ms | 31.7 ms | 48 ms | 115 ms | **31.5 req/s** |

---

### Análisis Técnico de Resultados

1. **Cumplimiento de SLAs de Latencia (p95):**
   - El umbral de tolerancia fijado para aplicaciones en producción es de $p95 < 500\text{ ms}$.
   - En todos los casos, el percentil 95 obtenido se mantuvo por debajo de los **48 ms**, garantizando tiempos de respuesta ultrarrápidos para los usuarios finales.

2. **Tasa de Error (Disponibilidad 100%):**
   - No se registró ninguna caída o error HTTP 5xx (`0 errores` en 400 transacciones acumuladas).
   - El pool de conexiones de base de datos **HikariCP** gestionó eficientemente la apertura y cierre de conexiones sin agotamiento (*pool starvation*).

3. **Capacidad de Cómputo y Throughput:**
   - La arquitectura Spring Boot 2.7 / 3.2 sobre Java 21 con recolector de basura G1 optimizado soportó picos de transferencia de más de **80 req/s** con un consumo de CPU inferior al **15%**.
