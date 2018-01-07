FROM openjdk:8

ADD app.jar /
cmd java ${JAVA_OPTS} -Dserver.port=8081 -Djava.net.preferIPv4Stack=true -Dfile.encoding=UTF-8 -Djava.security.egd=file:///dev/urandom -jar /app.jar --spring.config.location=file:/submit-backend.properties
