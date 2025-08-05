FROM openjdk:17-jdk-slim
COPY target/payment-system-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]