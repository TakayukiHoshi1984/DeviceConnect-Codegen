# DeviceConnect Codegen

## はじめに
DeviceConnect Codegenは、DeviceConnectシステム上で動作するプラグインおよびアプリケーションのスケルトンコードを生成するためのコマンドラインツールです。


## 開発環境
### 開発ツール
- Apache Maven 3.3.9+
- Python 2.x

### ビルド方法
DeviceConnectCodegenのルートディレクトリで下記のコマンドを実行すると、本ツールをビルドできます。

```
$ mvn package
```

ビルドを実行すると、本ツールのバイナリと配布用zipがそれぞれ下記の場所に出力されます。

|項目|出力先|
|:--|:--|
|バイナリ|DeviceConnectCodegen/bin/deviceconnect-codegen.jar|
|配布用zip|DeviceConnectCodegen/target/deviceconnect-codegen-project-X.Y.Z-dist.zip|

### バージョン更新
新しいバージョンを開発する場合は、以下のPython 2.x スクリプトで全体的にバージョン設定を書き換えること。

例: バージョン a.b.c に変更する場合

```
$ cd DeviceConnect-Codegen
$ python tools/version.py a.b.c
```

正常に変更された場合は、以下のログが標準出力される。

```
Changed: /(略)/DeviceConnect-Codegen/pom.xml
Changed: /(略)/DeviceConnect-Codegen/modules/deviceconnect-codegen/pom.xml
Changed: /(略)/DeviceConnect-Codegen/README.md
Completed
```

## 使用方法
作成したツールを使い、スケルトンコードを作成する手順は、[こちら](https://github.com/DeviceConnect/DeviceConnect-Codegen/blob/master/MANUAL.md)を参照してください。

## 参考リンク
- [Swagger](http://swagger.io/)
