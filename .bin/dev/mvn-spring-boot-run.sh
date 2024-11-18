#!/bin/bash

./.bin/authorization/provide-env.sh
./.bin/authorization/generate-tls-key.sh
./.bin/authorization/generate-auth-keys.sh

# Determine the operating system
if [ "$(uname)" == "Darwin" ] || [ "$(uname)" == "Linux" ]; then
    # macOS or Linux
    MAVEN_CMD="mvn clean spring-boot:run"
else
    # Windows
    MAVEN_CMD="./mvnw clean spring-boot:run"
fi

echo "--------------------------------------------------"
echo " 🐋 Start Database"
echo "--------------------------------------------------"

# Start just database container and then application by Maven to be able to code hotswap
export $(cat .env | xargs)
cd ./src/main/resources/db/oci/
docker compose up -d

echo "--------------------------------------------------"
echo " 🌱 Start Spring Boot application 'mvn clean spring-boot:run'"
echo "--------------------------------------------------"

cd -
$MAVEN_CMD