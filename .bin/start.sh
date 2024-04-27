#!/bin/bash

mvn -DskipTests spring-boot:build-image
docker-compose up