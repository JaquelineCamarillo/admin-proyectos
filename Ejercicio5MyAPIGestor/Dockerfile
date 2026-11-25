# =====================================================================
# Dockerfile Backend - Ejercicio5MyAPIGestor (Spring Boot 2.7 + Java 21)
# Optimizado multi-stage build: eclipse-temurin:21-jre-alpine
# =====================================================================
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="Juana Jaqueline Camarillo Olaez"
LABEL description="Backend REST Gestor de Proyectos"

WORKDIR /app

# Crear usuario no privilegiado para seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copiar el artefacto generado
COPY target/Ejercicio5MyAPIGestor-0.0.1-SNAPSHOT.jar app.jar

RUN chown -R appuser:appgroup /app
USER appuser

ENV PORT=8081
EXPOSE 8081

# Parámetros JVM optimizados para contenedores ligeros (cgroups)
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
