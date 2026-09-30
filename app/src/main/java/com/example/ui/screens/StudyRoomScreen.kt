package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.StudyRoom
import com.example.model.StudyTask
import com.example.model.UserStatus
import com.example.ui.components.CyberAvatar
import com.example.ui.components.GlowingCard
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

@Composable
fun StudyRoomScreen(
  viewModel: AppViewModel,
  modifier: Modifier = Modifier
) {
  val studyRooms by viewModel.studyRooms.collectAsState()
  val activeRoomId by viewModel.activeStudyRoomId.collectAsState()
  val friends by viewModel.friends.collectAsState()

  // Timer states
  val timerRunning by viewModel.timerRunning.collectAsState()
  val timerSecondsLeft by viewModel.timerSecondsLeft.collectAsState()
  val timerTotalSeconds by viewModel.timerTotalSeconds.collectAsState()

  val activeRoom = studyRooms.find { it.id == activeRoomId } ?: studyRooms.firstOrNull()

  var newTaskInput by remember { mutableStateOf("") }
  var isEditingNote by remember { mutableStateOf(false) }
  var noteTitle by remember { mutableStateOf("") }
  var noteContent by remember { mutableStateOf("") }
  var showInviteDialog by remember { mutableStateOf(false) }
  var invitedFeedback by remember { mutableStateOf<String?>(null) }

  LaunchedEffect(activeRoom?.notes) {
    val note = activeRoom?.notes?.firstOrNull()
    if (note != null && !isEditingNote) {
      noteTitle = note.title
      noteContent = note.content
    }
  }

  val minutes = timerSecondsLeft / 60
  val seconds = timerSecondsLeft % 60
  val progress = if (timerTotalSeconds > 0) timerSecondsLeft.toFloat() / timerTotalSeconds.toFloat() else 0f

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(CyberBg),
    contentPadding = PaddingValues(bottom = 88.dp)
  ) {
    // Room Selector Header
    item {
      Surface(
        color = CyberBgElevated,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "STUDY ROOMS",
              color = TextSecondary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp
            )
            Button(
              onClick = { viewModel.toggleCreateRoomDialog(true) },
              colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = CyberBg),
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.testTag("study_create_room_button")
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("New Room", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Room Pills
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(studyRooms) { room ->
              val isSelected = room.id == activeRoom?.id
              Surface(
                color = if (isSelected) NeonCyan.copy(alpha = 0.15f) else CyberCard,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (isSelected) NeonCyan else CyberCardBorder
                ),
                modifier = Modifier.clickable { viewModel.openStudyRoom(room.id) }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = if (isSelected) NeonCyan else TextMuted,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = room.title,
                    color = if (isSelected) NeonCyan else TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                  )
                }
              }
            }
          }
        }
      }
    }

    if (activeRoom != null) {
      // Room Info Card & Members
      item {
        GlowingCard(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = activeRoom.title,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Subject: ${activeRoom.subject} • Host: ${activeRoom.hostName}",
                color = NeonCyan,
                fontSize = 12.sp
              )
              Text(
                text = "🎧 Music: ${activeRoom.ambientTheme}",
                color = TextSecondary,
                fontSize = 11.sp
              )
            }

            OutlinedButton(
              onClick = { showInviteDialog = true },
              shape = RoundedCornerShape(8.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonPurple),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.testTag("study_invite_button")
            ) {
              Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Invite", fontSize = 11.sp)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Members Chips
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(text = "Members in room:", color = TextSecondary, fontSize = 11.sp)
            activeRoom.memberNames.forEach { name ->
              Surface(
                color = CyberBgElevated,
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(6.dp)
                      .clip(CircleShape)
                      .background(OnlineGreen)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(text = name, color = TextPrimary, fontSize = 11.sp)
                }
              }
            }
          }
        }
      }

      // Focus Timer Section
      item {
        GlowingCard(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.HourglassBottom, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "SHARED FOCUS TIMER",
                  color = TextPrimary,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 0.5.sp
                )
              }

              // Timer Presets
              Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(15, 25, 45).forEach { dur ->
                  Surface(
                    color = if (timerTotalSeconds == dur * 60) NeonCyan else CyberBgElevated,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.clickable { viewModel.setTimerDuration(dur) }
                  ) {
                    Text(
                      text = "${dur}m",
                      color = if (timerTotalSeconds == dur * 60) CyberBg else TextSecondary,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Circular Glowing HUD Timer
            Box(
              modifier = Modifier.size(160.dp),
              contentAlignment = Alignment.Center
            ) {
              CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.fillMaxSize(),
                color = CyberCardBorder,
                strokeWidth = 8.dp
              )
              CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                color = if (timerRunning) NeonCyan else NeonPurple,
                strokeWidth = 8.dp
              )
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = String.format("%02d:%02d", minutes, seconds),
                  color = TextPrimary,
                  fontSize = 32.sp,
                  fontWeight = FontWeight.ExtraBold,
                  letterSpacing = 1.sp
                )
                Text(
                  text = if (timerRunning) "FOCUSING" else "PAUSED",
                  color = if (timerRunning) NeonCyan else TextMuted,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Timer Controls
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
              Button(
                onClick = {
                  if (timerRunning) viewModel.pauseTimer() else viewModel.startTimer()
                },
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (timerRunning) NeonAmber else NeonCyan,
                  contentColor = CyberBg
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("study_timer_toggle")
              ) {
                Icon(
                  imageVector = if (timerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                  contentDescription = null
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (timerRunning) "Pause" else "Start Timer",
                  fontWeight = FontWeight.Bold
                )
              }

              OutlinedButton(
                onClick = { viewModel.resetTimer(timerTotalSeconds / 60) },
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                modifier = Modifier.testTag("study_timer_reset")
              ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Reset")
              }
            }
          }
        }
      }

      // Shared To-Do List Section
      item {
        GlowingCard(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Checklist, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "SHARED TO-DO LIST",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
              )
            }
            Text(
              text = "${activeRoom.tasks.count { it.isCompleted }}/${activeRoom.tasks.size} done",
              color = NeonEmerald,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Add Task Input
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = newTaskInput,
              onValueChange = { newTaskInput = it },
              placeholder = { Text("Add task for this session...", color = TextMuted) },
              modifier = Modifier
                .weight(1f)
                .testTag("study_add_task_input"),
              singleLine = true,
              shape = RoundedCornerShape(10.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = CyberCardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
              )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = {
                if (newTaskInput.isNotBlank()) {
                  viewModel.addTask(activeRoom.id, newTaskInput)
                  newTaskInput = ""
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = CyberBg),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.testTag("study_add_task_button")
            ) {
              Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Tasks Items
          activeRoom.tasks.forEach { task ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CyberBgElevated)
                .clickable { viewModel.toggleTask(activeRoom.id, task.id) }
                .padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { viewModel.toggleTask(activeRoom.id, task.id) },
                colors = CheckboxDefaults.colors(
                  checkedColor = NeonEmerald,
                  uncheckedColor = TextSecondary,
                  checkmarkColor = CyberBg
                )
              )
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = task.title,
                  color = if (task.isCompleted) TextMuted else TextPrimary,
                  fontSize = 13.sp,
                  textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                )
                Text(
                  text = "Assigned: ${task.assignedTo}",
                  color = TextMuted,
                  fontSize = 10.sp
                )
              }
            }
          }
        }
      }

      // Shared Notes Section
      item {
        GlowingCard(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.EditNote, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "SHARED STUDY NOTES",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
              )
            }

            TextButton(
              onClick = {
                if (isEditingNote) {
                  viewModel.saveNote(
                    activeRoom.id,
                    activeRoom.notes.firstOrNull()?.id,
                    noteTitle,
                    noteContent
                  )
                }
                isEditingNote = !isEditingNote
              },
              modifier = Modifier.testTag("study_edit_note_toggle")
            ) {
              Text(
                text = if (isEditingNote) "Save Notes" else "Edit Notes",
                color = NeonCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          if (isEditingNote) {
            OutlinedTextField(
              value = noteTitle,
              onValueChange = { noteTitle = it },
              label = { Text("Note Title") },
              modifier = Modifier.fillMaxWidth(),
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = CyberCardBorder
              )
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = noteContent,
              onValueChange = { noteContent = it },
              label = { Text("Collaborative Notes Content") },
              modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = CyberCardBorder
              )
            )
          } else {
            Surface(
              color = CyberBgElevated,
              shape = RoundedCornerShape(10.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text(
                  text = noteTitle.ifBlank { "Session Summary" },
                  color = NeonCyan,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = noteContent.ifBlank { "No notes yet. Tap 'Edit Notes' to jot down equations, summaries, or links." },
                  color = TextPrimary,
                  fontSize = 13.sp,
                  lineHeight = 18.sp
                )
              }
            }
          }
        }
      }
    }
  }

  // Invite Friend Modal
  if (showInviteDialog) {
    AlertDialog(
      onDismissRequest = {
        showInviteDialog = false
        invitedFeedback = null
      },
      containerColor = CyberCard,
      shape = RoundedCornerShape(20.dp),
      title = { Text(text = "Invite Friends to Room", color = TextPrimary, fontWeight = FontWeight.Bold) },
      text = {
        Column {
          Text(
            text = "Select a classmate to send an instant room invitation:",
            color = TextSecondary,
            fontSize = 12.sp
          )
          Spacer(modifier = Modifier.height(12.dp))

          friends.filter { it.isFriend }.forEach { friend ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CyberBgElevated)
                .clickable {
                  viewModel.sendMessage("🚀 Join my study room: ${activeRoom?.title}!")
                  invitedFeedback = "Invitation sent to ${friend.username}!"
                }
                .padding(horizontal = 10.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              CyberAvatar(avatarId = friend.avatarId, status = friend.status, size = 32.dp)
              Spacer(modifier = Modifier.width(10.dp))
              Text(text = friend.username, color = TextPrimary, fontSize = 13.sp, modifier = Modifier.weight(1f))
              Icon(Icons.Default.Send, contentDescription = "Invite", tint = NeonCyan, modifier = Modifier.size(16.dp))
            }
          }

          if (invitedFeedback != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = invitedFeedback ?: "", color = NeonEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      },
      confirmButton = {
        Button(
          onClick = { showInviteDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = CyberBg)
        ) {
          Text("Done")
        }
      }
    )
  }
}
