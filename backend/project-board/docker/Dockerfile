FROM openjdk:8-jdk-alpine

# Volume for JBoss files
VOLUME /tmp

# Token is replaced by gradle
EXPOSE @serverPort@

# Token is replaced by gradle
ARG JAR_FILE=@jarName@

# Copy jar archive
COPY ${JAR_FILE} project-board.jar

# Copy all property files to /config/ for Spring
COPY application*.properties /config/

# Set entrypoint to execute jar and set Spring config location to /config/
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-Dspring.config.location=/config/", "-jar", "/project-board.jar"]
