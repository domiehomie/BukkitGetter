FROM openjdk:17-jdk-alpine
MAINTAINER mufin.live
COPY target/bukkitgetter-1.0-jar-with-dependencies.jar bukkitgetter-1.0-jar-with-dependencies.jar
ENTRYPOINT ["java","-jar","bukkitgetter-1.0-jar-with-dependencies.jar"]