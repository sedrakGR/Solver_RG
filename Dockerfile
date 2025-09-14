##########
# Build stage: use Gradle to produce WAR (respects encoding + junit)
##########
FROM gradle:8.8-jdk17 AS build
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN gradle --no-daemon clean war

##########
# Stage to harvest Tomcat 9 + JRE 17 binaries
##########
FROM tomcat:9.0-jre17 AS tomcat

##########
# Final runtime stage: MongoDB 3.4 base with Tomcat 9 + JRE 11 copied in
##########
FROM mongo:3.4

## Default Mongo URL points to the in-container mongod
ENV MONGO_URL="mongodb://127.0.0.1:27017/yourdb"
ENV CATALINA_OPTS="-Dmongo.url=${MONGO_URL}"

## Copy Java and Tomcat from the tomcat image
# Eclipse Temurin images expose JAVA_HOME at /opt/java/openjdk
COPY --from=tomcat /opt/java/openjdk /opt/java/openjdk
COPY --from=tomcat /usr/local/tomcat /usr/local/tomcat
ENV JAVA_HOME="/opt/java/openjdk"
ENV PATH="$JAVA_HOME/bin:/usr/local/tomcat/bin:${PATH}"

## Deploy our app as ROOT.war and remove defaults
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /home/gradle/project/build/libs/ssrgt_solver.war /usr/local/tomcat/webapps/ROOT.war

# Data directory for MongoDB
RUN mkdir -p /data/db
VOLUME ["/data/db"]

# Add a simple entrypoint that starts mongod then Tomcat
COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

EXPOSE 8080
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]
