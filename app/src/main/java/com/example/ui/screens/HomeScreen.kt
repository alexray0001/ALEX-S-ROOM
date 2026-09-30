package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.FriendUser
import com.example.model.UserStatus
import com.example.ui.components.CyberAvatar
import com.example.ui.components.GlowingCard
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel
import com.example.viewmodel.ScreenTab

@Composable
fun HomeScreen(
  viewModel: AppViewModel,
  modifier: Modifier = Modifier
) {
  val user by viewModel.currentUser.collectAsState()
  val friends by viewModel.friends.collectAsState()
  val studyRooms by viewModel.studyRooms.collectAsState()
  val chats by viewModel.chats.collectAsState()

  val onlineFriends = friends.filter { it.status == UserStatus.ONLINE || it.status == UserStatus.IDLE }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(CyberBg),
    contentPadding = PaddingValues(bottom = 88.dp)
  ) {
    // Top Hero Card with Gaming Room Banner
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp)
          .height(180.dp)
          .clip(RoundedCornerShape(20.dp))
          .border(1.5.dp, Brush.linearGradient(listOf(NeonCyan, NeonPurple)), RoundedCornerShape(20.dp))
      ) {
        // Banner Image
        Image(
          painter = painterResource(id = R.drawable.gaming_room_banner),
          contentDescription = "Gaming Room",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                listOf(Color.Transparent, CyberBg.copy(alpha = 0.85f), CyberBg)
              )
            )
        )

        // Hero Info
        Column(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(16.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              color = NeonCyan.copy(alpha = 0.2f),
              shape = RoundedCornerShape(6.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
            ) {
              Text(
                text = "ALEX'S COMMAND CENTER",
                color = NeonCyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "🔥 ${user.studyStreakDays} Day Streak",
              color = NeonAmber,
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold
            )
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Welcome back, ${user.username}!",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = user.activityText,
            color = TextSecondary,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }
    }

    // Quick Stats Bar
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        QuickStatCard(
          icon = Icons.Default.Timer,
          value = "${user.focusMinutes}m",
          label = "Focus Time",
          tint = NeonCyan,
          modifier = Modifier.weight(1f)
        )
        QuickStatCard(
          icon = Icons.Default.CheckCircle,
          value = "${user.completedTasksCount}",
          label = "Tasks Done",
          tint = NeonEmerald,
          modifier = Modifier.weight(1f)
        )
        QuickStatCard(
          icon = Icons.Default.EmojiEvents,
          value = "${user.gamesWonCount}",
          label = "Game Wins",
          tint = NeonPurple,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Online Friends Section
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 16.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(OnlineGreen)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "ONLINE FRIENDS (${onlineFriends.size})",
              color = TextPrimary,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp
            )
          }
          TextButton(
            onClick = { viewModel.setTab(ScreenTab.FRIENDS) },
            contentPadding = PaddingValues(0.dp)
          ) {
            Text("See All", color = NeonCyan, fontSize = 12.sp)
          }
        }

        LazyRow(
          contentPadding = PaddingValues(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          items(onlineFriends) { friend ->
            OnlineFriendPill(
              friend = friend,
              onChatClick = { viewModel.openChat(friend.id) }
            )
          }
        }
      }
    }

    // Quick Action Buttons
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 16.dp)
      ) {
        Text(
          text = "QUICK ACTIONS",
          color = TextSecondary,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp,
          modifier = Modifier.padding(bottom = 10.dp)
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          ActionTile(
            title = "Join Study Room",
            subtitle = "Pomodoro & Tasks",
            icon = Icons.Default.MenuBook,
            accent = NeonCyan,
            modifier = Modifier.weight(1f),
            onClick = { viewModel.setTab(ScreenTab.STUDY) }
          )
          ActionTile(
            title = "Play Games",
            subtitle = "TicTacToe & RPS",
            icon = Icons.Default.SportsEsports,
            accent = NeonPurple,
            modifier = Modifier.weight(1f),
            onClick = { viewModel.setTab(ScreenTab.GAMES) }
          )
        }
      }
    }

    // Active Study Rooms
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "ACTIVE STUDY ROOMS",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
          )
          IconButton(
            onClick = { viewModel.toggleCreateRoomDialog(true) },
            modifier = Modifier.testTag("home_add_room_button")
          ) {
            Icon(Icons.Default.AddCircle, contentDescription = "Create room", tint = NeonCyan)
          }
        }

        studyRooms.forEach { room ->
          GlowingCard(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 6.dp)
              .testTag("home_room_${room.id}"),
            onClick = { viewModel.openStudyRoom(room.id) }
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(NeonCyan.copy(alpha = 0.15f))
                  .border(1.dp, NeonCyan, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Group,
                  contentDescription = null,
                  tint = NeonCyan,
                  modifier = Modifier.size(24.dp)
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = room.title,
                  color = TextPrimary,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "${room.subject} • ${room.ambientTheme}",
                  color = NeonCyan,
                  fontSize = 12.sp
                )
                Text(
                  text = "${room.memberNames.size} members active • ${room.tasks.count { it.isCompleted }}/${room.tasks.size} tasks done",
                  color = TextSecondary,
                  fontSize = 11.sp
                )
              }

              Button(
                onClick = { viewModel.openStudyRoom(room.id) },
                colors = ButtonDefaults.buttonColors(
                  containerColor = NeonCyan,
                  contentColor = CyberBg
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text("Join", fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }
            }
          }
        }
      }
    }

    // Recent Chats Section
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 14.dp)
      ) {
        Text(
          text = "RECENT CHATS",
          color = TextSecondary,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp,
          modifier = Modifier.padding(bottom = 8.dp)
        )

        chats.forEach { (friendId, messages) ->
          val friend = friends.find { it.id == friendId }
          val lastMsg = messages.lastOrNull()
          if (friend != null && lastMsg != null) {
            GlowingCard(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .testTag("recent_chat_$friendId"),
              onClick = { viewModel.openChat(friendId) }
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
              ) {
                CyberAvatar(avatarId = friend.avatarId, status = friend.status, size = 42.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = friend.username,
                      color = TextPrimary,
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold
                    )
                    Text(
                      text = "Active",
                      color = NeonCyan,
                      fontSize = 10.sp
                    )
                  }
                  Text(
                    text = lastMsg.text,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun QuickStatCard(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  value: String,
  label: String,
  tint: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    color = CyberCard,
    shape = RoundedCornerShape(14.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = value, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
      Text(text = label, color = TextSecondary, fontSize = 10.sp)
    }
  }
}

@Composable
private fun OnlineFriendPill(
  friend: FriendUser,
  onChatClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clickable(onClick = onChatClick)
      .width(68.dp)
  ) {
    CyberAvatar(
      avatarId = friend.avatarId,
      status = friend.status,
      size = 52.dp,
      onClick = onChatClick
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = friend.username,
      color = TextPrimary,
      fontSize = 11.sp,
      fontWeight = FontWeight.Medium,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}

@Composable
private fun ActionTile(
  title: String,
  subtitle: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  accent: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Surface(
    modifier = modifier.clickable(onClick = onClick),
    color = CyberCard,
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, accent.copy(alpha = 0.5f))
  ) {
    Column(
      modifier = Modifier.padding(14.dp)
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(accent.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(imageVector = icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
      }
      Spacer(modifier = Modifier.height(10.dp))
      Text(text = title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
      Text(text = subtitle, color = TextSecondary, fontSize = 11.sp)
    }
  }
}
