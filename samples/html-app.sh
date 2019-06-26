#!/bin/sh -x

# プロファイル定義ファイル
SPEC="./sample-profile-specs/swagger.json"

# テンプレート
TEMPLATE_DIR="./templates/deviceConnectHtmlApp"

# スケルトンコード出力先
OUTPUT_DIR="./output/html/MyApp"

# HTMLアプリの表示名
DISPLAY_NAME="MyApp"

# スケルトンコード生成ツールのバイナリ
JAR_FILE="../bin/deviceconnect-codegen.jar"

ARGS="--input-spec $SPEC  --lang deviceConnectHtmlApp  --template-dir $TEMPLATE_DIR  --display-name $DISPLAY_NAME  --output $OUTPUT_DIR"

java -Dfile.encoding=UTF-8 -jar $JAR_FILE $ARGS
