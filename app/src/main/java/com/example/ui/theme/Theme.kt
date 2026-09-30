package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val DarkGamingColorScheme = darkColorScheme(
  primary = NeonCyan,
  onPrimary = CyberBg,
  primaryContainer = Color(0xFF0F3A4A),
  onPrimaryContainer = NeonCyan,
  secondary = NeonPurple,
  onSecondary = CyberBg,
  secondaryContainer = Color(0xFF2C164A),
  onSecondaryContainer = NeonPurple,
  tertiary = NeonEmerald,
  onTertiary = CyberBg,
  background = CyberBg,
  onBackground = TextPrimary,
  surface = CyberCard,
  onSurface = TextPrimary,
  surfaceVariant = CyberBgElevated,
  onSurfaceVariant = TextSecondary,
  outline = CyberCardBorder,
  outlineVariant = Color(0xFF1E293B)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  content: @Composable () -> Unit,
) {
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = CyberBg.toArgb()
        window.navigationBarColor = CyberBg.toArgb()
        val insetsController = WindowCompat.getInsetsController(window, view)
        insetsController.isAppearanceLightStatusBars = false
        insetsController.isAppearanceLightNavigationBars = false
      }
    }
  }

  MaterialTheme(
    colorScheme = DarkGamingColorScheme,
    typography = Typography,
    content = content
  )
}
