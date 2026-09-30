package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FirebaseConfigHelper
import com.example.ui.components.CyberBottomNav
import com.example.ui.components.CyberHUDTopBar
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel
import com.example.viewmodel.ScreenTab

class MainActivity : ComponentActivity() {
  private val viewModel: AppViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      MyApplicationTheme {
        AlexsRoomApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun AlexsRoomApp(viewModel: AppViewModel) {
  var showSplash by remember { mutableStateOf(true) }
  val isLoggedIn by viewModel.isLoggedIn.collectAsState()
  val currentTab by viewModel.currentTab.collectAsState()
  val activeChatFriendId by viewModel.activeChatFriendId.collectAsState()
  val showSettings by viewModel.showSettings.collectAsState()
  val showNotifications by viewModel.showNotifications.collectAsState()
  val showFirebaseDialog by viewModel.showFirebaseDialog.collectAsState()
  val showCreateRoomDialog by viewModel.showCreateRoomDialog.collectAsState()
  val notifications by viewModel.notifications.collectAsState()
  val unreadNotificationsCount = notifications.count { !it.isRead }

  val clipboardManager = LocalClipboardManager.current
  var copiedFeedback by remember { mutableStateOf(false) }

  if (showSplash) {
    SplashScreen(onFinish = { showSplash = false })
    return
  }

  if (!isLoggedIn) {
    AuthScreen(
      viewModel = viewModel,
      onAuthSuccess = { /* Automatically navigates based on state */ }
    )
  } else {
    // Check detail destinations
    when {
      activeChatFriendId != null -> {
        BackHandler { viewModel.closeChat() }
        ChatScreen(
          viewModel = viewModel,
          friendId = activeChatFriendId!!,
          onBack = { viewModel.closeChat() }
        )
      }

      showSettings -> {
        BackHandler { viewModel.toggleSettings(false) }
        SettingsScreen(
          viewModel = viewModel,
          onBack = { viewModel.toggleSettings(false) }
        )
      }

      showNotifications -> {
        BackHandler { viewModel.toggleNotifications(false) }
        NotificationsScreen(
          viewModel = viewModel,
          onBack = { viewModel.toggleNotifications(false) }
        )
      }

      else -> {
        // Handle BackHandler to return to HOME tab if on other tabs
        if (currentTab != ScreenTab.HOME) {
          BackHandler { viewModel.setTab(ScreenTab.HOME) }
        }

        val topBarTitle = when (currentTab) {
          ScreenTab.HOME -> "ALEX'S ROOM"
          ScreenTab.FRIENDS -> "FRIENDS & SQUAD"
          ScreenTab.STUDY -> "STUDY ROOM"
          ScreenTab.GAMES -> "ARCADE MINI-GAMES"
          ScreenTab.PROFILE -> "STUDENT PROFILE"
        }

        val topBarSubtitle = when (currentTab) {
          ScreenTab.HOME -> "School Gaming & Study Hub"
          ScreenTab.FRIENDS -> "Online Classmates"
          ScreenTab.STUDY -> "Focus Timer & Shared Notes"
          ScreenTab.GAMES -> "Play & Challenge Friends"
          ScreenTab.PROFILE -> "Stats, Ranks & Badges"
        }

        Scaffold(
          modifier = Modifier.fillMaxSize(),
          containerColor = CyberBg,
          topBar = {
            CyberHUDTopBar(
              title = topBarTitle,
              subtitle = topBarSubtitle,
              unreadCount = unreadNotificationsCount,
              onNotificationsClick = { viewModel.toggleNotifications(true) },
              onSettingsClick = { viewModel.toggleSettings(true) }
            )
          },
          bottomBar = {
            CyberBottomNav(
              currentTab = currentTab,
              onTabSelected = { viewModel.setTab(it) }
            )
          }
        ) { paddingValues ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(paddingValues)
          ) {
            when (currentTab) {
              ScreenTab.HOME -> HomeScreen(viewModel = viewModel)
              ScreenTab.FRIENDS -> FriendsScreen(viewModel = viewModel)
              ScreenTab.STUDY -> StudyRoomScreen(viewModel = viewModel)
              ScreenTab.GAMES -> GamesScreen(viewModel = viewModel)
              ScreenTab.PROFILE -> ProfileScreen(viewModel = viewModel)
            }
          }
        }
      }
    }
  }

  // Firebase Setup Guide Modal
  if (showFirebaseDialog) {
    AlertDialog(
      onDismissRequest = {
        viewModel.toggleFirebaseDialog(false)
        copiedFeedback = false
      },
      containerColor = CyberCard,
      shape = RoundedCornerShape(20.dp),
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.CloudQueue, contentDescription = null, tint = NeonCyan)
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "Android Studio & Firebase Setup", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column {
          Text(
            text = "Alex's Room is ready for direct Firebase Authentication & Cloud Firestore synchronization!",
            color = TextSecondary,
            fontSize = 12.sp
          )
          Spacer(modifier = Modifier.height(10.dp))
          Surface(
            color = CyberBgElevated,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = FirebaseConfigHelper.FIREBASE_SETUP_INSTRUCTIONS,
              color = NeonCyan,
              fontSize = 11.sp,
              lineHeight = 16.sp,
              modifier = Modifier.padding(10.dp)
            )
          }
          if (copiedFeedback) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Copied instructions to clipboard!", color = NeonEmerald, fontSize = 11.sp)
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            clipboardManager.setText(AnnotatedString(FirebaseConfigHelper.FIREBASE_SETUP_INSTRUCTIONS))
            copiedFeedback = true
          },
          colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = CyberBg)
        ) {
          Text("Copy Instructions")
        }
      },
      dismissButton = {
        TextButton(onClick = {
          viewModel.toggleFirebaseDialog(false)
          copiedFeedback = false
        }) {
          Text("Close", color = TextSecondary)
        }
      }
    )
  }

  // Create Room Modal
  if (showCreateRoomDialog) {
    var roomTitle by remember { mutableStateOf("") }
    var roomSubject by remember { mutableStateOf("AP Physics & Calculus") }
    var roomTheme by remember { mutableStateOf("Neon Lo-Fi Beats") }

    AlertDialog(
      onDismissRequest = { viewModel.toggleCreateRoomDialog(false) },
      containerColor = CyberCard,
      shape = RoundedCornerShape(20.dp),
      title = { Text(text = "Create Study Room", color = TextPrimary, fontWeight = FontWeight.Bold) },
      text = {
        Column {
          OutlinedTextField(
            value = roomTitle,
            onValueChange = { roomTitle = it },
            label = { Text("Room Title") },
            placeholder = { Text("e.g. Calculus BC Finals Prep") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("create_room_title_input"),
            singleLine = true
          )
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = roomSubject,
            onValueChange = { roomSubject = it },
            label = { Text("Subject / Class") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = roomTheme,
            onValueChange = { roomTheme = it },
            label = { Text("Ambient Audio / Theme") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (roomTitle.isNotBlank()) {
              viewModel.createStudyRoom(roomTitle, roomSubject, roomTheme)
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = CyberBg),
          modifier = Modifier.testTag("create_room_submit_button")
        ) {
          Text("Create Room")
        }
      },
      dismissButton = {
        TextButton(onClick = { viewModel.toggleCreateRoomDialog(false) }) {
          Text("Cancel", color = TextSecondary)
        }
      }
    )
  }
}
