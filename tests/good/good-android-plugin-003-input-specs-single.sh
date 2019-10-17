#!/bin/sh -x

java -jar ../../bin/deviceconnect-codegen.jar \
     --lang           deviceConnectAndroidPlugin \
     --template-dir templates/deviceConnectAndroidPlugin \
     --package-name   com.mydomain.testplugin003 \
     --display-name Test003 \
     --input-spec profile-specs/swagger-files \
     --output         output/android-plugin-003 \
     --config configs/android-plugin.json
