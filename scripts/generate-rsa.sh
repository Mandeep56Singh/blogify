#!/bin/bash

set -e # exit on error

# creating directory for rsa keys
mkdir -p src/main/resources/certs

# go in directory
cd src/main/resources/certs

# generating key pair
openssl genrsa -out keypair.pem 2048

# Extract public key from key pair
openssl rsa -in keypair.pem -pubout -out public_key.pem

# Extract private key from key pair and make it java friendly
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out private_key.pem

# Clean up the intermediate keypair
rm keypair.pem


echo "RSA keys generated successfully"
