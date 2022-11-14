#!/bin/bash
FROM adoptopenjdk/openjdk11
ARG JAR_FILE=build/libs/*.jarr
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]