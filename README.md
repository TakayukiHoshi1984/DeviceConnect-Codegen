# DeviceConnect Codegen

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
$ cd DeviceConnect-Experiments/DeviceConnectCodegen
$ python tools/version.py a.b.c
```

正常に変更された場合は、以下のログが標準出力される。

```
Changed: /(略)/DeviceConnect-Experiments/DeviceConnectCodegen/pom.xml
Changed: /(略)/DeviceConnect-Experiments/DeviceConnectCodegen/modules/deviceconnect-codegen/pom.xml
Changed: /(略)/DeviceConnect-Experiments/DeviceConnectCodegen/README.md
Completed
```

## 参考リンク
- [Swagger](http://swagger.io/)