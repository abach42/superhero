#!/bin/bash
# to be able to use https, we do need this and the storepass variable in .env

keystore_file="abach42.superhero.p12"
path="src/main/resources/.certs"

# Check if the keystore file exists then stop
if [ -f "$path/$keystore_file" ]; then
    echo " 🟢 keystore file already exists."
    exit 1
fi


# Determine the path for keytool depending on OS
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    # Linux
    keytool_path=$(which keytool)
elif [[ "$OSTYPE" == "msys" || "$OSTYPE" == "mingw"* ]]; then
    # Windows (MinGW)
    keytool_path="$JAVA_HOME/bin/keytool.exe"
else
    echo " ⛔ Unsupported OS: Cannot locate keytool."
    exit 1
fi

echo $keytool_path

# Check if keytool is found
if [ -f "\"$keytool_path\"" ]; then
    echo " ⛔ Error: keytool not found. Make sure JAVA_HOME is set correctly."
    exit 1
fi

echo "The keystore file '$keystore_file' does not exist. Creating a new one..."

# Generate a new keystore using keytool command
"$keytool_path" -genkeypair \
        -alias abach42 \
        -keyalg RSA \
        -keysize 2048 \
        -storetype PKCS12 \
        -keystore $path/$keystore_file \
        -validity 365 \
        -storepass onlyForLocalhost123

# Check if the keystore file exists 
if [ -f "$path/$keystore_file" ]; then
    echo " ✅ New keystore '$path/$keystore_file' created."
else
    echo " ⛔ Error: '$path/$keystore_file' not created."
    exit 1
fi