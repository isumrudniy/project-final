FROM openjdk:17-jdk-alpine
MAINTAINER javarush.com
COPY target/jira-1.0.jar jira-1.0.jar
ENTRYPOINT ["java","-jar","/jira-1.0.jar"]