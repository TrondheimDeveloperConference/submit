#!/bin/bash

mvn clean install
mv target/*.jar target/app.jar
docker build -t trondheimdc/submit-backend docker
#docker push trondheimdc/submit-backend
