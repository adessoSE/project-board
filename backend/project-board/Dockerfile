FROM openjdk:8-jdk-alpine

# Volume for JBoss files
VOLUME /tmp

# Copy jar archive
COPY ./build/libs/project-board-0.0.1-SNAPSHOT.jar project-board.jar

# Copy all property files to /config/ for Spring
COPY docker/application*.properties /config/

# Set entrypoint to execute jar and set Spring config location to /config/
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-Dspring.config.location=/config/", "-jar", "/project-board.jar"]
