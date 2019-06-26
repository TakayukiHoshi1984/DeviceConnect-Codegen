#!/bin/sh -x

java -jar ../../bin/deviceconnect-codegen.jar \
     --lang       deviceConnectEmulator \
     --template-dir   templates/deviceConnectEmulator \
     --input-spec profile-specs/swagger.json \
     --output     output/nodejs-emulator-001