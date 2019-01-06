FROM maven:3.6.0-jdk-8-alpine as MAVEN_TOOL_CHAIN

COPY pom.xml /tmp/
COPY src /tmp/src/
WORKDIR /tmp/
RUN mvn package

FROM openjdk:8-jdk-alpine

COPY --from=MAVEN_TOOL_CHAIN /tmp/target/submit*.jar /app.jar
CMD java ${JAVA_OPTS} -Dserver.port=8081 -Djava.net.preferIPv4Stack=true -Dfile.encoding=UTF-8 -Djava.security.egd=file:///dev/urandom -jar /app.jar --spring.config.location=file:/submit-backend.properties

#HEALTHCHECK --interval=1m --timeout=3s CMD wget --quiet --tries=1 --spider http://localhost:8080/wizard/ || exit 1
