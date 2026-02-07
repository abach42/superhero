#!/bin/bash

# Function to get the appropriate Maven command
get_maven_cmd() {
    # Check if mvnd is available
    if command -v mvn >/dev/null 2>&1; then
        echo "mvn"
        return 0
    fi

    # Default fallback
    echo "./mvnw"
    return 0
}

# Export as environment variable
export mvn=$(get_maven_cmd)

echo " 📦 Builder: $mvn"
echo " ☕ Java: $JAVA_HOME"