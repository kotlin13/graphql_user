#!/bin/sh
./gradlew clean build
docker build -t user . && docker run -it -p 8888:8888 --env port=8888 user
