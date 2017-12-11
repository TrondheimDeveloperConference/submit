FROM openjdk:8

ADD target/app.jar /
cmd java -jar -Dserver.port=5000 -Djava.net.preferIPv4Stack=true -Dfile.encoding=UTF-8 -Djava.security.egd=file:///dev/urandom app.jar
