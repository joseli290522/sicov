FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY target/sicov-0.0.1-SNAPSHOT.jar app.jar

# Render envía el puerto dinámicamente
ENV PORT=$PORT  

# Expón un puerto cualquiera, da igual — Render no usa EXPOSE 
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
