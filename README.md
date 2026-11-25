# Gestor de Proyectos (Admin Proyectos) · Circuito DevOps con IaC y Cloud

[![Java 21](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7%20%2F%203.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![CI/CD Cloud](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-blue.svg)](https://github.com/JaquelineCamarillo/admin-proyectos/actions)
[![JaCoCo Coverage](https://img.shields.io/badge/JaCoCo%20Coverage-100%25-success.svg)](https://www.jacoco.org/)
[![IaC](https://img.shields.io/badge/IaC-Terraform%20%7C%20Ansible%20%7C%20Render-623ce4.svg)](https://www.terraform.io/)
[![Observability](https://img.shields.io/badge/Observability-Prometheus%20%26%20Grafana-F46800.svg)](https://grafana.com/)

Sistema empresarial de gestión de proyectos, usuarios, asignaciones, tareas y comentarios desarrollado bajo una arquitectura desacoplada de microservicios con **Java 21**, **Spring Boot**, base de datos relacional **MariaDB 11.4**, aprovisionamiento automatizado con **Infraestructura como Código (IaC)**, observabilidad con **Prometheus y Grafana**, y evaluación continua de rendimiento.

---

## 🚀 Arquitectura del Circuito DevOps

```mermaid
flowchart LR
    Dev[Desarrollador] -->|git push| Repo[(GitHub)]
    Repo -->|Trigger| GHA[GitHub Actions CI/CD]
    
    subgraph CI_CD["CI/CD Pipeline"]
        GHA --> Test[42 Tests Unitarios]
        Test --> Cov[JaCoCo 100% Check]
        Cov --> SAST[Semgrep + Trivy SAST]
        SAST --> IaC_Val[Terraform Validate]
        IaC_Val --> DockerBuild[Build Docker Multi-stage]
    end

    subgraph Deploy["Despliegue & IaC"]
        DockerBuild --> TF[Terraform / Ansible / Render]
        TF --> DB[(MariaDB 11.4)]
        TF --> Backend[Backend REST :8081]
        TF --> Frontend[Frontend Web :8080]
    end

    subgraph Monitoring["Observabilidad & Carga"]
        Backend & Frontend --> Actuator[/actuator/prometheus]
        Actuator --> Prom[Prometheus :9095]
        Prom --> Grafana[Grafana Dashboard :3000]
        k6[k6 / JMeter Carga] -.->|Latencia p95 < 50ms| Backend
    end
```

---

## 📦 Componentes del Sistema

| Microservicio / Módulo | Descripción Técnica | Puerto | Endpoints Clave |
| :--- | :--- | :---: | :--- |
| **`Ejercicio5MyAPIGestor`** | Backend REST (Spring Boot 2.7.18 + Java 21) | `8081` | `/swagger-ui.html`<br>`/actuator/health`<br>`/actuator/prometheus`<br>`/usuario/all` |
| **`Ejercicio5Cliente`** | Frontend Web (Spring Boot 3.2.5 + Java 21 + Thymeleaf) | `8080` | `/`<br>`/actuator/health`<br>`/actuator/prometheus` |
| **`MariaDB 11.4`** | Base de datos relacional (6 tablas gestionadas) | `3306` | Base `dbgestor` |
| **`Prometheus`** | Motor de scraping y almacenamiento de series temporales | `9095` | `http://localhost:9095` |
| **`Grafana`** | Tablero visual de métricas de CPU, JVM, memoria y peticiones | `3000` | `http://localhost:3000` *(admin/admin)* |

---

## 🛠️ Modos de Ejecución

### Opción 1: Todo en Uno con Docker Compose
Para compilar y levantar los 5 servicios interconectados en una red privada virtual:
```powershell
docker compose up -d --build
```

### Opción 2: Script Asistido de Demostración para Evaluación
Diseñado para la presentación presencial con el profesor:
```powershell
powershell -ExecutionPolicy Bypass -File .\demostracion_completa.ps1
```

### Opción 3: Despliegue con Infraestructura como Código (Terraform)
```powershell
cd iac/terraform
terraform init
terraform plan
terraform apply -auto-approve
```

### Opción 4: Aprovisionamiento con Ansible
```powershell
cd iac/ansible
ansible-playbook -i hosts.ini playbook.yml
```

---

## 🧪 Pruebas Unitarias y Cobertura (42 Tests · 100% JaCoCo)

Ejecución de pruebas aisladas con base de datos en memoria **H2** y verificación de cobertura:
```powershell
# Backend (25 pruebas unitarias)
cd Ejercicio5MyAPIGestor
.\mvnw test jacoco:report jacoco:check

# Frontend (17 pruebas unitarias)
cd ..\Ejercicio5Cliente
.\mvnw test jacoco:report jacoco:check
```

---

## 📊 Evaluación de Rendimiento y Carga Controlada

Ejecutar la suite de pruebas de rendimiento (100 peticiones concurrentes, medición de percentiles p95, throughput y tasa de error):
```powershell
cd tests/performance
powershell -ExecutionPolicy Bypass -File .\ejecutar_pruebas_rendimiento.ps1
```

---

## 📚 Documentación Técnica Completa

- [Informe Técnico del Proyecto Integrador](docs/ProyectoIntegrador/Informe_Tecnico_Proyecto_Integrador.md)
- [Guion de Defensa Oral y Banco de Preguntas](docs/ProyectoIntegrador/Guion_Defensa_Oral.md)
- [Reporte de Rendimiento y Métricas](docs/ProyectoIntegrador/Reporte_Rendimiento_Generado.md)
- [Reporte Práctica 3 (Antecedente)](docs/Practica3/P3_Reporte_Completo_AdminProyectos.pdf)
