#!/bin/bash
# Script to run Maven with specified parameters

# Navigate to the directory containing the mvnw file
cd "$(dirname "$0")/../.." || exit

source ./bin/init/init.sh

. bin/init/maven-cmd.sh

echo "--------------------------------------------------------"
echo " 🪴  starting spring on default profile, skipping tests"
echo "--------------------------------------------------------"

# you can add -PmyProfile 
$mvn clean spring-boot:run \
    -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"