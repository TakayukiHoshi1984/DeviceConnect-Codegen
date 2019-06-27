#!/bin/sh -x

# プロファイル定義ファイル
SPEC="./sample-profile-specs/swagger-files"

# テンプレート
TEMPLATE_DIR="./templates/gotapiNodePlugin"

# スケルトンコード出力先
# NOTE: フォルダ名の先頭に node-gotapi-plugin- を付けること.
OUTPUT_DIR="./output/NodeJS/node-gotapi-plugin-sample"

# node-gotapiプラグインの表示名
DISPLAY_NAME="MyPlugin"

# スケルトンコード生成ツールのバイナリ
JAR_FILE="../bin/deviceconnect-codegen.jar"

ARGS="--input-spec $SPEC  --lang gotapiNodePlugin  --template-dir $TEMPLATE_DIR  --display-name $DISPLAY_NAME  --output $OUTPUT_DIR"

java -Dfile.encoding=UTF-8 -jar $JAR_FILE $ARGS
