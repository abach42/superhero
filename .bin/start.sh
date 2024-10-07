#!/bin/bash

./.bin/authorization/provide-env.sh
./.bin/authorization/generate-tls-key.sh
./.bin/authorization/generate-auth-keys.sh

# Determine the operating system
if [ "$(uname)" == "Darwin" ] || [ "$(uname)" == "Linux" ]; then
    # macOS or Linux
    MAVEN_CMD="./mvnw -DskipTests spring-boot:build-image"
else
    # Windows
    MAVEN_CMD="mvnw.cmd -DskipTests spring-boot:build-image"
fi

# Starting application; code changes would be ignored
# 1. Build image of actual code state
# 2. Start OCI network database and Java

echo "--------------------------------------------------"
echo " 🖼️ Build docker image of Spring Boot application"
echo "--------------------------------------------------"

$MAVEN_CMD

echo "--------------------------------------------------"
echo " 🐳 Start docker network"
echo "--------------------------------------------------"

docker compose up