# GUION DE DEFENSA ORAL Y PRESENTACIÓN DEL PROYECTO INTEGRADOR DEVOPS
## Circuito DevOps con IaC, Microservicios Cloud y Observabilidad
**Estudiante:** Juana Jaqueline Camarillo Olaez  
**Asignatura:** Gestión del Proceso de Desarrollo de Software · Unidad III  
**Profesor:** Enrique Ramírez Ortiz  
**Grupo:** GIDS6102-E · UTNG  
**Duración Estimada:** 8 a 10 minutos  

---

## 1. Estructura General de la Presentación Oral

| Bloque | Tema | Duración | Qué mostrar en pantalla |
| :---: | :--- | :---: | :--- |
| **1** | Saludo, Introducción y Arquitectura General | 1:00 min | Repositorio GitHub y Diagrama de Arquitectura Mermaid |
| **2** | Justificación Técnica: El dilema Jenkins/SonarQube vs Render | 1:30 min | Diapositiva / Sección 1.3 del Informe Técnico |
| **3** | Pipelines CI/CD: Cloud (GitHub Actions) y Local (Jenkins) | 2:00 min | Pestaña Actions en GitHub y Jenkins Dashboard |
| **4** | Infraestructura como Código (IaC): Terraform, Ansible, Render | 1:30 min | Archivos `main.tf`, `playbook.yml`, `render.yaml` en VS Code |
| **5** | Despliegue, Microservicios e Integración | 1:30 min | Frontend Web (8080) y Swagger UI Backend (8081) |
| **6** | Monitoreo y Observabilidad: Prometheus + Grafana | 1:30 min | Tablero en vivo de Grafana (`http://localhost:3000`) |
| **7** | Evaluación del Rendimiento (k6 / JMeter / PowerShell) | 1:00 min | Resultados de carga: latencia p95 y throughput |
| **8** | Conclusión y Sesión de Preguntas | 1:00 min | Informe Técnico y Bitácora de Incidencias |

---

## 2. Guion Detallado Paso a Paso (Lo que debes decir y hacer)

### Minuto 0:00 – 1:00 · Saludo y Presentación de la Arquitectura
**En pantalla:** Abrir el repositorio GitHub `https://github.com/JaquelineCamarillo/admin-proyectos` y mostrar el diagrama de arquitectura en el informe.

> *"Buenos días profesor Enrique Ramírez. Mi nombre es Juana Jaqueline Camarillo Olaez, alumna del grupo GIDS6102-E. Hoy presento la defensa final de mi **Proyecto Integrador: Circuito DevOps con Infraestructura como Código y Despliegue Cloud** sobre el sistema empresarial Gestor de Proyectos (*admin-proyectos*).*
>
> *El sistema está concebido bajo una arquitectura desacoplada de microservicios: un Backend REST en Spring Boot 2.7 actualizado a Java 21, un Frontend Web en Spring Boot 3.2 con Thymeleaf también en Java 21, y una base de datos relacional MariaDB 11.4 con 6 tablas relacionadas.*
>
> *Para cumplir al 100% con los criterios de la rúbrica de la Unidad III, implementé un circuito DevOps completo que abarca control de versiones, pipelines de CI/CD automatizados, análisis de calidad y seguridad SAST, aprovisionamiento automatizado mediante Infraestructura como Código con Terraform, Ansible y Render Blueprints, monitoreo en tiempo real con Prometheus y Grafana, y pruebas de rendimiento y estrés controladas."*

---

### Minuto 1:00 – 2:30 · Justificación Técnica: ¿Por qué Jenkins y SonarQube NO se montan en Render Free y cuál es la solución óptima?
**En pantalla:** Mostrar la Sección 1.3 del `Informe_Tecnico_Proyecto_Integrador.md`.

> *"Profesor, un punto crucial de ingeniería que evalué detalladamente fue el requerimiento de desplegar Jenkins y SonarQube en la nube de Render.*
>
> *Hice un análisis riguroso de viabilidad técnica: el nivel gratuito de Render asigna un límite estricto de **512 MB de memoria RAM** por servicio y no ofrece discos persistentes.
> - **SonarQube:** Requiere dos procesos pesados: el servidor Java y un motor de indexación **Elasticsearch**. Oficialmente exige un mínimo de **2 GB a 3 GB de RAM**. Al intentar correrlo en 512 MB, Elasticsearch arroja de inmediato un error crítico de `OutOfMemoryError: Java heap space` y el OOM Killer de Linux finaliza el proceso con el código de salida 137. Además, sin almacenamiento persistente, cada reinicio borraría los proyectos y Quality Gates.
> - **Jenkins:** Su JVM más los plugins y la compilación de Maven con Java 21 consumen entre **1.5 GB y 2 GB de RAM**. En 512 MB el contenedor colapsa.*
>
> *Por ello, basándome en la **Sección 5 de la rúbrica oficial**, que establece que se exigen 'capacidades funcionales y no marcas específicas' y que se aceptan 'herramientas equivalentes justificadas', implementé la solución estándar de la industria:*
> 1. *Para CI/CD en la nube utilicé **GitHub Actions**, que nos brinda runners de Ubuntu con **16 GB de RAM** (32 veces más que Render), 4 vCPUs y ejecución en la nube completamente gratuita.*
> 2. *Para calidad y SAST en la nube integré **SonarCloud** (la plataforma SaaS oficial de SonarQube creada por la misma empresa SonarSource) y **Semgrep SAST** junto con **Aqua Trivy** en el pipeline.*
> 3. *Y para demostrar el dominio total de la tecnología, **mantengo y demuestro también los pipelines locales en Jenkins con SonarQube en Docker**. De esta forma cubro tanto el escenario On-Premise como el escenario Cloud."*

---

### Minuto 2:30 – 4:30 · Demostración de Pipelines CI/CD y Calidad
**En pantalla:** Abrir la pestaña **Actions** en GitHub (`https://github.com/JaquelineCamarillo/admin-proyectos/actions`) y luego mostrar Jenkins local (`http://localhost:9090`).

> *"Aquí en pantalla podemos observar el flujo de CI/CD en **GitHub Actions**:*
> - *En cada `push` o `pull request`, el workflow `ci-cd-cloud.yml` levanta un contenedor con JDK 21 y Maven 3.9.*
> - *Ejecuta de forma automatizada las **42 pruebas unitarias** del sistema (25 en backend con H2 en memoria y 17 en frontend con MockMvc y MockRestServiceServer).*
> - *Aplica la meta `jacoco:check` que exige el **100 % de cobertura de código** sobre clases evaluables; si la cobertura baja de ese umbral, el pipeline rompe la compilación.*
> - *A continuación ejecuta el escaneo SAST con **Semgrep** auditando reglas OWASP Top 10 y el escaneo de dependencias y secretos con **Aqua Trivy**.*
> - *Valida la sintaxis de Infraestructura como Código con `terraform validate` y construye las imágenes Docker multi-stage optimizadas con `eclipse-temurin:21-jre-alpine`.*
>
> *Asimismo, si revisamos nuestro Jenkins local en el puerto 9090, los tres pipelines (`pipeline-backend`, `pipeline-frontend` y `pipeline-database`) están totalmente operativos, conectados al servidor SonarQube local donde ambos proyectos tienen el Quality Gate en estado PASSED."*

---

### Minuto 4:30 – 6:00 · Infraestructura como Código (IaC)
**En pantalla:** Mostrar en VS Code la carpeta `iac/terraform/`, `iac/ansible/` y el archivo `render.yaml`.

> *"Para el criterio de Infraestructura como Código (12% de la rúbrica), implementé tres enfoques complementarios y reproducibles:*
> 1. ***Terraform (`iac/terraform/`):** Diseñé archivos HCL modulares (`main.tf`, `variables.tf`, `providers.tf`, `outputs.tf`) que declaran de forma declarativa los recursos: la red privada virtual tipo bridge, el volumen persistente de MariaDB, los contenedores del Backend, Frontend, Prometheus y Grafana, con inyección segura de variables de entorno mediante `${DB_PASSWORD}`.*
> 2. ***Ansible (`iac/ansible/playbook.yml`):** Diseñé un playbook que automatiza la verificación del motor de Docker, construye las imágenes, orquesta el encendido de los contenedores y ejecuta un sondeo activo (*polling*) contra el endpoint `/actuator/health` de ambos microservicios hasta comprobar que devuelven código HTTP 200.*
> 3. ***Render Blueprint (`render.yaml`):** Especificación de IaC declarativa nativa para Render que provisiona con un solo commit tanto los dos servicios web como la base de datos gestionada."*

---

### Minuto 6:00 – 7:30 · Servicios en Ejecución y Observabilidad (Prometheus + Grafana)
**En pantalla:** Mostrar el Frontend en `http://localhost:8080`, Swagger UI en `http://localhost:8081/swagger-ui.html`, y luego el Tablero de Grafana en `http://localhost:3000`.

> *"En este momento ambos microservicios se encuentran interactuando correctamente: el Frontend en el puerto 8080 renderiza los proyectos y tareas consumiendo la API REST del backend en el puerto 8081.*
>
> *Para cumplir con el criterio de **Monitoreo y Observabilidad (7%)**, instrumenté ambos microservicios con **Spring Boot Actuator** y **Micrometer Prometheus Registry**.*
>
> *Al ingresar al tablero de **Grafana en el puerto 3000**, aprovisionado automáticamente desde el archivo de configuración:*
> - *Podemos visualizar en tiempo real la disponibilidad de los microservicios (Backend y Frontend en estado UP).*
> - *El porcentaje de uso de CPU del proceso y del sistema.*
> - *El consumo de memoria Heap de la JVM frente a la memoria máxima asignada.*
> - *Las conexiones activas del pool HikariCP contra MariaDB.*
> - *Y el throughput de peticiones HTTP discriminado por códigos de estado 200, 400 y 500."*

---

### Minuto 7:30 – 8:30 · Pruebas de Rendimiento y Carga (k6 / JMeter)
**En pantalla:** Mostrar `docs/ProyectoIntegrador/Reporte_Rendimiento_Generado.md` o ejecutar en terminal `tests/performance/ejecutar_pruebas_rendimiento.ps1`.

> *"En cuanto a la **Evaluación del Rendimiento (7%)**, creamos un script de pruebas de carga con **k6** (`load-test.js`) y un plan de prueba en **Apache JMeter** (`jmeter-plan.jmx`).*
>
> *Ejecutamos una prueba de carga controlada de 100 peticiones concurrentes sobre los endpoints neurálgicos:*
> - *La tasa de fallos fue de **0.00 %**.*
> - *La latencia media en el healthcheck fue de **12 milisegundos**, y en las consultas a la base de datos fue de **22 milisegundos**.*
> - *El percentil 95 (**latencia p95**) se mantuvo en **35 milisegundos**, muy por debajo de los 500 milisegundos fijados como umbral de tolerancia.*
> - *El rendimiento alcanzó un throughput de más de **80 peticiones por segundo** sin experimentar fugas de memoria en la JVM ni bloqueos en el pool de conexiones."*

---

### Minuto 8:30 – 10:00 · Conclusión y Cierre
**En pantalla:** Mostrar el informe técnico y el historial de commits en GitHub.

> *"Para concluir, este proyecto me permitió no solo configurar herramientas de forma aislada, sino comprender la verdadera filosofía DevOps: la automatización integral de la entrega de software con calidad y seguridad comprobables mediante aserciones, la reproducibilidad total de la infraestructura con código versionado y el control operativo mediante observabilidad proactiva. Todo el código, configuraciones, reportes y bitácora de incidencias se encuentran versionados en mi repositorio público de GitHub.*
>
> *Quedo a su disposición para cualquier pregunta o prueba que desee realizar en vivo. Muchas gracias."*

---

## 3. Banco de Preguntas Difíciles del Profesor y Cómo Responderlas

### Pregunta 1: *"¿Por qué no insististe en meter Jenkins y SonarQube dentro de Render?"*
* **Respuesta Modelo:**  
  *"Profesor, como ingenieros de software debemos tomar decisiones basadas en arquitectura y viabilidad técnica, no en forzar herramientas en entornos inadecuados. Render Free ofrece únicamente 512 MB de RAM compartida y carece de almacenamiento persistente. SonarQube incluye un motor Elasticsearch que por sí solo requiere mínimo 1 a 2 GB de memoria dedicada; en 512 MB el sistema operativo mata el proceso instantáneamente por OOM Killer (código 137). Jenkins, al compilar con Maven y Java 21, supera los 1.5 GB de RAM y además necesita persistencia en disco para no perder el historial de builds. Conforme a la sección 5 de la rúbrica de evaluación, la solución estándar y óptima en la nube es utilizar **GitHub Actions**, que nos proporciona runners con 16 GB de RAM de forma gratuita, y **SonarCloud / Semgrep** para SAST en la nube, manteniendo además los pipelines de Jenkins documentados y funcionales en el entorno local."*

### Pregunta 2: *"¿Cuál es la diferencia técnica entre Terraform y Ansible en tu proyecto?"*
* **Respuesta Modelo:**  
  *"Tienen propósitos distintos y complementarios. **Terraform** es una herramienta de aprovisionamiento de infraestructura declarativa e inmutable: define 'qué' recursos deben existir (las redes virtuales, los volúmenes de almacenamiento, los puertos y los contenedores). **Ansible**, por su parte, es una herramienta de gestión de configuración y orquestación procedimental: se encarga de ejecutar tareas sobre los recursos ya creados, como verificar el estado de Docker, compilar imágenes, desplegar los contenedores en orden de dependencia y ejecutar pruebas de sondeo de salud (*healthcheck polling*) sobre los endpoints `/actuator/health` hasta confirmar disponibilidad HTTP 200."*

### Pregunta 3: *"¿Tener 100% de cobertura en JaCoCo significa que el software no tiene errores?"*
* **Respuesta Modelo:**  
  *"No, profesor. Un 100% de cobertura indica únicamente que el flujo de ejecución de las pruebas pasó por todas las líneas, instrucciones y ramas del código fuente, pero no garantiza la calidad de las aserciones. Por eso, en nuestras 42 pruebas automatizadas no solo nos enfocamos en cubrir código, sino en validar aserciones estrictas sobre los estados HTTP (200, 400, 404, 409), el contenido JSON de las respuestas, el manejo global de excepciones con `GlobalExceptionHandler` y los atributos del modelo en Thymeleaf, detectando incluso un error real en los métodos `/tarea` y `/saveComentario` donde faltaba inyectar un atributo al modelo."*

### Pregunta 4: *"¿Cómo se comunican el frontend y el backend dentro de Docker si no usas `localhost`?"*
* **Respuesta Modelo:**  
  *"En Docker Compose y Terraform creamos una red interna tipo *bridge* llamada `devops-network`. Docker cuenta con un servidor DNS interno embebido que resuelve el nombre del servicio al IP privado del contenedor. Por eso, configuramos la propiedad `api.url` del frontend mediante la variable de entorno `${API_URL}`, que en Docker apunta a `http://backend:8081` en lugar de `localhost`. De igual forma, el backend conecta a la base de datos usando `jdbc:mariadb://mariadb:3306/dbgestor`."*

### Pregunta 5: *"¿Qué métricas específicas expone Actuator para Prometheus y cómo las usas en Grafana?"*
* **Respuesta Modelo:**  
  *"Actuator expone el endpoint `/actuator/prometheus` formateando métricas en el estándar OpenMetrics. Entre las métricas principales monitoreadas están: `jvm_memory_used_bytes` y `jvm_memory_max_bytes` para controlar el uso de memoria Heap; `system_cpu_usage` y `process_cpu_usage` para detectar picos de cómputo; `hikaricp_connections_active` para vigilar la saturación del pool de conexiones a MariaDB; y `http_server_requests_seconds_count` agrupado por códigos de estado para calcular la tasa de peticiones por segundo y detectar errores HTTP 4xx y 5xx en tiempo real."*
