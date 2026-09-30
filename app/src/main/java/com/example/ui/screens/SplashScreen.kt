package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
  onFinish: () -> Unit
) {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.94f,
    targetValue = 1.06f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "scale"
  )

  LaunchedEffect(Unit) {
    delay(2000)
    onFinish()
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(CyberBg, Color(0xFF0D1424), CyberBg)
        )
      ),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.padding(24.dp)
    ) {
      // Glowing Neon Logo
      Box(
        modifier = Modifier
          .size(110.dp)
          .scale(pulseScale)
          .clip(CircleShape)
          .background(
            Brush.sweepGradient(
              listOf(NeonCyan, NeonPurple, NeonPink, NeonCyan)
            )
          )
          .border(2.dp, NeonCyan, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .size(98.dp)
            .clip(CircleShape)
            .background(CyberBg),
          contentAlignment = Alignment.Center
        ) {
          Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.SportsEsports,
              contentDescription = "Gaming Icon",
              tint = NeonCyan,
              modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
              imageVector = Icons.Default.MenuBook,
              contentDescription = "Study Icon",
              tint = NeonPurple,
              modifier = Modifier.size(32.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(28.dp))

      Text(
        text = "ALEX'S ROOM",
        color = TextPrimary,
        fontSize = 32.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 2.sp
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Study • Chat • Play • Level Up",
        color = NeonCyan,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 1.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Powered by StudyVerse",
        color = TextMuted,
        fontSize = 12.sp
      )

      Spacer(modifier = Modifier.height(48.dp))

      CircularProgressIndicator(
        modifier = Modifier.size(32.dp),
        color = NeonCyan,
        strokeWidth = 3.dp
      )
    }
  }
}
