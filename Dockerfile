FROM openjdk:17-jdk
MAINTAINER mufin.live
EXPOSE 8080
COPY target/bukkitgetter-1.0-jar-with-dependencies.jar bukkitgetter-1.0-jar-with-dependencies.jar
ENTRYPOINT ["java","-jar","bukkitgetter-1.0-jar-with-dependencies.jar"]