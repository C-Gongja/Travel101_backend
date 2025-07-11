FROM openjdk:21-jdk
RUN groupadd -r spring && useradd -r -g spring spring
USER spring
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} sharavel-be-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/sharavel-be-0.0.1-SNAPSHOT.jar"]