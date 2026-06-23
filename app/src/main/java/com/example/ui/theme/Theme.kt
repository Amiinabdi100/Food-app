package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = BentoDarkPrimary,
    onPrimary = BentoDarkOnPrimary,
    primaryContainer = BentoDarkPrimaryContainer,
    onPrimaryContainer = BentoDarkOnPrimaryContainer,
    secondary = BentoDarkSecondary,
    onSecondary = BentoDarkOnSecondary,
    secondaryContainer = BentoDarkSecondaryContainer,
    onSecondaryContainer = BentoDarkOnSecondaryContainer,
    tertiaryContainer = BentoDarkTertiaryContainer,
    onTertiaryContainer = BentoDarkOnTertiaryContainer,
    background = BentoDarkBackground,
    onBackground = BentoDarkOnBackground,
    surface = BentoDarkSurface,
    onSurface = BentoDarkOnSurface,
    surfaceVariant = BentoDarkSecondaryContainer, // Maps to main card
    onSurfaceVariant = BentoDarkOnSecondaryContainer,
    outline = BentoDarkOutline,
    outlineVariant = BentoDarkOutlineVariant,
    errorContainer = BentoDarkQuaternaryContainer,
    onErrorContainer = BentoDarkOnQuaternaryContainer
  )

private val LightColorScheme =
  lightColorScheme(
    primary = BentoPrimary,
    onPrimary = BentoOnPrimary,
    primaryContainer = BentoPrimaryContainer,
    onPrimaryContainer = BentoOnPrimaryContainer,
    secondary = BentoSecondary,
    onSecondary = BentoOnSecondary,
    secondaryContainer = BentoSecondaryContainer,
    onSecondaryContainer = BentoOnSecondaryContainer,
    tertiaryContainer = BentoTertiaryContainer,
    onTertiaryContainer = BentoOnTertiaryContainer,
    background = BentoBackground,
    onBackground = BentoOnBackground,
    surface = BentoSurface,
    onSurface = BentoOnSurface,
    surfaceVariant = BentoSecondaryContainer, // Maps to main card
    onSurfaceVariant = BentoOnSecondaryContainer,
    outline = BentoOutline,
    outlineVariant = BentoOutlineVariant,
    errorContainer = BentoQuaternaryContainer,
    onErrorContainer = BentoOnQuaternaryContainer
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disabling dynamic colors by default so Bento theme stands out
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
