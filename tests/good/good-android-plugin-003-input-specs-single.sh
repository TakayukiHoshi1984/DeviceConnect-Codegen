#!/bin/sh -x

java -jar ../../bin/deviceconnect-codegen.jar \
     --lang           deviceConnectAndroidPlugin \
     --template-dir templates/deviceConnectAndroidPlugin \
     --package-name   com.mydomain.testplugin003 \
     --display-name Test003 \
     --input-spec-dir profile-specs/swagger-files \
     --output         output/android-plugin-003
