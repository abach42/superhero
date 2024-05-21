#!/bin/bash

# delete database container, all records will be lost, recreation is provided
export $(cat .env | xargs)
cd ./src/main/resources/db/oci/
docker compose stop
docker compose rm postgres-db