#!/bin/bash
# Script to run Maven with specified parameters

# Navigate to the directory containing the mvnw file
cd "$(dirname "$0")/../.." || exit

./bin/authorization/generate-auth-keys.sh
./bin/authorization/generate-tls-key.sh

echo "--------------------------------------------------"
echo " 🪴  starting spring on default profile, skipping tests"
echo "--------------------------------------------------"

# you can add -PmyProfile 
./mvnw clean spring-boot:run -Dskiptests \
-Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
