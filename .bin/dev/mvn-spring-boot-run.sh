#!/bin/bash

./.bin/authorization/provide-env.sh
./.bin/authorization/generate-tls-key.sh
./.bin/authorization/generate-auth-keys.sh

echo "--------------------------------------------------"
echo " 🥫 Start Database"
echo "--------------------------------------------------"

# starts just database container and then application by maven to be able to code hotswap
export $(cat .env | xargs)
cd ./src/main/resources/db/oci/
docker-compose up -d

echo "--------------------------------------------------"
echo " 🌱 Start Spring Boot application"
echo "--------------------------------------------------"

cd -
mvn clean spring-boot:run