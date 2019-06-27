#!/bin/sh -x

# プロファイル定義ファイル
SPEC="./profile-specs/bad-swagger-files-104"

# スケルトンコード生成ツールのバイナリ
JAR_FILE="../../bin/deviceconnect-codegen.jar"

ARGS="--input-spec $SPEC --lang deviceConnectAndroidPlugin --template-dir templates/deviceConnectAndroidPlugin  --package-name com.mydomain.myplugin  --connection-type binder  --display-name MyPlugin  --output ./output/Android/MyPlugin"

java -Dfile.encoding=UTF-8 -jar $JAR_FILE $ARGS
