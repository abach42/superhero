#!/bin/bash

echo "--------------------------------------------------"
echo " 🗝️ Building TLS key"
echo "--------------------------------------------------"

# to be able to use https, we do need this and the storepass variable in .env

keystore_file="abach42.superhero.p12"
path="src/main/resources/.certs"
env_file=".env"

# get env vars from .env
export $(cat .env | xargs)

if [ -f "$path/$keystore_file" ]  && [ -n "$STORE_PASS" ]; then
    echo "  ✅ Keystore '$path/$keystore_file' exists."
    echo "  ✅ STORE_PASS environment variable is set."
    exit 0
fi

# Check if the keystore file exists and delete
if [ -f "$path/$keystore_file" ]; then
    rm $path/$keystore_file
fi

# Check if STORE_PASS environment variable is set
if [ -z "$STORE_PASS" ]; then
    echo "  ⛔ Error: STORE_PASS environment variable is not set. Cannot generate keystore without storepass."
    exit 1
fi

echo "The keystore file '$keystore_file' does not exist. Creating a new one..."

# Generate a new keystore using keytool command
keytool -genkeypair \
        -alias abach42 \
        -keyalg RSA \
        -keysize 2048 \
        -storetype PKCS12 \
        -keystore $path/$keystore_file \
        -validity 365 \
        -storepass $STORE_PASS

# Check if the keystore file exists 
if [ -f "$path/$keystore_file" ]; then
    echo "  ✅ New keystore '$path/$keystore_file' created."
else
    echo "  ⛔ Error: '$path/$keystore_file' not created."
    exit 1
fi