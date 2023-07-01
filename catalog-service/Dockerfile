FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/scala-2.13/catalog-service-assembly-0.1.0-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
