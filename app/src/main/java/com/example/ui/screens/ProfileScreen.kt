package com.example.ui.screens

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserProfile
import com.example.ui.components.CyberAvatar
import com.example.ui.components.GlowingCard
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

@Composable
fun ProfileScreen(
  viewModel: AppViewModel,
  modifier: Modifier = Modifier
) {
  val user by viewModel.currentUser.collectAsState()
  val friends by viewModel.friends.collectAsState()

  var showEditProfileDialog by remember { mutableStateOf(false) }
  var editUsername by remember { mutableStateOf(user.username) }
  var editBio by remember { mutableStateOf(user.bio) }
  var editGrade by remember { mutableStateOf(user.grade) }
  var selectedAvatarId by remember { mutableStateOf(user.avatarId) }

  val avatarOptions = listOf(
    "neon_ninja", "cyber_cat", "pixel_warrior", "cosmic_girl", "robo_geek", "gamer_girl", "cyber_skater"
  )

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(CyberBg),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Profile Header Card
    item {
      GlowingCard(modifier = Modifier.fillMaxWidth()) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier.size(88.dp),
            contentAlignment = Alignment.Center
          ) {
            CyberAvatar(
              avatarId = user.avatarId,
              status = user.status,
              size = 80.dp,
              onClick = { showEditProfileDialog = true }
            )
            Box(
              modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(26.dp)
                .clip(CircleShape)
                .background(NeonCyan)
                .clickable { showEditProfileDialog = true },
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Edit, contentDescription = "Edit Avatar", tint = CyberBg, modifier = Modifier.size(14.dp))
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = user.username,
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
          )

          Surface(
            color = NeonCyan.copy(alpha = 0.15f),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.padding(top = 4.dp)
          ) {
            Text(
              text = user.grade,
              color = NeonCyan,
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = user.bio,
            color = TextSecondary,
            fontSize = 13.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          Button(
            onClick = {
              editUsername = user.username
              editBio = user.bio
              editGrade = user.grade
              selectedAvatarId = user.avatarId
              showEditProfileDialog = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = CyberBg),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("profile_edit_button")
          ) {
            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Edit Profile & Avatar", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Study Statistics Card
    item {
      GlowingCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.MenuBook, contentDescription = null, tint = NeonCyan)
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "STUDY METRICS", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          StatBox(
            title = "Focus Time",
            value = "${user.focusMinutes / 60}h ${user.focusMinutes % 60}m",
            icon = Icons.Default.Timer,
            tint = NeonCyan,
            modifier = Modifier.weight(1f)
          )
          StatBox(
            title = "Study Streak",
            value = "${user.studyStreakDays} Days",
            icon = Icons.Default.LocalFireDepartment,
            tint = NeonAmber,
            modifier = Modifier.weight(1f)
          )
          StatBox(
            title = "Tasks Solved",
            value = "${user.completedTasksCount}",
            icon = Icons.Default.CheckCircle,
            tint = NeonEmerald,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // Gaming Statistics Card
    item {
      GlowingCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.SportsEsports, contentDescription = null, tint = NeonPurple)
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "ARCADE & GAME METRICS", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          StatBox(
            title = "Arcade Wins",
            value = "${user.gamesWonCount}",
            icon = Icons.Default.EmojiEvents,
            tint = NeonPurple,
            modifier = Modifier.weight(1f)
          )
          StatBox(
            title = "Friends Connected",
            value = "${friends.count { it.isFriend }}",
            icon = Icons.Default.People,
            tint = NeonCyan,
            modifier = Modifier.weight(1f)
          )
          StatBox(
            title = "Rank",
            value = "Room Master",
            icon = Icons.Default.MilitaryTech,
            tint = NeonPink,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // Account & School Info
    item {
      GlowingCard(modifier = Modifier.fillMaxWidth()) {
        Text(text = "STUDENT CREDENTIALS", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Email: ${user.email}", color = TextPrimary, fontSize = 13.sp)
        Text(text = "Status: Online & In Session", color = OnlineGreen, fontSize = 13.sp)
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }

  // Edit Profile Dialog
  if (showEditProfileDialog) {
    AlertDialog(
      onDismissRequest = { showEditProfileDialog = false },
      containerColor = CyberCard,
      shape = RoundedCornerShape(20.dp),
      title = { Text(text = "Customize Profile & Avatar", color = TextPrimary, fontWeight = FontWeight.Bold) },
      text = {
        Column {
          Text(text = "Select Avatar Icon:", color = TextSecondary, fontSize = 12.sp)
          Spacer(modifier = Modifier.height(8.dp))

          LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(avatarOptions) { avId ->
              val isSelected = selectedAvatarId == avId
              Box(
                modifier = Modifier
                  .clip(CircleShape)
                  .border(
                    if (isSelected) 2.dp else 1.dp,
                    if (isSelected) NeonCyan else CyberCardBorder,
                    CircleShape
                  )
                  .padding(2.dp)
                  .clickable { selectedAvatarId = avId }
              ) {
                CyberAvatar(avatarId = avId, size = 44.dp)
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = editUsername,
            onValueChange = { editUsername = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = editGrade,
            onValueChange = { editGrade = it },
            label = { Text("School Grade / Tag") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = editBio,
            onValueChange = { editBio = it },
            label = { Text("Bio") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.updateProfile(editUsername, editBio, editGrade, selectedAvatarId)
            showEditProfileDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = CyberBg)
        ) {
          Text("Save Changes")
        }
      },
      dismissButton = {
        TextButton(onClick = { showEditProfileDialog = false }) {
          Text("Cancel", color = TextSecondary)
        }
      }
    )
  }
}

@Composable
private fun StatBox(
  title: String,
  value: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  tint: androidx.compose.ui.graphics.Color,
  modifier: Modifier = Modifier
) {
  Surface(
    color = CyberBgElevated,
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = value, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
      Text(text = title, color = TextSecondary, fontSize = 10.sp)
    }
  }
}
