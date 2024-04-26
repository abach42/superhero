#!/bin/bash

export $(cat .env | xargs)
cd ./src/main/resources/db/oci/
docker-compose stop