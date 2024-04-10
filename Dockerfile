FROM amazoncorretto:17-alpine-jdk
MAINTAINER javarush.com
ARG JAR_FILE=target/*.jar
ENTRYPOINT ["java","-jar","/jira-1.0.jar"]