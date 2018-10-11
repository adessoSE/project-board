FROM openjdk:8-jdk-alpine
VOLUME /tmp
EXPOSE 8081
ARG JAR_FILE=build/libs/project-board-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} project-board.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/project-board.jar"]
