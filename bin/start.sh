#!/bin/bash

./bin/authorization/generate-tls-key.sh
./bin/authorization/generate-auth-keys.sh

# Starting application; code changes would be ignored
# 1. Build image of actual code state
# 2. Start OCI network database and Java

echo "--------------------------------------------------"
echo " 🖼️ Build docker image of Spring Boot application"
echo "--------------------------------------------------"

./mvnw clean -DskipTests -Pcontainer spring-boot:build-image

echo "--------------------------------------------------"
echo " 🐳 Start docker network"
echo "--------------------------------------------------"

docker compose up