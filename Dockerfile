# ── Stage 1: Build com Maven ──────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copia pom.xml e libs/ primeiro (camada de cache — só re-baixa se pom mudar)
COPY pom.xml .
COPY libs/ libs/

# Baixa dependências antecipadamente
RUN mvn dependency:go-offline -DskipTests --no-transfer-progress 2>/dev/null || true

# Copia o código-fonte e compila
COPY src/ src/
RUN mvn clean package -DskipTests --no-transfer-progress

# ── Stage 2: Runtime leve ─────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copia o fat JAR gerado pelo Spring Boot
COPY --from=build /app/target/pega-1.0.0.jar app.jar

EXPOSE 8080

# Flags JVM:
#   -Xmx450m              → limita memória para o plano gratuito
#   -Xms200m              → inicia com menos memória
#   -Djava.awt.headless   → sem GUI (container sem display)
#   -Djava.rmi.server.*   → JADE RMI funciona corretamente no container
ENTRYPOINT ["java", \
  "-Xmx450m", \
  "-Xms200m", \
  "-Djava.awt.headless=true", \
  "-Djava.rmi.server.hostname=127.0.0.1", \
  "-Djava.rmi.server.useLocalHostname=true", \
  "-jar", "app.jar"]
