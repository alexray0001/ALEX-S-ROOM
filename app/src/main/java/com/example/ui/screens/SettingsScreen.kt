package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FirebaseConfigHelper
import com.example.ui.components.GlowingCard
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel
import com.example.viewmodel.ScreenTab

@Composable
fun SettingsScreen(
  viewModel: AppViewModel,
  onBack: () -> Unit
) {
  val context = LocalContext.current
  val isFirebaseConfigured = remember { FirebaseConfigHelper.isFirebaseAvailable(context) }

  var notificationsEnabled by remember { mutableStateOf(true) }
  var hapticsEnabled by remember { mutableStateOf(true) }
  var soundFxEnabled by remember { mutableStateOf(true) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(CyberBg)
      .systemBarsPadding()
  ) {
    // Header
    Surface(
      color = CyberBgElevated,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onBack, modifier = Modifier.testTag("settings_back_button")) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeonCyan)
        }
        Text(
          text = "SETTINGS & SYSTEM",
          color = TextPrimary,
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )
      }
    }

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Firebase Cloud Status Card
      item {
        GlowingCard(
          modifier = Modifier.fillMaxWidth(),
          glowColor = if (isFirebaseConfigured) NeonEmerald else NeonCyan.copy(alpha = 0.5f)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = if (isFirebaseConfigured) Icons.Default.CloudDone else Icons.Default.SettingsInputAntenna,
              contentDescription = null,
              tint = if (isFirebaseConfigured) NeonEmerald else NeonCyan,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Firebase Backend Status",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = if (isFirebaseConfigured) "Connected to Firebase Auth & Firestore" else "Running in Interactive Local & Studio Mode",
                color = if (isFirebaseConfigured) NeonEmerald else TextSecondary,
                fontSize = 12.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Button(
            onClick = { viewModel.toggleFirebaseDialog(true) },
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = CyberBg),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("settings_view_setup_guide")
          ) {
            Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("View Android Studio Setup Guide", fontWeight = FontWeight.Bold)
          }
        }
      }

      // App Preferences
      item {
        GlowingCard(modifier = Modifier.fillMaxWidth()) {
          Text(text = "GAMING & AUDIO SETTINGS", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(10.dp))

          SettingToggleRow(
            title = "Push Notifications",
            subtitle = "Friend requests, room invites & chat alerts",
            checked = notificationsEnabled,
            onCheckedChange = { notificationsEnabled = it }
          )

          HorizontalDivider(color = CyberCardBorder, modifier = Modifier.padding(vertical = 8.dp))

          SettingToggleRow(
            title = "Haptic Vibration",
            subtitle = "Feedback when timer finishes and in games",
            checked = hapticsEnabled,
            onCheckedChange = { hapticsEnabled = it }
          )

          HorizontalDivider(color = CyberCardBorder, modifier = Modifier.padding(vertical = 8.dp))

          SettingToggleRow(
            title = "Game Sound FX",
            subtitle = "Sounds in Tic Tac Toe and Rock Paper Scissors",
            checked = soundFxEnabled,
            onCheckedChange = { soundFxEnabled = it }
          )
        }
      }

      // Account Options
      item {
        GlowingCard(modifier = Modifier.fillMaxWidth()) {
          Text(text = "ACCOUNT & PRIVACY", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                onBack()
                viewModel.setTab(ScreenTab.PROFILE)
              }
              .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = NeonCyan)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(text = "Edit Profile & Avatar", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
              Text(text = "Change gamer tag, bio, and student grade", color = TextSecondary, fontSize = 12.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
          }

          HorizontalDivider(color = CyberCardBorder, modifier = Modifier.padding(vertical = 8.dp))

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { viewModel.logout() }
              .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Logout, contentDescription = null, tint = NeonCrimson)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(text = "Log Out", color = NeonCrimson, fontSize = 14.sp, fontWeight = FontWeight.Bold)
              Text(text = "Sign out of your Alex's Room account", color = TextSecondary, fontSize = 12.sp)
            }
          }
        }
      }

      // About
      item {
        GlowingCard(modifier = Modifier.fillMaxWidth()) {
          Text(text = "ABOUT ALEX'S ROOM", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(8.dp))
          Text(text = "Version 1.0.0 (Release Build)", color = TextPrimary, fontSize = 13.sp)
          Text(text = "Designed for school friends to chat, study together, manage tasks, and play small games.", color = TextSecondary, fontSize = 12.sp)
          Spacer(modifier = Modifier.height(6.dp))
          Text(text = "Built with Kotlin, Jetpack Compose, and Material 3.", color = NeonCyan, fontSize = 11.sp)
        }
      }
    }
  }
}

@Composable
private fun SettingToggleRow(
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(text = title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
      Text(text = subtitle, color = TextSecondary, fontSize = 11.sp)
    }
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = CyberBg,
        checkedTrackColor = NeonCyan,
        uncheckedThumbColor = TextMuted,
        uncheckedTrackColor = CyberBgElevated
      )
    )
  }
}
