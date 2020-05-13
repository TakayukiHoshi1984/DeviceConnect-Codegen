#!/bin/sh -x

# プロファイル定義ファイル
SPEC="./sample-profile-specs/swagger.json"

# テンプレート
TEMPLATE_DIR="./templates/deviceConnectKotlinPlugin"

# スケルトンコード出力先
OUTPUT_DIR="./output/Android/MyPluginForKotlin"

# Androidプラグインのパッケージ名
PACKAGE_NAME="com.mydomain.myplugin"

# Androidプラグインの連携タイプ
CONNECTION_TYPE="binder"

# Androidプラグインの表示名
DISPLAY_NAME="MyPluginForKotlin"

# スケルトンコード生成ツールのバイナリ
JAR_FILE="../bin/deviceconnect-codegen.jar"

ARGS="--overwrite --input-spec $SPEC  --lang deviceConnectKotlinPlugin --config configs/android-plugin-kotlin.json --template-dir $TEMPLATE_DIR --package-name $PACKAGE_NAME  --connection-type $CONNECTION_TYPE  --display-name $DISPLAY_NAME  --output $OUTPUT_DIR"

java -Dfile.encoding=UTF-8 -jar $JAR_FILE $ARGS
