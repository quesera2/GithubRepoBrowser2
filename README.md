# GitHub Repo Browser

GitHubのユーザー名を入力して、そのユーザーのリポジトリ一覧を表示するアプリです。
Kotlin Multiplatform で構築し、iOS・Android それぞれネイティブ UI で動作します。

## スクリーンショット

| Android | iOS |
|:---:|:---:|
| ![Android](docs/screenshots/android.png) | ![iOS](docs/screenshots/ios.png) |

## 技術スタック

| レイヤー | 技術 |
|---|---|
| 共通ロジック | Kotlin Multiplatform |
| 通信 | Ktor |
| JSON | kotlinx.serialization |
| 非同期 | kotlinx.coroutines |
| iOS Swift 連携 | KMP-NativeCoroutines + Combine |
| Android UI | Jetpack Compose |
| iOS UI | SwiftUI |
| 状態管理 | ViewModel (KMP) + StateFlow |

## プロジェクト構成

```
├── shared/          # KMP共通コード（API・モデル・ViewModel）
├── composeApp/      # Android アプリ（Jetpack Compose）
└── iosApp/          # iOS アプリ（SwiftUI）
```

## ビルド方法

### Android

```shell
./gradlew :composeApp:assembleDebug
```

### iOS

`iosApp/iosApp.xcodeproj` を Xcode で開いて Run。
