#!/bin/sh -x

java -jar ../../bin/deviceconnect-codegen.jar \
     --lang                  deviceConnectAndroidPlugin \
     --template-dir templates/deviceConnectAndroidPlugin \
     --package-name com.mydomain.testplugin010 \
     --display-name Test009 \
     --input-spec        profile-specs/swagger-files-android-plugin-009/ \
     --output                output/android-plugin-010 \
     --gradle-plugin-version 3.3.2 \
     --config configs/android-plugin.json
