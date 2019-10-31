#!/bin/sh -x

# プロファイル定義ファイル
SPEC="./sample-profile-specs/swagger.json"

# テンプレート
TEMPLATE_DIR="./templates/deviceConnectAndroidPlugin"

# スケルトンコード出力先
OUTPUT_DIR="./output/Android/MyPlugin"

# Androidプラグインのパッケージ名
PACKAGE_NAME="com.mydomain.myplugin"

# Androidプラグインの連携タイプ
CONNECTION_TYPE="binder"

# Androidプラグインの表示名
DISPLAY_NAME="MyPlugin"

# スケルトンコード生成ツールのバイナリ
JAR_FILE="../bin/deviceconnect-codegen.jar"

ARGS="--overwrite --input-spec $SPEC  --lang deviceConnectAndroidPlugin --config configs/android-plugin.json --template-dir $TEMPLATE_DIR --package-name $PACKAGE_NAME  --connection-type $CONNECTION_TYPE  --display-name $DISPLAY_NAME  --output $OUTPUT_DIR"

java -Dfile.encoding=UTF-8 -jar $JAR_FILE $ARGS
