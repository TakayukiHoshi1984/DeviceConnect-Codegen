#!/bin/sh -x

java -jar ../../bin/deviceconnect-codegen.jar \
     --lang           deviceConnectAndroidPlugin \
     --template-dir   templates/deviceConnectAndroidPlugin \
     --package-name   com.mydomain.testplugin004 \
     --display-name   Test004 \
     --input-spec     profile-specs/swagger-files-multiple \
     --output         output/android-plugin-004 \
     --config configs/android-plugin.json