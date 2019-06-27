#!/bin/sh -x

java -jar ../../bin/deviceconnect-codegen.jar \
     --lang           deviceConnectHtmlDocs \
     --template-dir templates/deviceConnectHtmlDocs \
     --input-spec profile-specs/swagger-files \
     --output         output/html-docs-001