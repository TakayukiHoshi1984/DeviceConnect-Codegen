:: スケルトンコード種別: HTMLアプリ
set LANG=deviceConnectHtmlApp

:: プロファイル定義ファイル
set SPEC=.\sample-profile-specs\swagger.json

:: スケルトンコード出力先
set OUTPUT_DIR=.\output\html\MyApp

:: テンプレート
set TEMPLATE_DIR="./templates/deviceConnectHtmlApp"

:: node-gotapiプラグインの表示名
set DISPLAY_NAME=MyApp

:: スケルトンコード生成ツールのバイナリ
set JAR_FILE=..\bin\deviceconnect-codegen.jar

java -Dfile.encoding=UTF-8 -jar %JAR_FILE% --input-spec %SPEC% --template-dir %TEMPLATE_DIR% --lang %LANG%  --display-name %DISPLAY_NAME%  --output %OUTPUT_DIR%
