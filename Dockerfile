# syntax=docker/dockerfile:1

#   ===========    Base/Dependencies Stage    ===========
FROM eclipse-temurin:21-jdk-jammy AS base
WORKDIR /orderflow

# copy dependency layer
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -ntp dependency:go-offline


#   ===========    Development Stage    ===========
FROM base AS development
# copy lombok.config + source
COPY lombok.config ./
COPY src/ src/
# run app in `local` spring profile as default
CMD ["./mvnw", "spring-boot:run"]


#   ===========    Test Stage    ===========
FROM base AS test
COPY lombok.config ./
COPY src/ src/
CMD ["./mvnw", "-B", "-ntp", "-DexcludedGroups=testcontainers", "clean", "verify"]


#   ===========    Extract Layers/Build for Production   ===========
FROM base AS builder
# copy lombok.config + source
COPY lombok.config ./
COPY src/ src/
RUN ./mvnw -B -ntp clean package -DskipTests

# split into layers to enable cacheable copying
RUN cp target/*.jar orderflow.jar \
    && java -Djarmode=tools -jar orderflow.jar extract --layers --destination extracted


#   ===========    Production Stage    ===========
FROM eclipse-temurin:21-jre-jammy AS production

# `curl` is needed for `HEALTHCHECK` but JRE base does not ship an HTTP client
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# run as unprivileged user
RUN groupadd --system spring  \
    && useradd --system --gid spring --no-create-home spring
WORKDIR /app

# copy layers with most frequent changes last
COPY --from=builder --chown=spring:spring /orderflow/extracted/dependencies/ ./
COPY --from=builder --chown=spring:spring /orderflow/extracted/spring-boot-loader/ ./
COPY --from=builder --chown=spring:spring /orderflow/extracted/snapshot-dependencies/ ./
COPY --from=builder --chown=spring:spring /orderflow/extracted/application/ ./

USER spring
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -fsS http://localhost:8080/actuator/health/liveness || exit 1

ENTRYPOINT ["java", "-jar", "orderflow.jar"]
