#!/bin/sh -x

java -jar ../../bin/deviceconnect-codegen.jar \
     --lang       deviceConnectAndroidPlugin \
     --template-dir templates/deviceConnectAndroidPlugin \
     --package-name com.mydomain.testplugin002 \
     --display-name Test002 \
     --input-spec profile-specs/swagger.yaml \
     --output     output/android-plugin-002 \
     --config configs/android-plugin.json
