#!/bin/sh
# Define file names for asymmetric key, please see README.md
private_key="private.pem"
public_key="public.pem"

# Define directory path
path="src/main/resources/.certs"

# Check if private.pem and public.pem exist
if [ ! -f "$path/$private_key" ] || [ ! -f "$path/$public_key" ]; then
    echo "One or both key files are missing. Creating a new key pair..."

    # Creating a new key pair with SHA encryption
    openssl genrsa -out "$path/$private_key" 2048
    openssl rsa -in "$path/$private_key" -pubout -out "$path/$public_key"

    echo "  ✅ New auth key pair created."
else
    echo "  🟢 Key auth files already exist."
fi
