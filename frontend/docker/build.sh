#!/bin/bash

cd ..
npm install && npm run build
mv dist docker/
cd docker
docker build -t trondheimdc/submit-frontend .
#docker push trondheimdc/submit-frontend
rm -rf dist