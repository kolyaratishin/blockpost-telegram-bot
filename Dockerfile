FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY src/main/resources/static/photos/ /photos/
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080