#!/bin/sh -x

# プロファイル定義ファイル
SPEC="../../samples/sample-profile-specs/swagger-files"

# スケルトンコード生成ツールのバイナリ
JAR_FILE="../../bin/deviceconnect-codegen.jar"

SCRIPT_DIR=$(cd $(dirname $0); pwd)

DATETIME=`date +%Y%m%d_%H%M%S_%3N`

OUTPUT="$SCRIPT_DIR/$DATETIME"

ARGS="--input-spec $SPEC --lang deviceConnectAndroidPlugin --template-dir templates/deviceConnectAndroidPlugin  --package-name com.mydomain.myplugin  --connection-type binder  --display-name MyPlugin  --output $OUTPUT"

mkdir -p $OUTPUT
touch $OUTPUT/.placeholder

java -Dfile.encoding=UTF-8 -jar $JAR_FILE $ARGS

rm -rf $OUTPUT