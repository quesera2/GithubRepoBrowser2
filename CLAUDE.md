# 概要

マルチモジュール KMP（Kotlin Multiplatform）の構成・アーキテクチャの研究用プロジェクト。

- ロジックは共有部分（Kotlin）
- 文字列リソースは `:feature:resource` モジュールに定義
  - Moko Resource が iOS 側に出力するときに `MR` 一つでまとめるため
  - モジュールごとで文字列を定義した場合は、`MR_` `MR__` のように分割される
- Android の UI は Jetpack Compose で作成
- iOS の UI は SwiftUI で作成

## 注意点

- gradle で `api()` は必要なタイミングのみ使用し、基本は `implementation()` を使う
- DI (metroを使用) のオブジェクトグラフ作成は `:shared` モジュールにて行う
   - 各モジュール内で `@ContributesTo(AppScope::class)` を使って合成することもできるが、DI の合成基点を用意する
   - ViewModelModule の `@ViewModelKey(XXX::class)` のクラス名は Provide では明記する必要がある

### テスト

```bash
# テスト
./gradlew allTests # ユニットテストはKMP部分だけなのでこれで網羅
```

- フレームワークは Kotest を用いて `DescribeSpec` で作成
- Flow のテストは Turbine を使う
- モックは使わずフェイクを実装する