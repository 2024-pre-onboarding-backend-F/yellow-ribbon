FROM openjdk:17-alpine

ARG JAR_FILE=/build/libs/ribbon-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} /yellow-ribbon.jar

ENTRYPOINT ["java","-jar","-Dspring.profiles.active=secret", "/yellow-ribbon.jar"]