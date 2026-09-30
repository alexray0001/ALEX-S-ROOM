package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppNotification
import com.example.model.NotificationType
import com.example.ui.components.GlowingCard
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel
import com.example.viewmodel.ScreenTab

@Composable
fun NotificationsScreen(
  viewModel: AppViewModel,
  onBack: () -> Unit
) {
  val notifications by viewModel.notifications.collectAsState()

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
        IconButton(onClick = onBack, modifier = Modifier.testTag("notifications_back_button")) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = NeonCyan)
        }
        Text(
          text = "NOTIFICATIONS",
          color = TextPrimary,
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp,
          modifier = Modifier.weight(1f)
        )
        TextButton(onClick = { viewModel.repository.markAllNotificationsRead() }) {
          Text("Read All", color = NeonCyan, fontSize = 12.sp)
        }
      }
    }

    if (notifications.isEmpty()) {
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(Icons.Outlined.NotificationsOff, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
          Spacer(modifier = Modifier.height(12.dp))
          Text(text = "No Notifications", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
          Text(text = "You're all caught up with your study group!", color = TextSecondary, fontSize = 12.sp)
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(notifications) { notif ->
          NotificationItemCard(
            notification = notif,
            onAction = {
              when (notif.type) {
                NotificationType.FRIEND_REQUEST -> {
                  if (notif.senderId != null) {
                    viewModel.acceptFriendRequest(notif.senderId)
                  }
                }
                NotificationType.ROOM_INVITE -> {
                  if (notif.roomId != null) {
                    viewModel.openStudyRoom(notif.roomId)
                    onBack()
                  }
                }
                NotificationType.NEW_MESSAGE -> {
                  if (notif.senderId != null) {
                    viewModel.openChat(notif.senderId)
                    onBack()
                  }
                }
                NotificationType.SYSTEM -> {}
              }
            },
            onDismiss = {
              viewModel.repository.clearNotification(notif.id)
            }
          )
        }
      }
    }
  }
}

@Composable
private fun NotificationItemCard(
  notification: AppNotification,
  onAction: () -> Unit,
  onDismiss: () -> Unit
) {
  val icon = when (notification.type) {
    NotificationType.FRIEND_REQUEST -> Icons.Default.PersonAdd
    NotificationType.NEW_MESSAGE -> Icons.Default.Chat
    NotificationType.ROOM_INVITE -> Icons.Default.MeetingRoom
    NotificationType.SYSTEM -> Icons.Default.LocalFireDepartment
  }

  val tint = when (notification.type) {
    NotificationType.FRIEND_REQUEST -> NeonCyan
    NotificationType.NEW_MESSAGE -> NeonPurple
    NotificationType.ROOM_INVITE -> NeonEmerald
    NotificationType.SYSTEM -> NeonAmber
  }

  GlowingCard(
    modifier = Modifier.fillMaxWidth(),
    glowColor = if (!notification.isRead) tint.copy(alpha = 0.4f) else CyberCardBorder
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.Top
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(tint.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = notification.title,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
          IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
          }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = notification.message,
          color = TextSecondary,
          fontSize = 12.sp,
          lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (notification.type == NotificationType.FRIEND_REQUEST) {
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
              onClick = onAction,
              colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = CyberBg),
              shape = RoundedCornerShape(6.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
            ) {
              Text("Accept", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        } else if (notification.type == NotificationType.ROOM_INVITE) {
          Button(
            onClick = onAction,
            colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = CyberBg),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
          ) {
            Text("Join Room", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
