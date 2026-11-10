FROM eclipse-temurin:25-jdk AS build

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod +x mvnw \
    && ./mvnw -B -DskipTests dependency:go-offline

COPY src/ src/

RUN ./mvnw -B -DskipTests package \
    && cp target/streamora-*.jar application.jar

FROM eclipse-temurin:25-jre

WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends pure-ftpd \
    && rm -rf /var/lib/apt/lists/* \
    && mkdir -p /data/pureftpd /data/liquidsoap /etc/pure-ftpd/passwd

COPY --from=build /workspace/application.jar /app/application.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/application.jar"]

