FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY target/sicov-0.0.1-SNAPSHOT.jar app.jar

# EXPOSE es opcional, Render no lo usa pero no afecta
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
