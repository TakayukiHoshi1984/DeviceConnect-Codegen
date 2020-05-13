:: スケルトンコード種別: Androidプラグイン
set LANG=deviceConnectKotlinPlugin

:: プロファイル定義ファイル
set SPEC=.\sample-profile-specs\swagger.json

:: スケルトンコード出力先
set OUTPUT_DIR=.\output\Android\MyPluginForKotlin

:: テンプレート
set TEMPLATE_DIR="./templates/deviceConnectKotlinPlugin"

:: Androidプラグインのパッケージ名
set PACKAGE_NAME=com.mydomain.myplugin

:: Androidプラグインの表示名
set DISPLAY_NAME=MyPluginForKotlin

:: スケルトンコード生成ツールのバイナリ
set JAR_FILE=..\bin\deviceconnect-codegen.jar

java -Dfile.encoding=UTF-8 -jar %JAR_FILE% --config .\configs\android-plugin-kotlin.json --template-dir %TEMPLATE_DIR% --input-spec %SPEC%  --lang %LANG% --package-name %PACKAGE_NAME%  --display-name %DISPLAY_NAME%  --output %OUTPUT_DIR%
