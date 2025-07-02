#!/bin/sh
# Run test with optional filter parameters
# example parameters
# referring @Tag("unit): -Dgroups=unit
# -Dtest=MyClassTest
# -Dtest=MyClassTest#testMyMethod
# -Dtest='MyClassTest$NestedClassTest'

# Navigate to the directory containing the mvnw file
cd "$(dirname "$0")/.." || exit

. "$(dirname "$0")/init/maven-cmd.sh"

$mvn clean test -Dparallel=all -DperCoreThreadCount=false -DthreadCount=28 -Djdk.net.URLClassPath.disableClassPathURLCheck=true "$@"