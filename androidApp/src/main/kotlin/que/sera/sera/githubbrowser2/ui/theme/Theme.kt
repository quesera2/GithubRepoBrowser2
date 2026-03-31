package que.sera.sera.githubbrowser2.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Blue40,
    onPrimary = Ink99,
    primaryContainer = Blue90,
    onPrimaryContainer = Blue10,
    secondary = Slate40,
    onSecondary = Ink99,
    secondaryContainer = Slate90,
    onSecondaryContainer = Slate10,
    tertiary = Gold40,
    onTertiary = Gold10,
    tertiaryContainer = Gold40,
    onTertiaryContainer = Ink10,
    error = Red40,
    onError = Ink99,
    errorContainer = Red90,
    onErrorContainer = Red10,
    background = Ink95,       // 画面背景 #f2f4f8
    onBackground = Ink10,
    surface = Ink99,          // カード背景 #ffffff
    onSurface = Ink10,
    surfaceVariant = InkVar90, // 検索バー #e4e7ef
    onSurfaceVariant = Slate40,
    outline = InkVar60,        // 目立つボーダー #b0b8c4
    outlineVariant = InkVar80, // カードボーダー #e2e5ec
    inverseSurface = Ink20,
    inverseOnSurface = Ink90,
    inversePrimary = Blue80,
)

private val DarkColorScheme = darkColorScheme(
    primary = Blue80,
    onPrimary = Blue20,
    primaryContainer = Blue30,
    onPrimaryContainer = Blue90,
    secondary = Slate80,
    onSecondary = Slate10,
    secondaryContainer = Slate20,
    onSecondaryContainer = Slate90,
    tertiary = Gold80,
    onTertiary = Gold10,
    tertiaryContainer = Gold30,
    onTertiaryContainer = Gold80,
    error = Red80,
    onError = Red20,
    errorContainer = Red30,
    onErrorContainer = Red90,
    background = InkVar10,    // 画面背景 #111318
    onBackground = Ink90,
    surface = Ink20,          // カード背景 #1c1f26
    onSurface = Ink90,
    surfaceVariant = Slate20, // 検索バー #22252e
    onSurfaceVariant = Slate80,
    outline = InkVar30,       // 目立つボーダー #44474e
    outlineVariant = InkVar20, // カードボーダー #2c303a
    inverseSurface = Ink95,
    inverseOnSurface = Ink10,
    inversePrimary = Blue40,
)

@Composable
fun GitHubBrowserTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content,
    )
}