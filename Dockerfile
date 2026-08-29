# syntax=docker/dockerfile:1

#   ===========    Stage 1: Build/Extract Layers    ===========
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /workspace

# copy dependency layer
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -ntp dependency:go-offline

# copy lombok.config + source
COPY lombok.config ./
COPY src/ src/
RUN ./mvnw -B -ntp clean package -DskipTests

# split into layers to enable cacheable copying
RUN cp target/*.jar orderflow.jar \
    && java -Djarmode=tools -jar orderflow.jar extract --layers --destination extracted

#   ===========    Stage 2: Runtime    ===========
FROM eclipse-temurin:21-jre-jammy AS runtime

# JRE base does not ship an HTTP client so `curl` is needed for `HEALTHCHECK`
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# run as unprivileged user
RUN groupadd --system spring  \
    && useradd --system --gid spring --no-create-home spring
WORKDIR /app

# copy layers with most frequent changes last
COPY --from=builder --chown=spring:spring /workspace/extracted/dependencies/ ./
COPY --from=builder --chown=spring:spring /workspace/extracted/spring-boot-loader/ ./
COPY --from=builder --chown=spring:spring /workspace/extracted/snapshot-dependencies/ ./
COPY --from=builder --chown=spring:spring /workspace/extracted/application/ ./

USER spring
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -fsS http://localhost:8080/actuator/health/liveness || exit 1

ENTRYPOINT ["java", "-jar", "orderflow.jar"]
