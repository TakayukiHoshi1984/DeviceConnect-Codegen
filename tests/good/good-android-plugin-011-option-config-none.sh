#!/bin/sh -x

java -jar ../../bin/deviceconnect-codegen.jar \
     --lang                  deviceConnectAndroidPlugin \
     --template-dir templates/deviceConnectAndroidPlugin \
     --package-name com.mydomain.testplugin011 \
     --display-name Test011 \
     --input-spec        profile-specs/swagger-files-android-plugin-009/ \
     --output                output/android-plugin-011 \
     --gradle-plugin-version 3.3.2
