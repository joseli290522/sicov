# Stage 1: Build the project
FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos el pom y el código fuente
COPY pom.xml .
COPY src ./src

# Construimos el JAR (omitimos tests para acelerar)
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copiamos el JAR compilado desde la etapa de build
COPY --from=build /app/target/sicov-0.0.1-SNAPSHOT.jar app.jar

# Puerto que usa Spring Boot
EXPOSE 8080

# Ejecutamos la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
