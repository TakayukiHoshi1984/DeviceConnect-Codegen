:: スケルトンコード種別: APIリファレンス
set LANG=deviceConnectHtmlDocs

:: プロファイル定義ファイル
set SPEC=..\standard-profile-specs

:: スケルトンコード出力先
set OUTPUT_DIR=.\output\html\Device_Connect_RESTful_API_Specification

:: テンプレート
set TEMPLATE_DIR="./templates/deviceConnectHtmlDocs"

:: node-gotapiプラグインの表示名
set DISPLAY_NAME=Device_Connect_RESTful_API_Specification

:: スケルトンコード生成ツールのバイナリ
set JAR_FILE=..\bin\deviceconnect-codegen.jar

java -Dfile.encoding=UTF-8 -jar %JAR_FILE% --input-spec-dir %SPEC% --template-dir %TEMPLATE_DIR% --lang %LANG%  --display-name %DISPLAY_NAME%  --output %OUTPUT_DIR%
