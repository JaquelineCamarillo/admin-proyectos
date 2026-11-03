# Guion de Video · Práctica 3: Actualización de Microservicios y Pipelines CI

- **Alumna:** Juana Jaqueline Camarillo Olaez
- **Grupo:** GIDS6102-E · Universidad Tecnológica del Norte de Guanajuato (UTNG)
- **Asignatura:** Gestión de Procesos de Desarrollo de Software
- **Profesor:** Enrique Ramírez Ortíz
- **Duración estimada:** 5 a 7 minutos

---

## Minuto 0:00 – 0:45 · Introducción y Verificación de la Parte 1 (Java 21 y Herramientas)

**[EN PANTALLA: Mostrar repositorio de GitHub `https://github.com/JaquelineCamarillo/admin-proyectos` con los commits recientes, luego cambiar a Jenkins → Administrar Jenkins → Tools (`http://localhost:9090/manage/tools/`)]**

> "Hola, buen día profesor Enrique. Soy Juana Jaqueline Camarillo Olaez, del grupo GIDS6102-E de la asignatura Gestión de Procesos de Desarrollo de Software. En este video presento la ejecución completa y el aprendizaje obtenido en la **Práctica 3: Actualización de microservicios y pipelines de Integración Continua (CI)** sobre el sistema Gestor de Proyectos (`admin-proyectos`).
>
> Como punto de partida de la Parte 1, ambos microservicios —el backend `Ejercicio5MyAPIGestor` con Spring Boot 2.7.18 y el frontend `Ejercicio5Cliente` con Spring Boot 3.2.5— fueron actualizados a **Java 21**. Aquí en la configuración de herramientas de Jenkins podemos ver registrados el **JDK 21 (`jdk21` en `/opt/java/openjdk`)** y **Maven 3.9 (`maven-3.9`)**, además de la conexión con nuestro servidor **SonarQube** en la red Docker `ci`. En ambos archivos `pom.xml` configuramos `maven-compiler-plugin` 3.13.0 con `<release>21</release>`, `maven-surefire-plugin` 3.2.5, `jacoco-maven-plugin` 0.8.12 y `sonar-maven-plugin` 5.5.0.6356."

---

## Minuto 0:45 – 2:10 · Pruebas Automatizadas y Cobertura del 100 % con JaCoCo

**[EN PANTALLA: Mostrar en el IDE o GitHub las clases `ControladoresBackendTest.java`, `application.properties` de test con H2, y `GestorControllerTest.java`. Luego abrir los dos reportes `target/site/jacoco/index.html` en el navegador]**

> "Para cumplir con el primer y segundo punto de la Parte 2, resolvimos primero un problema clave de infraestructura: originalmente, las pruebas del backend requerían una instancia activa de MariaDB, lo que impedía que corrieran de forma aislada en el contenedor de Jenkins. Para solucionarlo, agregamos la base de datos en memoria **H2** con alcance `test` y creamos el archivo `src/test/resources/application.properties` con el dialecto `H2Dialect` en modo compatible con MySQL.
>
> Desarrollamos una suite de **25 pruebas automatizadas en el backend** (`ControladoresBackendTest` y `Ejercicio5MyApiGestorApplicationTests`) que cubren la totalidad de los 5 controladores REST (`UsuarioController`, `ProyectosController`, `TareasController`, `AsignacionesController` y `ComentariosController`) así como el manejador global de errores `GlobalExceptionHandler`. Evaluamos casos de éxito (HTTP 200), recursos no encontrados (HTTP 404), peticiones con datos inválidos o fechas mal formadas (HTTP 400) y conflictos de integridad referencial (HTTP 409), verificando tanto el código de estado como la estructura JSON de respuesta.
>
> En el **frontend (`Ejercicio5Cliente`)**, ampliamos `GestorControllerTest` con `MockMvc` y `MockRestServiceServer` hasta alcanzar **17 pruebas automatizadas** que simulan respuestas exitosas de la API, errores 4xx con y sin cuerpo, errores 500, caídas de red (`ResourceAccessException`) y validaciones de formularios. Incluso durante las pruebas detectamos y corregimos un defecto real en `GestorController`: los métodos `/tarea` y `/saveComentario` no inyectaban el atributo `tarea` en el `Model`, lo que provocaba una excepción de plantilla en Thymeleaf.
>
> En pantalla muestro los reportes verificables de **JaCoCo**: alcanzamos **100 % de cobertura de instrucciones, ramas, líneas y métodos** en las clases evaluables tanto del backend como del frontend, imponiendo además la regla `jacoco:check` en la fase `verify`. Documentamos únicamente las exclusiones justificadas: las clases `main` de arranque (`SpringApplication.run`) y los paquetes `modelo` que contienen exclusivamente POJOs/entidades con getters y setters sin lógica de negocio. Es importante recalcar que **un 100 % de cobertura no equivale por sí mismo a calidad de las pruebas**; la verdadera calidad radica en que cada test realiza aserciones específicas sobre el estado HTTP, las vistas retornadas y los atributos del modelo."

---

## Minuto 2:10 – 3:25 · Análisis Estático de Calidad con SonarQube y Quality Gate

**[EN PANTALLA: Mostrar el Dashboard de SonarQube (`http://localhost:9000`) con los dos proyectos: `Ejercicio5MyAPIGestor` y `Ejercicio5Cliente`, mostrando Quality Gate en verde (Passed), porcentaje de cobertura, Issues y Security Hotspots]**

> "A continuación, muestro el servidor **SonarQube LTS Community** corriendo en el puerto 9000 dentro de la red Docker `ci`, integrado con Jenkins mediante un token secreto (`sonar-token`) y un webhook hacia `/sonarqube-webhook/`.
>
> Configuramos la propiedad `sonar.exclusions` en ambos `pom.xml` para excluir las carpetas `target` y los recursos pesados de `static/Images` (videos MP4 y animaciones GIF), sin excluir ninguna clase del código fuente. Como se observa en el dashboard de ambos microservicios, SonarQube recibe el reporte XML de JaCoCo, refleja el **100 % de cobertura** sobre el código evaluable y aprueba satisfactoriamente el **Quality Gate (`Passed`)**, permitiendo que los pipelines continúen automáticamente tras el paso `waitForQualityGate`."

---

## Minuto 3:25 – 4:35 · Escaneo de Vulnerabilidades con Trivy (Antes y Después)

**[EN PANTALLA: Mostrar en el navegador los reportes `docs/Practica3/seguridad/trivy_antes.html` y `trivy_despues.html` lado a lado o secuencialmente, y señalar las propiedades actualizadas en ambos `pom.xml`]**

> "Para el análisis de vulnerabilidades de seguridad, ejecutamos **Aqua Trivy** sobre el código fuente con los escáneres de vulnerabilidades (`vuln`), secretos (`secret`) y malas configuraciones (`misconfig`), excluyendo explícitamente las carpetas `target` y `static/Images` mediante `--skip-dirs`.
>
> En el escaneo inicial (**Antes**), Trivy detectó **0 secretos expuestos** en el repositorio (ya que las contraseñas se manejan por variables de entorno y credenciales de Jenkins), pero identificó **203 vulnerabilidades CVE** en dependencias transitivas: **115 en el backend** y **88 en el frontend**, incluyendo 23 críticas y 71 altas en componentes como `tomcat-embed-core`, `snakeyaml`, `jackson-databind`, `thymeleaf`, `mariadb-java-client`, `commons-lang3` y `logback`.
>
> Clasificamos cada hallazgo y aplicamos acciones correctivas directas en la sección `<properties>` de ambos `pom.xml`: actualizamos Tomcat embebido a `9.0.123` y `10.1.61`, SnakeYAML a `2.2`, Jackson BOM a `2.22.4`, Thymeleaf a `3.1.5.RELEASE`, MariaDB JDBC Client a `3.5.11`, Commons Lang3 a `3.18.0`, Log4j2 API a `2.26.1` y Spring Framework a `5.3.39` y `6.1.21`. Con esto **eliminamos 140 vulnerabilidades reales (reducción del 69 %, eliminando el 87 % de las críticas)** sin romper la compatibilidad con Spring Boot 2.7 y 3.2, como se demuestra en el reporte **Después** (`trivy_despues.html`)."

---

## Minuto 4:35 – 6:00 · Ejecución de los Tres Pipelines de CI en Jenkins (Backend, Frontend y Base de Datos)

**[EN PANTALLA: Mostrar el panel principal de Jenkins (`http://localhost:9090`) con los 3 jobs en verde: `pipeline-backend`, `pipeline-frontend` y `pipeline-database`. Abrir la vista de etapas (Stage View) y la salida de consola de cada uno, los artefactos archivados y finalmente Swagger UI (`http://localhost:8081/swagger-ui.html`) y el Frontend (`http://localhost:8080`)]**

> "En Jenkins construimos tres pipelines independientes bajo el esquema *Pipeline script from SCM* con clonado superficial (`shallow clone depth 1`) y tiempo límite de 20 minutos:
>
> 1. **`pipeline-backend` (`Jenkinsfile.backend`)**: descarga el código, verifica Java 21 y Maven 3.9, compila, ejecuta las 25 pruebas unitarias publicando resultados JUnit, genera y verifica el 100 % de cobertura JaCoCo, ejecuta el análisis en SonarQube esperando el Quality Gate, corre el escaneo de Trivy excluyendo `target` y empaqueta y archiva el JAR ejecutable.
> 2. **`pipeline-frontend` (`Jenkinsfile.frontend`)**: ejecuta el mismo flujo de aseguramiento de calidad y seguridad sobre `Ejercicio5Cliente`, validando sus 17 pruebas y archivando el artefacto final.
> 3. **`pipeline-database` (`Jenkinsfile.database`)**: se conecta al contenedor `mariadb-ci` (MariaDB 11) en la red `ci` usando credenciales seguras de Jenkins, importa el volcado `dump-dbgestor-202406261453.sql`, verifica la existencia de las 6 tablas del sistema (`asignaciones`, `comentarios`, `hibernate_sequence`, `proyectos`, `tareas` y `usuario`), aplica y valida las llaves primarias y foráneas en `information_schema`, ejecuta pruebas CRUD completas (INSERT, SELECT con JOIN, UPDATE y DELETE) y realiza análisis estático de calidad SQL con **SQLFluff**, archivando todos los reportes.
>
> Finalmente, muestro ambos microservicios en ejecución local: la documentación interactiva de la API REST en **Swagger UI** sobre el puerto 8081 y la interfaz web del **Gestor de Proyectos** en el puerto 8080 consumiendo la API correctamente."

---

## Minuto 6:00 – 6:30 · Problemas Resueltos y Conclusión del Aprendizaje

**[EN PANTALLA: Mostrar el documento final `P3_Reporte_Completo_AdminProyectos.docx` / `.pdf`]**

> "Entre los principales retos técnicos que resolvimos estuvieron: desacoplar las pruebas del backend mediante H2 con dialecto compatible, configurar el mirror oficial de Google Maven Central para evitar bloqueos HTTP 429 durante los escaneos, mantener `logback` en `1.4.14` en el frontend por compatibilidad binaria con Spring Boot 3.2.5 (`LoggerContext.getConfigurationLock()`), y deshabilitar SSL obligatorio en el cliente de MariaDB 11 dentro de la red interna de CI.
>
> Esta práctica me permitió comprender que un pipeline de Integración Continua moderno no solo compila código, sino que actúa como una barrera automatizada de calidad y seguridad donde las pruebas unitarias con aserciones sólidas, la cobertura verificable, el análisis estático con SonarQube, la gestión de vulnerabilidades con Trivy y la validación automatizada de base de datos trabajan en conjunto para garantizar entregas confiables. Muchas gracias."
