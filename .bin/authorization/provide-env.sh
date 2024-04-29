#!/bin/bash

echo "--------------------------------------------------"
echo " ⚙️ Provide env vars"
echo "--------------------------------------------------"

env_file=".env"
env_dist_file=".env.dist"

# check if copy of .env.dist to .env is necessary
if [ -f "$env_file" ]; then
    # get env vars from .env
    export $(cat .env | xargs)

    # check if necessary env vars by user prompt already exist
    if [ -n "$STORE_PASS" ] && [ -n "$SPRING_DATASOURCE_PASSWORD" ]; then
        echo "  ✅ STORE_PASS environment variable is set."
        echo "  ✅ SPRING_DATASOURCE_PASSWORD environment variable is set."
        exit 0
    fi
fi

# Check if .env.dist file exists
if [ -f "$env_dist_file" ]; then
    echo "The .env file does not exist. Copying from $env_dist_file..."

    # Copy .env.dist file to .env
    cp "$env_dist_file" "$env_file"

    echo ".env file created."
else
    echo "  ⛔ No env.dist file exists, env. not created"
    
fi

# Prompt user for STORE_PASS
read -p "  ❔ Enter STORE_PASS value (at least 6 chars): " STORE_PASS

echo "" >> "$env_file"
echo "STORE_PASS=$STORE_PASS" >> "$env_file"

# Prompt user for SPRING_DATASOURCE_PASSWORD
read -p "  ❔ Enter SPRING_DATASOURCE_PASSWORD value [default=db]: " SPRING_DATASOURCE_PASSWORD
SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD:-"db"}

# Write environment variable to .env file
echo "" >> "$env_file"
echo "SPRING_DATASOURCE_PASSWORD=$SPRING_DATASOURCE_PASSWORD" >> "$env_file"