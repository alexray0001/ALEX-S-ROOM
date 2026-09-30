package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.model.UserStatus
import com.example.ui.components.CyberAvatar
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatScreen(
  viewModel: AppViewModel,
  friendId: String,
  onBack: () -> Unit
) {
  val friends by viewModel.friends.collectAsState()
  val chats by viewModel.chats.collectAsState()
  val typingFriendId by viewModel.typingFriendId.collectAsState()

  val friend = friends.find { it.id == friendId }
  val messages = chats[friendId].orEmpty()
  var inputMessage by remember { mutableStateOf("") }
  val listState = rememberLazyListState()

  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  val quickReplies = listOf(
    "Ready to study! 📚",
    "Join my study room! 🚀",
    "Play Tic Tac Toe? 🎮",
    "On question 5 now",
    "Be right back! ⏳"
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(CyberBg)
      .systemBarsPadding()
  ) {
    // Chat Top App Bar
    Surface(
      color = CyberBgElevated,
      shadowElevation = 6.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onBack,
          modifier = Modifier.testTag("chat_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = NeonCyan
          )
        }

        if (friend != null) {
          CyberAvatar(
            avatarId = friend.avatarId,
            status = friend.status,
            size = 40.dp
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = friend.username,
              color = TextPrimary,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = if (typingFriendId == friendId) "typing..." else friend.activityText,
              color = if (typingFriendId == friendId) NeonCyan else TextSecondary,
              fontSize = 11.sp,
              fontWeight = if (typingFriendId == friendId) FontWeight.Bold else FontWeight.Normal
            )
          }

          IconButton(
            onClick = {
              viewModel.sendMessage("🎮 I challenged you to a game in Alex's Room!")
            },
            modifier = Modifier.testTag("chat_game_invite_button")
          ) {
            Icon(Icons.Default.SportsEsports, contentDescription = "Play Game", tint = NeonPurple)
          }
        }
      }
    }

    // Message List
    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = 16.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(messages) { msg ->
        ChatBubbleItem(message = msg)
      }

      if (typingFriendId == friendId) {
        item {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
          ) {
            Surface(
              color = CyberCard,
              shape = RoundedCornerShape(14.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "${friend?.username ?: "Friend"} is typing",
                  color = TextSecondary,
                  fontSize = 11.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                CircularProgressIndicator(
                  modifier = Modifier.size(10.dp),
                  color = NeonCyan,
                  strokeWidth = 1.5.dp
                )
              }
            }
          }
        }
      }
    }

    // Quick Replies Chips
    LazyRow(
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(quickReplies) { chip ->
        Surface(
          color = CyberCard,
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
          modifier = Modifier.clickable {
            viewModel.sendMessage(chip)
          }
        ) {
          Text(
            text = chip,
            color = NeonCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          )
        }
      }
    }

    // Bottom Message Input Row
    Surface(
      color = CyberBgElevated,
      modifier = Modifier
        .fillMaxWidth()
        .imePadding()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = inputMessage,
          onValueChange = { inputMessage = it },
          placeholder = { Text("Message ${friend?.username ?: "friend"}...", color = TextMuted) },
          modifier = Modifier
            .weight(1f)
            .testTag("chat_message_input"),
          maxLines = 3,
          shape = RoundedCornerShape(20.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = CyberCardBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = NeonCyan
          )
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
          onClick = {
            if (inputMessage.isNotBlank()) {
              viewModel.sendMessage(inputMessage)
              inputMessage = ""
            }
          },
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(
              Brush.linearGradient(listOf(NeonCyan, NeonPurple))
            )
            .testTag("chat_send_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send",
            tint = CyberBg,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun ChatBubbleItem(message: ChatMessage) {
  val isMine = message.isMine
  val timeFormat = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
  val timeStr = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
  ) {
    Surface(
      color = if (isMine) NeonCyan.copy(alpha = 0.2f) else CyberCard,
      shape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (isMine) 16.dp else 4.dp,
        bottomEnd = if (isMine) 4.dp else 16.dp
      ),
      border = androidx.compose.foundation.BorderStroke(
        1.dp,
        if (isMine) NeonCyan else CyberCardBorder
      ),
      modifier = Modifier.widthIn(max = 290.dp)
    ) {
      Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
        if (!isMine) {
          Text(
            text = message.senderName,
            color = NeonCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 2.dp)
          )
        }
        Text(
          text = message.text,
          color = TextPrimary,
          fontSize = 14.sp,
          lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.align(Alignment.End),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = timeStr,
            color = TextMuted,
            fontSize = 10.sp
          )
          if (isMine) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
              imageVector = Icons.Default.DoneAll,
              contentDescription = "Delivered",
              tint = NeonCyan,
              modifier = Modifier.size(12.dp)
            )
          }
        }
      }
    }
  }
}
