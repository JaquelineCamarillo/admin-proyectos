# =====================================================================
# Dockerfile Frontend - Ejercicio5Cliente (Spring Boot 3.2 + Java 21)
# Optimizado multi-stage build: eclipse-temurin:21-jre-alpine
# =====================================================================
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="Juana Jaqueline Camarillo Olaez"
LABEL description="Frontend Web Gestor de Proyectos"

WORKDIR /app

# Crear usuario no privilegiado para seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copiar el artefacto generado
COPY target/Ejercicio5Cliente-0.0.1-SNAPSHOT.jar app.jar

RUN chown -R appuser:appgroup /app
USER appuser

ENV PORT=8080
EXPOSE 8080

# Parámetros JVM optimizados para contenedores ligeros (cgroups)
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
