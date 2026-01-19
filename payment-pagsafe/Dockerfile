FROM eclipse-temurin:24-jdk

WORKDIR /payment-pagsafe

COPY target/*.jar app.jar

EXPOSE 8002

CMD ["java", "-jar", "app.jar"]