# DelesteRandomSelector
デレステの課題曲をよりスムーズに決定できます

THIS SOFTWARE DEPENDS ON APACHE LICENSE 2.0 BECAUSE THIS USED SOME LIBRARIES WHICH DEPENDS ON IT.

## 使用ライブラリ群 -Used Libraries-

- Jackson JSON read/write libraries (https://github.com/FasterXML/jackson)
	- And other child libraries
- jsoup Java HTML Parser (https://jsoup.org/)
- Twitter4J Java TwitterAPI wrapper (https://twitter4j.org/ja/)

## 使用方法
(v0.2.0時点の使用方法です)

1. 画面左側から、楽曲の条件を指定します！
2. 指定し終わったら、画面右上「楽曲取り込み」ボタンをクリック！
3. 最後に、画面右下「開始！」ボタンをクリック！課題曲を楽しみましょう！

## 設定ファイルについて

初回起動後に生成される設定ファイル(settings.json)で変更できる設定は以下の通りです

|設定項目|値|デフォルト|説明|
|:--:|:--:|:--:|:--:|
|`checkVersion`|`true`/`false`|`true`|アプリケーションのバージョンをチェックします|
|`checkLibaryUpdates`|`true`/`false`|`true`|楽曲ライブラリの更新を毎回チェックします|
|`windowWidth`|1以上の整数値|`640`|ウィンドウの横長さを指定します|
|`windowHeight`|1以上の整数値|`360`|ウィンドウの縦長さを指定します|
|`songLimit`|1以上の整数値|`3`|ランダム選曲する最大曲数を指定します|
|`saveScoreLog`|`true`/`false`|`false`|スコアデータを保存するかどうか指定します|
|`outputDebugSentences`|`true`/`false`|`false`|標準出力へログを流すかどうか指定します