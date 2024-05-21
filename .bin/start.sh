#!/bin/bash

./.bin/authorization/provide-env.sh
./.bin/authorization/generate-tls-key.sh
./.bin/authorization/generate-auth-keys.sh

# starting application, code changes would be ignored
# 1. building image of actual code state
# 2. staring oci network database and java

echo "--------------------------------------------------"
echo " 🖼️ Build docker image of Spring Boot application"
echo "--------------------------------------------------"

mvn -DskipTests spring-boot:build-image

echo "--------------------------------------------------"
echo " 🐳 Start docker network"
echo "--------------------------------------------------"

docker compose up