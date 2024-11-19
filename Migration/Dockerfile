FROM openjdk:17-jdk-alpine

COPY target/Migration-1.0-SNAPSHOT.jar .
EXPOSE 8082
ENTRYPOINT ["java","-jar","/Migration-1.0-SNAPSHOT.jar"]