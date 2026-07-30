package com.half.wowsca.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Primary = Color(0xFF31628D)
private val OnPrimary = Color(0xFFFFFFFF)
private val PrimaryContainer = Color(0xFFCFE5FF)
private val OnPrimaryContainer = Color(0xFF001D34)
private val Secondary = Color(0xFF526070)
private val OnSecondary = Color(0xFFFFFFFF)
private val SecondaryContainer = Color(0xFFD5E4F7)
private val OnSecondaryContainer = Color(0xFF0E1D2A)
private val Tertiary = Color(0xFF845416)
private val OnTertiary = Color(0xFFFFFFFF)
private val TertiaryContainer = Color(0xFFFFDCBB)
private val OnTertiaryContainer = Color(0xFF2C1700)
private val Error = Color(0xFFBA1A1A)
private val OnError = Color(0xFFFFFFFF)
private val ErrorContainer = Color(0xFFFFDAD6)
private val OnErrorContainer = Color(0xFF410002)
private val Background = Color(0xFFF8F9FF)
private val OnBackground = Color(0xFF191C20)
private val Surface = Color(0xFFF8F9FF)
private val OnSurface = Color(0xFF191C20)
private val SurfaceVariant = Color(0xFFDEE3EB)
private val OnSurfaceVariant = Color(0xFF42474E)
private val Outline = Color(0xFF72777F)
private val OutlineVariant = Color(0xFFC2C7CF)

private val DarkPrimary = Color(0xFF9ECAFF)
private val DarkOnPrimary = Color(0xFF003354)
private val DarkPrimaryContainer = Color(0xFF154A73)
private val DarkOnPrimaryContainer = Color(0xFFCFE5FF)
private val DarkSecondary = Color(0xFFBAC8DB)
private val DarkOnSecondary = Color(0xFF243240)
private val DarkSecondaryContainer = Color(0xFF3A4857)
private val DarkOnSecondaryContainer = Color(0xFFD5E4F7)
private val DarkTertiary = Color(0xFFFFB86F)
private val DarkOnTertiary = Color(0xFF472A00)
private val DarkTertiaryContainer = Color(0xFF663E00)
private val DarkOnTertiaryContainer = Color(0xFFFFDCBB)
private val DarkBackground = Color(0xFF111318)
private val DarkOnBackground = Color(0xFFE1E2E8)
private val DarkSurface = Color(0xFF111318)
private val DarkOnSurface = Color(0xFFE1E2E8)
private val DarkSurfaceVariant = Color(0xFF42474E)
private val DarkOnSurfaceVariant = Color(0xFFC2C7CF)
private val DarkOutline = Color(0xFF8C9199)
private val DarkOutlineVariant = Color(0xFF42474E)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
    outlineVariant = OutlineVariant,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        content = content
    )
}
