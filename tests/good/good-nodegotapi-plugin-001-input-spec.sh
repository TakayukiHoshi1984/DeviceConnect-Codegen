#!/bin/sh -x

java -jar ../../bin/deviceconnect-codegen.jar \
     --lang       gotapiNodePlugin \
     --template-dir   templates/gotapiNodePlugin \
     --input-spec profile-specs/swagger.json \
     --output     output/nodegotapi-plugin-001