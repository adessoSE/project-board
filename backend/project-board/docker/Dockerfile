FROM openjdk:8-jdk-alpine
VOLUME /tmp /config
EXPOSE 8080

# Token is replaced by gradle
ARG JAR_FILE=@jarName@

# Copy jar archive
COPY ${JAR_FILE} project-board.jar

# Copy .properties file to /config for Spring
COPY application-?.properties /config

# Set entrypoint to execute jar and set Spring config location to /config
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-Dspring.config.location=/config","-jar","/project-board.jar"]
