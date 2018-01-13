#!/bin/bash

cd ..
mvn clean install
mv target/*.jar docker/app.jar
cd docker
docker build -t trondheimdc/submit-backend .
docker push trondheimdc/submit-backend
rm -rf app.jar
