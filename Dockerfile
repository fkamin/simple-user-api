FROM openjdk:21
ARG JAR_FILE=target/simple-user-api-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} simple-user-api.jar
ENTRYPOINT ["java", "-jar", "simple-user-api.jar"]
EXPOSE 8080