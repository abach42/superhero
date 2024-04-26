#!/bin/bash

cp .env.dist .env
export $(cat .env | xargs)
cd ./src/main/resources/db/oci/
docker-compose up -d
cd -
mvn clean spring-boot:run