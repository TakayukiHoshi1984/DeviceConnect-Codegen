#!/bin/sh -x

# プロファイル定義ファイル
SPEC="../../samples/sample-profile-specs/swagger-files"

# スケルトンコード生成ツールのバイナリ
JAR_FILE="../../bin/deviceconnect-codegen.jar"

SCRIPT_DIR=$(cd $(dirname $0); pwd)

DATETIME=`date +%Y%m%d_%H%M%S`

OUTPUT="$SCRIPT_DIR/$DATETIME"

ARGS="--input-spec $SPEC --lang deviceConnectAndroidPlugin --template-dir templates/deviceConnectAndroidPlugin  --package-name com.mydomain.myplugin  --connection-type binder  --display-name MyPlugin  --output $OUTPUT"

mkdir -p $OUTPUT
chmod u-w $OUTPUT

java -Dfile.encoding=UTF-8 -jar $JAR_FILE $ARGS

sudo rm -rf $OUTPUT