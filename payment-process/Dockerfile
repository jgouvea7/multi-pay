FROM eclipse-temurin:24-jdk

WORKDIR /payment-process

COPY target/*.jar app.jar

EXPOSE 8000

CMD ["java", "-jar", "app.jar"]