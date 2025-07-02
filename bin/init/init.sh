#!/bin/bash
# Script to set up environment variables and determine Maven command

# Get the directory of this script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Navigate to the root project directory (two levels up)
PROJECT_ROOT="$SCRIPT_DIR/../.."

# documentation purpose, enable to use asymmetric key, please see README.md
# 
# echo "--------------------------------------------------"
# echo " 🔐 Building auth keys"
# echo "--------------------------------------------------"
# 
# "$PROJECT_ROOT/bin/init/generate-auth-keys.sh"
# 
# echo "--------------------------------------------------"
# echo " ✔️  Building auth keys"
# echo "--------------------------------------------------"
# 

echo "--------------------------------------------------"
echo " 🗝️  Building TLS key"
echo "--------------------------------------------------"

"$PROJECT_ROOT/bin/init/generate-tls-key.sh"

echo "--------------------------------------------------"
echo " ✔️  Building TLS key"
echo "--------------------------------------------------"