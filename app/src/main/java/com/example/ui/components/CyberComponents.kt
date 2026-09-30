package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserStatus
import com.example.ui.theme.*
import com.example.viewmodel.ScreenTab

@Composable
fun CyberAvatar(
  avatarId: String,
  status: UserStatus? = null,
  size: Dp = 44.dp,
  showBorder: Boolean = true,
  onClick: (() -> Unit)? = null
) {
  val icon = when (avatarId) {
    "cyber_cat" -> Icons.Default.Pets
    "pixel_warrior" -> Icons.Default.SportsEsports
    "cosmic_girl" -> Icons.Default.AutoAwesome
    "robo_geek" -> Icons.Default.SmartToy
    "gamer_girl" -> Icons.Default.Headphones
    "cyber_skater" -> Icons.Default.Skateboarding
    else -> Icons.Default.AccountCircle
  }

  val bgBrush = Brush.linearGradient(
    colors = when (avatarId) {
      "cyber_cat" -> listOf(Color(0xFFF43F5E), Color(0xFFFB7185))
      "pixel_warrior" -> listOf(NeonCyan, NeonPurple)
      "cosmic_girl" -> listOf(Color(0xFF8B5CF6), Color(0xFFD946EF))
      "robo_geek" -> listOf(Color(0xFF10B981), Color(0xFF06B6D4))
      "gamer_girl" -> listOf(Color(0xFFEC4899), Color(0xFFF43F5E))
      "cyber_skater" -> listOf(Color(0xFFF59E0B), Color(0xFFEF4444))
      else -> listOf(NeonCyan, Color(0xFF3B82F6))
    }
  )

  Box(
    modifier = Modifier
      .size(size)
      .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
    contentAlignment = Alignment.Center
  ) {
    Box(
      modifier = Modifier
        .size(size)
        .clip(CircleShape)
        .then(
          if (showBorder) Modifier.border(
            1.5.dp,
            Brush.linearGradient(listOf(NeonCyan, NeonPurple)),
            CircleShape
          ) else Modifier
        )
        .background(bgBrush),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = "Avatar $avatarId",
        tint = Color.White,
        modifier = Modifier.size(size * 0.58f)
      )
    }

    // Status indicator
    if (status != null) {
      val statusColor = when (status) {
        UserStatus.ONLINE -> OnlineGreen
        UserStatus.IDLE -> IdleYellow
        UserStatus.OFFLINE -> OfflineGray
      }
      Box(
        modifier = Modifier
          .size(size * 0.30f)
          .align(Alignment.BottomEnd)
          .clip(CircleShape)
          .border(2.dp, CyberBg, CircleShape)
          .background(statusColor)
      )
    }
  }
}

@Composable
fun GlowingCard(
  modifier: Modifier = Modifier,
  glowColor: Color = NeonCyan.copy(alpha = 0.25f),
  shapeRadius: Dp = 16.dp,
  onClick: (() -> Unit)? = null,
  content: @Composable ColumnScope.() -> Unit
) {
  val shape = RoundedCornerShape(shapeRadius)
  Card(
    modifier = modifier
      .border(1.dp, glowColor, shape)
      .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
    shape = shape,
    colors = CardDefaults.cardColors(containerColor = CyberCard)
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      content = content
    )
  }
}

@Composable
fun CyberHUDTopBar(
  title: String,
  subtitle: String? = null,
  unreadCount: Int = 0,
  onNotificationsClick: () -> Unit,
  onSettingsClick: () -> Unit,
  onBackClick: (() -> Unit)? = null
) {
  Surface(
    color = CyberBgElevated,
    shadowElevation = 8.dp,
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (onBackClick != null) {
        IconButton(
          onClick = onBackClick,
          modifier = Modifier.testTag("top_bar_back_button")
        ) {
          Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = NeonCyan
          )
        }
      } else {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
              Brush.linearGradient(listOf(NeonCyan.copy(alpha = 0.3f), NeonPurple.copy(alpha = 0.3f)))
            )
            .border(1.dp, NeonCyan, RoundedCornerShape(10.dp)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.SportsEsports,
            contentDescription = "App Logo",
            tint = NeonCyan,
            modifier = Modifier.size(22.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
      }

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          color = TextPrimary,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )
        if (subtitle != null) {
          Text(
            text = subtitle,
            color = NeonCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      // Notifications button with badge
      Box {
        IconButton(
          onClick = onNotificationsClick,
          modifier = Modifier.testTag("top_bar_notifications_button")
        ) {
          Icon(
            imageVector = if (unreadCount > 0) Icons.Filled.Notifications else Icons.Outlined.Notifications,
            contentDescription = "Notifications",
            tint = if (unreadCount > 0) NeonCyan else TextSecondary
          )
        }
        if (unreadCount > 0) {
          Box(
            modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(top = 6.dp, end = 6.dp)
              .size(16.dp)
              .clip(CircleShape)
              .background(NeonCrimson),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = unreadCount.toString(),
              color = Color.White,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      IconButton(
        onClick = onSettingsClick,
        modifier = Modifier.testTag("top_bar_settings_button")
      ) {
        Icon(
          imageVector = Icons.Outlined.Settings,
          contentDescription = "Settings",
          tint = TextSecondary
        )
      }
    }
  }
}

@Composable
fun CyberBottomNav(
  currentTab: ScreenTab,
  onTabSelected: (ScreenTab) -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    color = CyberBgElevated,
    shadowElevation = 12.dp,
    modifier = modifier.fillMaxWidth()
  ) {
    NavigationBar(
      containerColor = Color.Transparent,
      contentColor = TextPrimary,
      windowInsets = WindowInsets.navigationBars,
      modifier = Modifier.height(72.dp)
    ) {
      val items = listOf(
        Triple(ScreenTab.HOME, "Room", Icons.Filled.Home),
        Triple(ScreenTab.FRIENDS, "Friends", Icons.Filled.People),
        Triple(ScreenTab.STUDY, "Study", Icons.Filled.MenuBook),
        Triple(ScreenTab.GAMES, "Games", Icons.Filled.SportsEsports),
        Triple(ScreenTab.PROFILE, "Profile", Icons.Filled.Person)
      )

      items.forEach { (tab, label, icon) ->
        val selected = currentTab == tab
        NavigationBarItem(
          selected = selected,
          onClick = { onTabSelected(tab) },
          icon = {
            Icon(
              imageVector = icon,
              contentDescription = label,
              tint = if (selected) NeonCyan else TextMuted
            )
          },
          label = {
            Text(
              text = label,
              fontSize = 11.sp,
              fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
              color = if (selected) NeonCyan else TextMuted
            )
          },
          colors = NavigationBarItemDefaults.colors(
            indicatorColor = NeonCyan.copy(alpha = 0.15f),
            selectedIconColor = NeonCyan,
            unselectedIconColor = TextMuted
          ),
          modifier = Modifier.testTag("nav_tab_${label.lowercase()}")
        )
      }
    }
  }
}
