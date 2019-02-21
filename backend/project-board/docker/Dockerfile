FROM openjdk:11

# Volume for JBoss files
VOLUME /tmp

# Volume for Apache Lucene Indexes
VOLUME /var/lucene

# Token is replaced by gradle
EXPOSE 8080

# Token is replaced by gradle
ARG JAR_FILE=@jarName@

# Copy jar archive
COPY ${JAR_FILE} project-board.jar

# Copy all property files to /config/ for Spring
COPY *.properties /config/

# Set entrypoint to execute jar and set Spring config location to /config/
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-Dspring.config.location=/config/", "-jar", "/project-board.jar"]
