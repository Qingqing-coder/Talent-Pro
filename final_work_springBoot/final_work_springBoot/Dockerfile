FROM bellsoft/liberica-openjdk-alpine:21.0.2-jre
WORKDIR /app
COPY target/IRMS_back-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]