#!/bin/sh -x

# プロファイル定義ファイルのディレクトリ
SPEC="./sample-profile-specs/swagger.json"

# テンプレート
TEMPLATE_DIR="./templates/deviceConnectEmulator"

# スケルトンコード出力先
OUTPUT_DIR="./output/NodeJS/Emulator"

# Androidプラグインの表示名
DISPLAY_NAME="Device Connect Emulator"

# スケルトンコード生成ツールのバイナリ
JAR_FILE="../bin/deviceconnect-codegen.jar"

ARGS="--input-spec $SPEC  --lang deviceConnectEmulator  --template-dir $TEMPLATE_DIR  --display-name $DISPLAY_NAME  --output $OUTPUT_DIR"

java -Dfile.encoding=UTF-8 -jar $JAR_FILE $ARGS
