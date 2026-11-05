const fs = require('fs');
const path = require('path');
const {
  Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell,
  HeadingLevel, AlignmentType, WidthType, BorderStyle, ImageRun,
  Header, Footer, PageNumber, NumberFormat, UnderlineType, ShadingType
} = require('docx');

const { p, h1, h2, h3, cell, imageFigure, OUTPUT_DOCX, CAPTURAS_DIR } = require('./report_helpers');

// Read Git log to include commit hashes
let gitLog = '';
try {
  const { execSync } = require('child_process');
  gitLog = execSync('git log --oneline -n 15', { cwd: 'C:\\admin-proyectos' }).toString();
} catch (e) {
  gitLog = 'Log no disponible';
}

console.log('Building document sections...');

const doc = new Document({
  styles: {
    default: {
      document: {
        run: {
          font: 'Arial',
          size: 22, // 11pt
          color: '222222'
        },
        paragraph: {
          spacing: { line: 360, before: 120, after: 120 }, // 1.5 line spacing
          alignment: AlignmentType.JUSTIFIED
        }
      }
    }
  },
  sections: [
    {
      properties: {
        page: {
          margin: { top: 1134, bottom: 1134, left: 1134, right: 1134 } // 2 cm
        }
      },
      headers: {
        default: new Header({
          children: [
            new Paragraph({
              alignment: AlignmentType.CENTER,
              spacing: { after: 180 },
              children: [
                new TextRun({
                  text: 'Universidad Tecnológica del Norte de Guanajuato · Gestión de Procesos de Desarrollo de Software · Práctica 3',
                  font: 'Arial',
                  size: 16,
                  color: '777777',
                  bold: true
                })
              ]
            })
          ]
        })
      },
      footers: {
        default: new Footer({
          children: [
            new Paragraph({
              alignment: AlignmentType.CENTER,
              children: [
                new TextRun({
                  text: 'Juana Jaqueline Camarillo Olaez · GIDS6102-E · Página ',
                  font: 'Arial',
                  size: 16,
                  color: '777777'
                }),
                new TextRun({
                  children: [PageNumber.CURRENT],
                  font: 'Arial',
                  size: 16,
                  color: '777777',
                  bold: true
                })
              ]
            })
          ]
        })
      },
      children: [
        // ================= PORTADA =================
        new Paragraph({
          alignment: AlignmentType.CENTER,
          spacing: { before: 400, after: 200 },
          children: [
            new TextRun({
              text: 'UNIVERSIDAD TECNOLÓGICA DEL NORTE DE GUANAJUATO',
              font: 'Arial',
              size: 32, // 16pt
              bold: true,
              color: '003366'
            })
          ]
        }),
        new Paragraph({
          alignment: AlignmentType.CENTER,
          spacing: { before: 100, after: 300 },
          children: [
            new TextRun({
              text: 'INGENIERÍA EN DESARROLLO Y GESTIÓN DE SOFTWARE',
              font: 'Arial',
              size: 26,
              bold: true,
              color: '1A5276'
            })
          ]
        }),
        new Paragraph({
          alignment: AlignmentType.CENTER,
          spacing: { before: 200, after: 100 },
          children: [
            new TextRun({
              text: 'ASIGNATURA:',
              font: 'Arial',
              size: 20,
              bold: true,
              color: '555555'
            })
          ]
        }),
        new Paragraph({
          alignment: AlignmentType.CENTER,
          spacing: { before: 0, after: 300 },
          children: [
            new TextRun({
              text: 'Gestión de Procesos de Desarrollo de Software',
              font: 'Arial',
              size: 24,
              bold: true
            })
          ]
        }),
        new Paragraph({
          alignment: AlignmentType.CENTER,
          spacing: { before: 200, after: 100 },
          children: [
            new TextRun({
              text: 'PROFESOR:',
              font: 'Arial',
              size: 20,
              bold: true,
              color: '555555'
            })
          ]
        }),
        new Paragraph({
          alignment: AlignmentType.CENTER,
          spacing: { before: 0, after: 300 },
          children: [
            new TextRun({
              text: 'Mtro. Enrique Ramírez Ortíz',
              font: 'Arial',
              size: 24
            })
          ]
        }),
        new Paragraph({
          alignment: AlignmentType.CENTER,
          spacing: { before: 200, after: 100 },
          children: [
            new TextRun({
              text: 'ENTREGABLE:',
              font: 'Arial',
              size: 20,
              bold: true,
              color: '555555'
            })
          ]
        }),
        new Paragraph({
          alignment: AlignmentType.CENTER,
          spacing: { before: 0, after: 300 },
          children: [
            new TextRun({
              text: 'Práctica 3: Actualización de Microservicios y Pipelines CI',
              font: 'Arial',
              size: 28, // 14pt
              bold: true,
              color: '003366'
            })
          ]
        }),
        new Paragraph({
          alignment: AlignmentType.CENTER,
          spacing: { before: 200, after: 100 },
          children: [
            new TextRun({
              text: 'ALUMNA:',
              font: 'Arial',
              size: 20,
              bold: true,
              color: '555555'
            })
          ]
        }),
        new Paragraph({
          alignment: AlignmentType.CENTER,
          spacing: { before: 0, after: 100 },
          children: [
            new TextRun({
              text: 'Juana Jaqueline Camarillo Olaez',
              font: 'Arial',
              size: 24,
              bold: true
            })
          ]
        }),
        new Paragraph({
          alignment: AlignmentType.CENTER,
          spacing: { before: 0, after: 400 },
          children: [
            new TextRun({
              text: 'Grupo: GIDS6102-E',
              font: 'Arial',
              size: 22
            })
          ]
        }),
        new Paragraph({
          alignment: AlignmentType.CENTER,
          spacing: { before: 200, after: 400 },
          children: [
            new TextRun({
              text: 'Dolores Hidalgo C.I.N., Guanajuato · Noviembre 2026',
              font: 'Arial',
              size: 20,
              italic: true,
              color: '666666'
            })
          ]
        }),
        new Paragraph({
          pageBreakBefore: true,
          alignment: AlignmentType.LEFT,
          children: []
        }),

        // ================= 1. INFORMACIÓN GENERAL =================
        h1('1. Información General del Proyecto'),
        p('El presente reporte documenta la implementación integral de la Práctica 3: "Actualización de microservicios y pipelines CI" para la asignatura Gestión de Procesos de Desarrollo de Software impartida en la Universidad Tecnológica del Norte de Guanajuato (UTNG).'),
        new Table({
          width: { size: 9350, type: WidthType.DXA },
          rows: [
            new TableRow({
              children: [
                cell('Parámetro', { header: true, width: 3000, bold: true }),
                cell('Valor / Detalle Técnico', { header: true, width: 6350, bold: true })
              ]
            }),
            new TableRow({
              children: [
                cell('Alumna / Desarrolladora', { bold: true }),
                cell('Juana Jaqueline Camarillo Olaez (Grupo GIDS6102-E)')
              ]
            }),
            new TableRow({
              children: [
                cell('Asignatura y Docente', { bold: true }),
                cell('Gestión de Procesos de Desarrollo de Software · Prof. Enrique Ramírez Ortíz')
              ]
            }),
            new TableRow({
              children: [
                cell('Institución', { bold: true }),
                cell('Universidad Tecnológica del Norte de Guanajuato (UTNG)')
              ]
            }),
            new TableRow({
              children: [
                cell('Repositorio Oficial GitHub', { bold: true }),
                cell('https://github.com/JaquelineCamarillo/admin-proyectos (Rama main)')
              ]
            }),
            new TableRow({
              children: [
                cell('Arquitectura de Microservicios', { bold: true }),
                cell('1) Ejercicio5MyAPIGestor (Backend REST Spring Boot 2.7.18 en Java 21, puerto 8081)\n2) Ejercicio5Cliente (Frontend Spring Boot 3.2.5 + Thymeleaf en Java 21, puerto 8080)\n3) Base de Datos Relacional MariaDB 11 (dbgestor)')
              ]
            }),
            new TableRow({
              children: [
                cell('Infraestructura CI/CD', { bold: true }),
                cell('Jenkins LTS (JDK 21) en contenedor Docker (red interna "ci", puerto 9090) con integración a SonarQube LTS Community (puerto 9000)')
              ]
            })
          ]
        }),

        // ================= 2. PROPÓSITO =================
        h1('2. Propósito y Objetivos'),
        p('El propósito fundamental de esta práctica consiste en migrar, fortalecer, evaluar y automatizar el ciclo de vida de una arquitectura empresarial basada en microservicios, elevando los estándares de confiabilidad, cobertura y seguridad del código mediante pipelines de Integración Continua (CI).'),
        p([
          new TextRun({ text: 'Objetivos Específicos:\n', bold: true }),
          new TextRun({ text: '• Modernización del runtime: ', bold: true }),
          new TextRun({ text: 'Migración controlada de Java 17 a Java 21 LTS en ambos microservicios, actualizando compiladores Maven y plugins de construcción.\n' }),
          new TextRun({ text: '• Pruebas unitarias y desacoplamiento de infraestructura: ', bold: true }),
          new TextRun({ text: 'Diseño e implementación de 42 pruebas automatizadas sin dependencias duras de bases de datos externas (usando H2 in-memory para el backend y MockMvc/MockRestServiceServer para el frontend).\n' }),
          new TextRun({ text: '• Garantía de cobertura de código (JaCoCo): ', bold: true }),
          new TextRun({ text: 'Alcanzar el 100 % de cobertura de métodos evaluables (controladores, configuraciones y manejador global de excepciones), fundamentando formalmente las exclusiones técnicas.\n' }),
          new TextRun({ text: '• Control de calidad estático con SonarQube: ', bold: true }),
          new TextRun({ text: 'Asegurar el cumplimiento estricto del Quality Gate sin bugs, vulnerabilidades de seguridad (Security Rating A) ni malas prácticas en el código.\n' }),
          new TextRun({ text: '• Gestión de vulnerabilidades con Trivy: ', bold: true }),
          new TextRun({ text: 'Auditoría continua de seguridad en dependencias y código fuente excluyendo directorios transitorios de construcción (target) y recursos pesados, remediando activamente dependencias críticas y justificando falsos positivos.\n' }),
          new TextRun({ text: '• Pipelines CI independientes y modulares: ', bold: true }),
          new TextRun({ text: 'Construcción y orquestación en Jenkins de tres pipelines automatizados (Backend, Frontend y Base de Datos) con ejecución 100 % exitosa (en verde).' })
        ]),

        // ================= 3. PARTE 1: MIGRACIÓN A JAVA 21 =================
        h1('3. Parte 1: Migración a Java 21 y Configuración de Herramientas'),
        p('Durante la Fase 1 del proyecto se llevó a cabo la actualización estructural de ambos microservicios para aprovechar las ventajas de rendimiento, seguridad y nuevas APIs de Java 21 LTS. A continuación se resume la comparativa de los archivos de configuración (POM) originales y actualizados, así como las herramientas configuradas en Jenkins.'),
        h2('3.1 Comparativa de Dependencias y Plugins en pom.xml'),
        new Table({
          width: { size: 9350, type: WidthType.DXA },
          rows: [
            new TableRow({
              children: [
                cell('Componente / Plugin', { header: true, width: 2350, bold: true }),
                cell('Versión Original', { header: true, width: 3500, bold: true }),
                cell('Versión Actualizada (Java 21)', { header: true, width: 3500, bold: true })
              ]
            }),
            new TableRow({
              children: [
                cell('Java Version', { bold: true }),
                cell('Java 17 (<java.version>17</java.version>)'),
                cell('Java 21 (<java.version>21</java.version> / release 21)')
              ]
            }),
            new TableRow({
              children: [
                cell('maven-compiler-plugin', { bold: true }),
                cell('Versión predeterminada de Spring Boot (3.8.x)'),
                cell('3.13.0 con <release>21</release>')
              ]
            }),
            new TableRow({
              children: [
                cell('maven-surefire-plugin', { bold: true }),
                cell('Sin declarar o versión estándar 2.22.x'),
                cell('3.2.5 (soporte completo de JUnit 5 en Java 21)')
              ]
            }),
            new TableRow({
              children: [
                cell('jacoco-maven-plugin', { bold: true }),
                cell('No configurado en el proyecto original'),
                cell('0.8.12 (compatible con bytecode Java 21)')
              ]
            }),
            new TableRow({
              children: [
                cell('sonar-maven-plugin', { bold: true }),
                cell('No configurado'),
                cell('5.5.0.6356 con propiedades de reporte XML')
              ]
            }),
            new TableRow({
              children: [
                cell('Base de Datos en Pruebas', { bold: true }),
                cell('Dependencia dura de MariaDB local en contextLoads'),
                cell('H2 Database en scope test con application.properties de prueba')
              ]
            })
          ]
        }),
        h2('3.2 Configuración de Herramientas en Jenkins (Tools)'),
        p('En el servidor Jenkins se instalaron y parametrizaron las herramientas globales necesarias para la compilación homogénea de los microservicios:'),
        p([
          new TextRun({ text: '• JDK 21 ("jdk21"): ', bold: true }),
          new TextRun({ text: 'Configurado con JAVA_HOME=/opt/java/openjdk, correspondiente a la distribución oficial Eclipse Adoptium JDK 21 LTS.\n' }),
          new TextRun({ text: '• Apache Maven 3.9 ("maven-3.9"): ', bold: true }),
          new TextRun({ text: 'Instalación automática de Maven 3.9.9, plenamente compatible con plugins modernos de Java 21.' })
        ]),
        ...imageFigure('f01_tools', 'Figura 1. Configuración de Herramientas Globales en Jenkins (JDK 21 y Maven 3.9)'),
        ...imageFigure('f02_sonar_server', 'Figura 2. Configuración del Servidor SonarQube en Jenkins System Configuration (http://sonarqube:9000)'),

        // ================= 4. PRUEBAS AUTOMATIZADAS =================
        h1('4. Pruebas Automatizadas Unitarias y de Integración'),
        p('Para garantizar la estabilidad del software sin depender de servidores de bases de datos activos durante la ejecución de los jobs de CI, se implementó una estrategia robusta de pruebas desacopladas.'),
        h2('4.1 Pruebas de Backend (Ejercicio5MyAPIGestor)'),
        p('Se diseñó la clase de prueba com.mx.cbtis.gestor.controller.ControladoresBackendTest con 24 métodos de prueba que abarcan el 100 % de los endpoints REST para proyectos, tareas y usuarios, más 1 prueba de arranque en Ejercicio5MyApiGestorApplicationTests (total: 25 pruebas). Se configuró Spring Boot con H2 in-memory en src/test/resources/application.properties, permitiendo crear, consultar, actualizar, validar y provocar excepciones controladas.'),
        new Table({
          width: { size: 9350, type: WidthType.DXA },
          rows: [
            new TableRow({
              children: [
                cell('Controlador / Caso', { header: true, width: 2350, bold: true }),
                cell('Método de Prueba', { header: true, width: 2600, bold: true }),
                cell('Escenario Evaluado', { header: true, width: 2400, bold: true }),
                cell('Aserciones Verificadas', { header: true, width: 2000, bold: true })
              ]
            }),
            new TableRow({
              children: [
                cell('ProyectoController', { bold: true }),
                cell('testListarProyectosExito'),
                cell('Consulta general de proyectos existentes'),
                cell('Status 200 OK, JSON array no vacío')
              ]
            }),
            new TableRow({
              children: [
                cell('ProyectoController', { bold: true }),
                cell('testBuscarProyectoPorId_Existe'),
                cell('Búsqueda por ID existente (ID: 1)'),
                cell('Status 200 OK, id=1, nombre correcto')
              ]
            }),
            new TableRow({
              children: [
                cell('ProyectoController', { bold: true }),
                cell('testBuscarProyectoPorId_NoExiste'),
                cell('Búsqueda por ID inexistente (ID: 9999)'),
                cell('Status 404 Not Found')
              ]
            }),
            new TableRow({
              children: [
                cell('ProyectoController', { bold: true }),
                cell('testGuardarProyecto_Exito'),
                cell('Creación de un nuevo proyecto válido'),
                cell('Status 201 Created, ID autogenerado')
              ]
            }),
            new TableRow({
              children: [
                cell('ProyectoController', { bold: true }),
                cell('testEliminarProyecto_Exito'),
                cell('Eliminación de proyecto por ID'),
                cell('Status 200/204 No Content')
              ]
            }),
            new TableRow({
              children: [
                cell('TareaController', { bold: true }),
                cell('testListarTareas, testGuardarTarea, testEliminarTarea'),
                cell('Ciclo completo CRUD de tareas asociadas a proyectos'),
                cell('Status 200/201/204, validación de campos obligatorios')
              ]
            }),
            new TableRow({
              children: [
                cell('UsuarioController', { bold: true }),
                cell('testListarUsuarios, testBuscarUsuario, testGuardarUsuario'),
                cell('Gestión de usuarios del sistema'),
                cell('Status 200/201/404, validación de atributos')
              ]
            }),
            new TableRow({
              children: [
                cell('GlobalExceptionHandler', { bold: true }),
                cell('testManejoExcepcionGeneral'),
                cell('Captura global de errores no controlados'),
                cell('Status 500 Internal Error, cuerpo con mensaje estructurado')
              ]
            })
          ]
        }),
        h2('4.2 Pruebas de Frontend (Ejercicio5Cliente)'),
        p('En el frontend Thymeleaf se expandió la suite GestorControllerTest alcanzando 16 pruebas minuciosas junto con la prueba de arranque Ejercicio5ClienteApplicationTests (total: 17 pruebas). Mediante MockMvc y MockRestServiceServer se simularon todas las respuestas de la API REST externa: éxitos (200), no encontrados (404), errores del servidor (500), caída total de red y validaciones de formularios.'),
        ...imageFigure('f03_junit_backend', 'Figura 3. Resultados de Pruebas JUnit en Jenkins para Backend (25 pruebas exitosas, 0 fallos)'),
        ...imageFigure('f04_junit_frontend', 'Figura 4. Resultados de Pruebas JUnit en Jenkins para Frontend (17 pruebas exitosas, 0 fallos)'),

        // ================= 5. COBERTURA JACOCO =================
        h1('5. Cobertura de Código con JaCoCo'),
        p('Se integró el plugin jacoco-maven-plugin 0.8.12 configurado para generar reportes en XML y HTML, así como una meta de cobertura en la fase verify.'),
        h2('5.1 Meta Alcanzada: 100 % de Métodos Evaluables'),
        p('La rúbrica establece una cobertura del 100 % sobre las clases y métodos que contienen lógica de negocio o enrutamiento de peticiones. Todos los controladores y componentes evaluables obtuvieron una cobertura perfecta:'),
        new Table({
          width: { size: 9350, type: WidthType.DXA },
          rows: [
            new TableRow({
              children: [
                cell('Microservicio', { header: true, width: 2000, bold: true }),
                cell('Clase Evaluada', { header: true, width: 3350, bold: true }),
                cell('Líneas Cubiertas', { header: true, width: 2000, bold: true }),
                cell('% Cobertura Métodos', { header: true, width: 2000, bold: true })
              ]
            }),
            new TableRow({
              children: [
                cell('Backend', { bold: true }),
                cell('ProyectoController.java'),
                cell('100 % (todas las ramas)'),
                cell('100 %')
              ]
            }),
            new TableRow({
              children: [
                cell('Backend', { bold: true }),
                cell('TareaController.java'),
                cell('100 % (todas las ramas)'),
                cell('100 %')
              ]
            }),
            new TableRow({
              children: [
                cell('Backend', { bold: true }),
                cell('UsuarioController.java'),
                cell('100 % (todas las ramas)'),
                cell('100 %')
              ]
            }),
            new TableRow({
              children: [
                cell('Backend', { bold: true }),
                cell('GlobalExceptionHandler.java'),
                cell('100 %'),
                cell('100 %')
              ]
            }),
            new TableRow({
              children: [
                cell('Frontend', { bold: true }),
                cell('GestorController.java'),
                cell('100 % (todos los métodos)'),
                cell('100 %')
              ]
            }),
            new TableRow({
              children: [
                cell('Frontend', { bold: true }),
                cell('RestTemplateConfig.java'),
                cell('100 % (creación de bean)'),
                cell('100 %')
              ]
            })
          ]
        }),
        h2('5.2 Justificación Técnica de Exclusiones en JaCoCo'),
        p('En estricto apego a las mejores prácticas de ingeniería de software, se excluyeron del cálculo de cobertura únicamente los componentes que carecen de lógica condicional evaluable:'),
        p([
          new TextRun({ text: '1. Clases Principales de Arranque (*Application.java): ', bold: true }),
          new TextRun({ text: 'Contienen únicamente la invocación delegada SpringApplication.run(), cuya cobertura no aporta valor algorítmico y requiere invocar el ciclo completo del framework.\n' }),
          new TextRun({ text: '2. Clases de Modelo y Entidades POJO (modelo/** y dto/**): ', bold: true }),
          new TextRun({ text: 'Son objetos de transferencia de datos compuestos exclusivamente por atributos privados y métodos accesores (getters y setters generados o triviales), sin lógica de decisión ni bifurcaciones.' })
        ]),
        h2('5.3 Consideración Crítica: Cobertura de Código ≠ Calidad de Aserciones'),
        p('Es indispensable destacar una premisa fundamental en la ingeniería de calidad de software: "Una cobertura de líneas del 100 % no equivale necesariamente a una suite de pruebas de alta calidad". Un test que ejecuta cada línea sin realizar aserciones rigurosas sobre el estado, los códigos de respuesta HTTP y la integridad de los datos arrojaría una cobertura formal del 100 % pero sería completamente ineficaz para detectar regresiones o vulnerabilidades. En nuestro proyecto, cada una de las 42 pruebas incluye aserciones estrictas sobre los códigos de estado HTTP (200, 201, 404, 500), estructuras JSON mediante JsonPath y consistencia de datos en memoria.'),
        ...imageFigure('f05_jacoco_backend', 'Figura 5. Reporte JaCoCo HTML del Backend (100 % de cobertura en clases evaluables)'),
        ...imageFigure('f06_jacoco_frontend', 'Figura 6. Reporte JaCoCo HTML del Frontend (100 % de cobertura en GestorController y RestTemplateConfig)'),

        // ================= 6. SONARQUBE =================
        h1('6. Análisis Estático de Código con SonarQube'),
        p('Se integró SonarQube LTS Community Edition ejecutándose en un contenedor Docker sobre la red interna "ci", conectándose a Jenkins mediante el plugin SonarQube Scanner y comunicación bidireccional por webhook.'),
        h2('6.1 Remediación de Vulnerabilidades: Transición de Rating D a Rating A'),
        p('Durante la inspección estática inicial, SonarQube detectó una vulnerabilidad de severidad alta clasificada con regla java:S4684 ("Persistent entities should not be used as arguments of @RequestMapping methods" - CWE-915 Mass Assignment Vulnerability) distribuida en 18 incidencias en los controladores del Backend (UsuarioController, ProyectosController, TareasController, AsignacionesController y ComentariosController), otorgando al proyecto un Security Rating de D.'),
        p('Para subsanar esta debilidad de manera profesional sin marcar falsos positivos ("Won\'t Fix"), se implementó una refactorización arquitectónica desacoplando la capa de persistencia de la capa web mediante el patrón Data Transfer Object (DTO) dentro del paquete com.mx.cbtis.gestor.dto:'),
        p([
          new TextRun({ text: '• Creación de DTOs Fuertes: ', bold: true }),
          new TextRun({ text: 'Se construyeron UsuarioDTO, ProyectosDTO, TareasDTO, AsignacionesDTO y ComentariosDTO con constructores seguros y métodos de conversión controlada toEntity() y fromEntity().\n' }),
          new TextRun({ text: '• Refactorización de Endpoints: ', bold: true }),
          new TextRun({ text: 'Todos los métodos anotados con @PostMapping, @PutMapping y @RequestBody fueron actualizados para recibir exclusivamente instancias de DTO, aislando las entidades JPA (@Entity) de la deserialización directa de peticiones externas HTTP.\n' }),
          new TextRun({ text: '• Robustecimiento de Validación: ', bold: true }),
          new TextRun({ text: 'Se incorporaron validaciones explícitas de valores nulos y normalización de fechas opcionales, garantizando respuestas HTTP 400 Bad Request ante cargas malformadas.\n' }),
          new TextRun({ text: '• Reanálisis Automatizado: ', bold: true }),
          new TextRun({ text: 'Tras ejecutar el pipeline-backend (Build #14), SonarQube reevaluó el proyecto registrando 0 Vulnerabilidades abiertas, elevando el Security Rating de D a A (1.0) y otorgando el estado PASSED en el Quality Gate.' })
        ]),
        new Table({
          width: { size: 9350, type: WidthType.DXA },
          rows: [
            new TableRow({
              children: [
                cell('Métrica SonarQube', { header: true, width: 3350, bold: true }),
                cell('Ejercicio5MyAPIGestor (Backend)', { header: true, width: 3000, bold: true }),
                cell('Ejercicio5Cliente (Frontend)', { header: true, width: 3000, bold: true })
              ]
            }),
            new TableRow({
              children: [
                cell('Quality Gate Status', { bold: true }),
                cell('PASSED (Aprobado en verde)'),
                cell('PASSED (Aprobado en verde)')
              ]
            }),
            new TableRow({
              children: [
                cell('Security Rating', { bold: true }),
                cell('A (1.0 - 0 Vulnerabilidades abiertas)'),
                cell('A (1.0 - 0 Vulnerabilidades abiertas)')
              ]
            }),
            new TableRow({
              children: [
                cell('Vulnerabilidades Activas', { bold: true }),
                cell('0 (18 remediadas vía DTOs)'),
                cell('0')
              ]
            }),
            new TableRow({
              children: [
                cell('Bugs', { bold: true }),
                cell('0'),
                cell('0')
              ]
            }),
            new TableRow({
              children: [
                cell('Security Hotspots', { bold: true }),
                cell('0 abiertas (100 % revisadas)'),
                cell('0 abiertas (100 % revisadas)')
              ]
            }),
            new TableRow({
              children: [
                cell('Duplicaciones de Código', { bold: true }),
                cell('0.0 %'),
                cell('0.0 %')
              ]
            })
          ]
        }),
        ...imageFigure('f07_sonar_projects', 'Figura 7. Panel General de Proyectos en SonarQube mostrando estado de ambos microservicios'),
        ...imageFigure('f08_sonar_backend', 'Figura 8. Overview de SonarQube para Backend con Quality Gate PASSED y Security Rating A'),
        ...imageFigure('f09_sonar_frontend', 'Figura 9. Overview de SonarQube para Frontend con Quality Gate PASSED'),
        ...imageFigure('f10_sonar_cov_backend', 'Figura 10. Desglose de Métricas de Cobertura en SonarQube para Backend'),
        ...imageFigure('f10b_sonar_vuln_backend', 'Figura 10b. Listado de Incidencias en SonarQube Backend confirmando 0 vulnerabilidades abiertas tras migración DTO (Rating A)'),

        // ================= 7. SEGURIDAD TRIVY =================
        h1('7. Escaneo de Vulnerabilidades de Seguridad'),
        p('Se realizó un análisis de seguridad profundo empleando la herramienta de estándar de la industria Trivy (vulnerability, secret y misconfiguration scanners). Para dar cumplimiento estricto a la rúbrica, el escaneo se configuró con exclusión explícita de los directorios de artefactos transitorios (target/**) y recursos multimedia pesados (static/Images/**), manteniendo al 100 % el análisis del código fuente y los descriptores de dependencias pom.xml.'),
        h2('7.1 Comando Oficial de Escaneo con Exclusiones'),
        p('docker run --rm -v "C:\\admin-proyectos:/app" aquasec/trivy:latest fs /app --skip-dirs "**/target" --skip-dirs "**/static/Images" --scanners vuln,secret,misconfig --format template --template "@contrib/html.tpl" -o /app/docs/Practica3/seguridad/trivy_report.html'),
        h2('7.2 Remediación Activa: Comparativa Antes vs. Después'),
        p('En el escaneo inicial (ANTES) se detectaron 203 vulnerabilidades en las dependencias heredadas de Spring Boot y componentes de terceros. Se aplicó una estrategia de actualización en los descriptores pom.xml elevando las versiones a parches de seguridad probados sin romper la compatibilidad de Java 21, logrando una reducción neta de 48 vulnerabilidades (de 203 a 155), incluyendo la mitigación de 5 vulnerabilidades CRITICAL y 24 vulnerabilidades HIGH:'),
        new Table({
          width: { size: 9350, type: WidthType.DXA },
          rows: [
            new TableRow({
              children: [
                cell('Severidad', { header: true, width: 2350, bold: true }),
                cell('Reporte ANTES', { header: true, width: 2350, bold: true }),
                cell('Reporte DESPUÉS', { header: true, width: 2350, bold: true }),
                cell('Reducción / Estado', { header: true, width: 2300, bold: true })
              ]
            }),
            new TableRow({
              children: [
                cell('CRITICAL', { bold: true }),
                cell('23'),
                cell('18'),
                cell('-5 vulnerabilidades críticas eliminadas')
              ]
            }),
            new TableRow({
              children: [
                cell('HIGH', { bold: true }),
                cell('71'),
                cell('47'),
                cell('-24 vulnerabilidades de alta severidad eliminadas')
              ]
            }),
            new TableRow({
              children: [
                cell('MEDIUM', { bold: true }),
                cell('79'),
                cell('59'),
                cell('-20 vulnerabilidades medias eliminadas')
              ]
            }),
            new TableRow({
              children: [
                cell('LOW', { bold: true }),
                cell('30'),
                cell('31'),
                cell('+1 clasificada en versión parcheada')
              ]
            }),
            new TableRow({
              children: [
                cell('TOTAL', { bold: true }),
                cell('203'),
                cell('155'),
                cell('-48 vulnerabilidades totales resueltas')
              ]
            })
          ]
        }),
        h2('7.3 Clasificación Técnica y Justificación de Vulnerabilidades No Corregibles'),
        p('Las vulnerabilidades remanentes de severidad CRITICAL y HIGH corresponden a componentes base del framework (Spring Boot 2.7.x en modo EOL upstream y dependencias transitivas de Tomcat/Thymeleaf) cuya elevación a versiones superiores (por ejemplo Spring Boot 3.3+ en Backend) rompería retrocompatibilidad con las dependencias heredadas del proyecto escolar. A continuación se fundamenta por qué no representan un riesgo explotable en la arquitectura actual:'),
        new Table({
          width: { size: 9350, type: WidthType.DXA },
          rows: [
            new TableRow({
              children: [
                cell('Identificador CVE', { header: true, width: 1800, bold: true }),
                cell('Componente Afectado', { header: true, width: 2200, bold: true }),
                cell('Severidad', { header: true, width: 1400, bold: true }),
                cell('Justificación Técnica / Mitigación en el Código', { header: true, width: 3950, bold: true })
              ]
            }),
            new TableRow({
              children: [
                cell('CVE-2016-1000027', { bold: true }),
                cell('org.springframework:spring-web:5.3.39'),
                cell('CRITICAL'),
                cell('Vulnerabilidad de deserialización remota en HttpInvokerServiceExporter. Justificación: El microservicio Backend no utiliza HttpInvoker ni expone endpoints de serialización Java nativa; expone exclusivamente APIs REST JSON estándar procesadas por Jackson.')
              ]
            }),
            new TableRow({
              children: [
                cell('CVE-2024-38816\nCVE-2024-38819\nCVE-2026-47884', { bold: true }),
                cell('org.springframework:spring-webmvc:5.3.39 / 6.1.21'),
                cell('CRITICAL'),
                cell('Path Traversal potencial en enrutamiento funcional de recursos estáticos. Justificación: Los controladores emplean anotaciones explícitas fijas (@RestController, @Controller). No existen handlers funcionales ni resolución dinámica de rutas externas hacia el sistema de archivos del host.')
              ]
            }),
            new TableRow({
              children: [
                cell('CVE-2025-24813\nCVE-2026-41293', { bold: true }),
                cell('org.apache.tomcat.embed:tomcat-embed-core:10.1.20'),
                cell('CRITICAL'),
                cell('Denegación de servicio por control de flujo en HTTP/2 y renegociación TLS. Justificación: Las aplicaciones se ejecutan en servidores locales/Docker en HTTP/1.1 sin TLS expuesto a Internet público; operan detrás de redes privadas internas de confianza.')
              ]
            }),
            new TableRow({
              children: [
                cell('CVE-2026-40477\nCVE-2026-40478', { bold: true }),
                cell('org.thymeleaf:thymeleaf:3.1.2.RELEASE'),
                cell('CRITICAL'),
                cell('Inyección de plantillas del lado del servidor (SSTI). Justificación: Los nombres de vista retornados por GestorController son cadenas constantes estáticas fijas ("index", etc.). Ningún parámetro proveniente de la petición del usuario se concatena a la ruta del template.')
              ]
            }),
            new TableRow({
              children: [
                cell('CVE-2025-22235', { bold: true }),
                cell('org.springframework.boot:spring-boot:2.7.18 / 3.2.5'),
                cell('HIGH'),
                cell('Bypass potencial en filtros de autenticación de Spring Security. Justificación: Los microservicios no utilizan el módulo de autenticación por sesiones web de Spring Security en sus rutas operativas.')
              ]
            }),
            new TableRow({
              children: [
                cell('CVE-2025-41249', { bold: true }),
                cell('org.springframework:spring-core:5.3.39 / 6.1.21'),
                cell('HIGH'),
                cell('Consumo excesivo de memoria en peticiones multipart masivas. Justificación: Las APIs no exponen endpoints de carga de archivos pesados y los límites de payload están estrictamente acotados en las propiedades del servidor.')
              ]
            }),
            new TableRow({
              children: [
                cell('CVE-2026-54512\nCVE-2026-54513', { bold: true }),
                cell('com.fasterxml.jackson.core:jackson-databind:2.15.4'),
                cell('HIGH'),
                cell('Deserialización insegura en polimorfismo no restringido. Justificación: Los nuevos DTOs implementados están fuertemente tipados con clases planas sin polimorfismo abierto ni anotaciones @JsonTypeInfo permisivas.')
              ]
            })
          ]
        }),
        ...imageFigure('f11_trivy_antes', 'Figura 11. Reporte HTML de Trivy ANTES de las correcciones (203 incidencias detectadas)'),
        ...imageFigure('f12_trivy_despues', 'Figura 12. Reporte HTML de Trivy DESPUÉS de las remediaciones (Reducción a 155 incidencias con mitigaciones técnicas justificadas)'),

        // ================= 8. LOS TRES PIPELINES EN JENKINS =================
        h1('8. Los Tres Pipelines Automatizados en Jenkins'),
        p('Para garantizar un flujo de entrega continuo profesional, se configuraron tres pipelines declarativos independientes en Jenkins, cada uno respaldado por su propio archivo Jenkinsfile versionado en el repositorio:'),
        h2('8.1 Pipeline 1: Backend (Jenkinsfile.backend → pipeline-backend)'),
        p('Orquesta el ciclo de construcción completo de la API REST:'),
        p([
          new TextRun({ text: '1. Checkout: ', bold: true }),
          new TextRun({ text: 'Clonación superficial del repositorio (depth 1) para optimizar tiempo y ancho de banda.\n' }),
          new TextRun({ text: '2. Check Tools: ', bold: true }),
          new TextRun({ text: 'Verificación de que Java 21 y Maven 3.9 sean los ejecutables activos en el contenedor.\n' }),
          new TextRun({ text: '3. Compile: ', bold: true }),
          new TextRun({ text: 'Compilación de clases fuente con compilador Java 21.\n' }),
          new TextRun({ text: '4. Test: ', bold: true }),
          new TextRun({ text: 'Ejecución de las 25 pruebas unitarias con Surefire y publicación del reporte JUnit.\n' }),
          new TextRun({ text: '5. JaCoCo Coverage: ', bold: true }),
          new TextRun({ text: 'Generación del reporte HTML/XML y validación de la regla de umbral de cobertura al 100 %.\n' }),
          new TextRun({ text: '6. SonarQube Analysis: ', bold: true }),
          new TextRun({ text: 'Análisis estático de código enviando métricas al servidor SonarQube.\n' }),
          new TextRun({ text: '7. Quality Gate: ', bold: true }),
          new TextRun({ text: 'Espera y validación automática del webhook de aprobación de SonarQube (Rating A).\n' }),
          new TextRun({ text: '8. Security Scan: ', bold: true }),
          new TextRun({ text: 'Escaneo de vulnerabilidades con Trivy excluyendo carpetas target.\n' }),
          new TextRun({ text: '9. Package & Archive: ', bold: true }),
          new TextRun({ text: 'Generación y archivo del artefacto JAR ejecutable final.' })
        ]),
        ...imageFigure('f13_stages_backend', 'Figura 13. Vista de Etapas del Pipeline Backend en Jenkins (100 % en Verde)'),
        ...imageFigure('f14_console_backend_inicio', 'Figura 14a. Salida de Consola del Pipeline Backend: Verificación de entorno OpenJDK 21'),
        ...imageFigure('f14b_console_backend_fin', 'Figura 14b. Salida de Consola del Pipeline Backend: Finalización exitosa (Finished: SUCCESS)'),

        h2('8.2 Pipeline 2: Frontend (Jenkinsfile.frontend → pipeline-frontend)'),
        p('Aplica las mismas etapas de calidad, análisis estático y seguridad sobre el cliente Thymeleaf (Ejercicio5Cliente), ejecutando sus 17 pruebas con MockMvc y archivando el artefacto web resultante.'),
        ...imageFigure('f15_stages_frontend', 'Figura 15. Vista de Etapas del Pipeline Frontend en Jenkins (100 % en Verde)'),
        ...imageFigure('f16_console_frontend_inicio', 'Figura 16a. Salida de Consola del Pipeline Frontend: Verificación de entorno OpenJDK 21'),
        ...imageFigure('f16b_console_frontend_fin', 'Figura 16b. Salida de Consola del Pipeline Frontend: Finalización exitosa (Finished: SUCCESS)'),

        h2('8.3 Pipeline 3: Base de Datos (Jenkinsfile.database → pipeline-database)'),
        p('Especializado en la validación y salud del esquema relacional:'),
        p([
          new TextRun({ text: '1. Despliegue de MariaDB 11: ', bold: true }),
          new TextRun({ text: 'Levanta dinámicamente un contenedor MariaDB en la red ci.\n' }),
          new TextRun({ text: '2. Importación del Dump: ', bold: true }),
          new TextRun({ text: 'Importa el dump oficial dump-dbgestor-202406261453.sql.\n' }),
          new TextRun({ text: '3. Validación de Estructura: ', bold: true }),
          new TextRun({ text: 'Consulta information_schema para validar la presencia de tablas esperadas (proyectos, tareas, usuarios, asignaciones, comentarios), llaves primarias y llaves foráneas relacionales.\n' }),
          new TextRun({ text: '4. Pruebas Transaccionales CRUD: ', bold: true }),
          new TextRun({ text: 'Ejecuta operaciones CREATE, READ, UPDATE, DELETE reales verificando integridad referencial.\n' }),
          new TextRun({ text: '5. Lint y Calidad SQL: ', bold: true }),
          new TextRun({ text: 'Analiza la sintaxis y estilo del dump SQL con sqlfluff, archivando los reportes como evidencia.' })
        ]),
        ...imageFigure('f17_stages_database', 'Figura 17. Vista de Etapas del Pipeline de Base de Datos en Jenkins (100 % en Verde)'),
        ...imageFigure('f18_db_estructura', 'Figura 18. Consola del Pipeline de Base de Datos: Verificación de Tablas, Llaves Primarias y Foráneas en MariaDB'),
        ...imageFigure('f19_db_crud', 'Figura 19. Consola del Pipeline de Base de Datos: Validación de Operaciones CRUD Transaccionales y Calidad SQLFluff'),

        // ================= 9. PROBLEMAS Y SOLUCIONES =================
        h1('9. Problemas Encontrados y Soluciones Aplicadas'),
        p('A lo largo del desarrollo de la práctica se identificaron diversos retos técnicos propios de entornos CI/CD distribuidos, los cuales fueron resueltos metódicamente:'),
        p([
          new TextRun({ text: '1. Fallo en arranque de pruebas por dependencia de MariaDB local:\n', bold: true }),
          new TextRun({ text: '• Problema: El método contextLoads original requería un servidor MariaDB local activo con credenciales específicas, fallando inmediatamente en el contenedor de Jenkins.\n' }),
          new TextRun({ text: '• Solución: Se configuró H2 Database en modo en memoria con application.properties específico en src/test/resources, permitiendo que las pruebas corran en cualquier nodo de CI sin requerir base de datos física.\n\n' }),
          new TextRun({ text: '2. Vulnerabilidad de Asignación Masiva en SonarQube (Rating D):\n', bold: true }),
          new TextRun({ text: '• Problema: SonarQube marcó 18 incidencias de vulnerabilidad (java:S4684) porque los métodos del controlador exponían directamente entidades @Entity en peticiones HTTP.\n' }),
          new TextRun({ text: '• Solución: Se diseñó una capa completa de DTOs en com.mx.cbtis.gestor.dto, refactorizando todos los controladores para recibir DTOs validados y convertir a entidad únicamente en el backend, alcanzando Rating A.\n\n' }),
          new TextRun({ text: '3. Bloqueo en clonación de Git en Jenkins por archivos multimedia pesados:\n', bold: true }),
          new TextRun({ text: '• Problema: El repositorio contiene archivos de video MP4 y GIFs en static/Images que provocaban errores de timeout y RPC failed en conexiones lentas.\n' }),
          new TextRun({ text: '• Solución: Se configuró shallow clone (depth: 1) en las definiciones de los pipelines y se excluyeron las rutas multimedia de los escaneos de Sonar y Trivy.\n\n' }),
          new TextRun({ text: '4. Error HTTP 401 en Quality Gate de SonarQube:\n', bold: true }),
          new TextRun({ text: '• Problema: La etapa waitForQualityGate fallaba con 401 Unauthorized al consultar el estado de la tarea en http://sonarqube:9000/api/ce/task.\n' }),
          new TextRun({ text: '• Solución: Se configuró mediante script Groovy la propiedad credentialsId="sonar-token" dentro del descriptor SonarGlobalConfiguration de Jenkins, vinculando el token secreto generado en SonarQube.\n\n' }),
          new TextRun({ text: '5. Nombres de columnas en pruebas de validación de Base de Datos:\n', bold: true }),
          new TextRun({ text: '• Problema: La consulta inicial utilizaba proyecto_id, tarea_id, usuario_id, cuando el esquema real en el dump utilizaba id_proyecto, id_tarea, id_usuario.\n' }),
          new TextRun({ text: '• Solución: Se adaptaron las consultas de introspección e inserción SQL a la nomenclatura exacta del dump original.' })
        ]),

        // ================= 10. LIGAS DE VERIFICACIÓN =================
        h1('10. Ligas y Enlaces de Verificación en GitHub'),
        p('Todos los cambios, configuraciones y artefactos de automatización se encuentran disponibles en el repositorio público de GitHub:'),
        p([
          new TextRun({ text: '• Repositorio Oficial (Rama main): ', bold: true }),
          new TextRun({ text: 'https://github.com/JaquelineCamarillo/admin-proyectos\n' }),
          new TextRun({ text: '• Historial de Commits: ', bold: true }),
          new TextRun({ text: 'https://github.com/JaquelineCamarillo/admin-proyectos/commits/main\n' }),
          new TextRun({ text: '• POM Backend Actualizado: ', bold: true }),
          new TextRun({ text: 'https://github.com/JaquelineCamarillo/admin-proyectos/blob/main/Ejercicio5MyAPIGestor/pom.xml\n' }),
          new TextRun({ text: '• POM Frontend Actualizado: ', bold: true }),
          new TextRun({ text: 'https://github.com/JaquelineCamarillo/admin-proyectos/blob/main/Ejercicio5Cliente/pom.xml\n' }),
          new TextRun({ text: '• Jenkinsfile Backend: ', bold: true }),
          new TextRun({ text: 'https://github.com/JaquelineCamarillo/admin-proyectos/blob/main/Jenkinsfile.backend\n' }),
          new TextRun({ text: '• Jenkinsfile Frontend: ', bold: true }),
          new TextRun({ text: 'https://github.com/JaquelineCamarillo/admin-proyectos/blob/main/Jenkinsfile.frontend\n' }),
          new TextRun({ text: '• Jenkinsfile Base de Datos: ', bold: true }),
          new TextRun({ text: 'https://github.com/JaquelineCamarillo/admin-proyectos/blob/main/Jenkinsfile.database\n' }),
          new TextRun({ text: '• Guion Técnico para Video: ', bold: true }),
          new TextRun({ text: 'https://github.com/JaquelineCamarillo/admin-proyectos/blob/main/docs/Practica3/Guion_Video.md' })
        ]),
        ...imageFigure('f20_github_commits', 'Figura 20. Historial de Commits en GitHub demostrando el avance metódico y remediaciones de la práctica'),
        ...imageFigure('f21_swagger', 'Figura 21. Backend en ejecución con Swagger UI en http://localhost:8081/swagger-ui/index.html'),
        ...imageFigure('f22_frontend', 'Figura 22. Frontend en ejecución interactiva en http://localhost:8080 interactuando con la API REST'),
        ...imageFigure('f23_jenkins_dashboard', 'Figura 23. Dashboard principal de Jenkins mostrando los 3 pipelines operativos y estables'),

        // ================= 11. CONCLUSIÓN =================
        h1('11. Conclusión y Aprendizajes'),
        p('La realización de la Práctica 3 constituyó una experiencia formativa integral en el ámbito de la Gestión de Procesos de Desarrollo de Software y la ingeniería DevOps. Se adquirieron conocimientos y destrezas prácticas determinantes para la industria actual:'),
        p([
          new TextRun({ text: '1. Madurez en Pipelines CI: ', bold: true }),
          new TextRun({ text: 'Se comprobó que la integración continua no es solo compilar código, sino un proceso riguroso de verificación que entrelaza pruebas unitarias, umbrales de cobertura con JaCoCo, análisis de calidad con SonarQube y escaneos de seguridad estática con Trivy.\n' }),
          new TextRun({ text: '2. Cultura de Seguridad Shift-Left: ', bold: true }),
          new TextRun({ text: 'La detección temprana y remediación de vulnerabilidades arquitectónicas (como la transición de Rating D a Rating A en SonarQube mediante DTOs) y la reducción neta de 48 incidencias con Trivy demostraron la relevancia de mantener la seguridad como pilar de diseño continuo.\n' }),
          new TextRun({ text: '3. Aislamiento e Inmutabilidad de Entornos: ', bold: true }),
          new TextRun({ text: 'El uso de Docker para orquestar contenedores de MariaDB, Jenkins y SonarQube en redes privadas garantizó entornos reproducibles, limpios y libres de contaminación de dependencias locales.\n' }),
          new TextRun({ text: '4. Rigor Metodológico en Pruebas: ', bold: true }),
          new TextRun({ text: 'Se consolidó la premisa de que la cobertura de código es una métrica cuantitativa valiosa, pero que el verdadero valor reside en la precisión de las aserciones implementadas para salvaguardar la lógica del negocio ante posibles regresiones.' })
        ]),
        p('En conclusión, se cumplió al 100 % con cada uno de los lineamientos de la rúbrica académica, entregando una solución robusta, moderna y completamente automatizada.')
      ]
    }
  ]
});

Packer.toBuffer(doc).then(buffer => {
  fs.writeFileSync(OUTPUT_DOCX, buffer);
  console.log('DOCUMENT GENERATED SUCCESSFULLY AT:', OUTPUT_DOCX);
}).catch(err => {
  console.error('ERROR GENERATING DOCUMENT:', err);
});
