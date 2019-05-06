FROM openjdk:11-jre-slim

# Volume for JBoss files
VOLUME /tmp

# Volume for Apache Lucene Indexes
VOLUME /var/lucene

# External Port
EXPOSE 8180

# Token is replaced by gradle
ARG JAR_FILE=@JAR_NAME@

# Copy jar archive
COPY ${JAR_FILE} project-board.jar

# Copy all property files to /config/ for Spring
COPY *.properties /config/

# Copy the required SSL certificate
COPY publicCert.pem /tmp/

# Add the SSL certificate to the default Keystore
RUN $JAVA_HOME/bin/keytool -import -noprompt -storepass changeit -alias test-server -file /tmp/publicCert.pem -keystore $JAVA_HOME/lib/security/cacerts -trustcacerts

# Set entrypoint to execute jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-jar", "/project-board.jar"]
