package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FriendUser
import com.example.model.UserStatus
import com.example.ui.components.CyberAvatar
import com.example.ui.components.GlowingCard
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel
import com.example.viewmodel.ScreenTab

@Composable
fun FriendsScreen(
  viewModel: AppViewModel,
  modifier: Modifier = Modifier
) {
  val friends by viewModel.friends.collectAsState()
  val searchQuery by viewModel.friendSearchQuery.collectAsState()
  var selectedTab by remember { mutableStateOf(0) } // 0 = My Friends, 1 = Requests, 2 = Find Friends
  var selectedFriendForProfile by remember { mutableStateOf<FriendUser?>(null) }

  val acceptedFriends = friends.filter { it.isFriend }
  val incomingRequests = friends.filter { it.hasPendingIncomingRequest }
  val searchResults = friends.filter {
    searchQuery.isNotBlank() && it.username.contains(searchQuery, ignoreCase = true)
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(CyberBg)
  ) {
    // Search Bar
    Surface(
      color = CyberBgElevated,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = {
            viewModel.setFriendSearch(it)
            if (it.isNotBlank()) selectedTab = 2
          },
          placeholder = { Text("Search friends by username or subject...", color = TextMuted) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { viewModel.setFriendSearch("") }) {
                Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary)
              }
            }
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("friends_search_input"),
          shape = RoundedCornerShape(14.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = CyberCardBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs Row
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CyberCard)
            .padding(4.dp),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          TabButton(
            label = "Friends (${acceptedFriends.size})",
            isSelected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            modifier = Modifier.weight(1f)
          )
          TabButton(
            label = "Requests (${incomingRequests.size})",
            isSelected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            hasBadge = incomingRequests.isNotEmpty(),
            modifier = Modifier.weight(1f)
          )
          TabButton(
            label = "Add Friends",
            isSelected = selectedTab == 2,
            onClick = { selectedTab = 2 },
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // Content List based on Tab
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      when (selectedTab) {
        0 -> { // My Friends
          if (acceptedFriends.isEmpty()) {
            item {
              EmptyState(
                icon = Icons.Outlined.People,
                title = "No Friends Added Yet",
                desc = "Search for your classmates or accept incoming requests."
              )
            }
          } else {
            items(acceptedFriends) { friend ->
              FriendCard(
                friend = friend,
                onChat = { viewModel.openChat(friend.id) },
                onViewProfile = { selectedFriendForProfile = friend },
                onChallenge = {
                  viewModel.openChat(friend.id)
                  viewModel.sendMessage("🎮 Challenge: Let's play Tic Tac Toe or Rock Paper Scissors!")
                }
              )
            }
          }
        }

        1 -> { // Requests
          if (incomingRequests.isEmpty()) {
            item {
              EmptyState(
                icon = Icons.Outlined.MailOutline,
                title = "No Pending Requests",
                desc = "You're all caught up! New classmate invites will show up here."
              )
            }
          } else {
            items(incomingRequests) { request ->
              RequestCard(
                friend = request,
                onAccept = { viewModel.acceptFriendRequest(request.id) },
                onReject = { viewModel.rejectFriendRequest(request.id) }
              )
            }
          }
        }

        2 -> { // Add Friends / Search
          val listToShow = if (searchQuery.isNotBlank()) searchResults else friends.filterNot { it.isFriend }

          if (listToShow.isEmpty()) {
            item {
              EmptyState(
                icon = Icons.Outlined.PersonSearch,
                title = "No Users Found",
                desc = "Try searching for Chloe_Sci, Marcus_Apex, Sophia_Bio, or Liam_Math"
              )
            }
          } else {
            items(listToShow) { user ->
              AddFriendCard(
                friend = user,
                onSendRequest = { viewModel.sendFriendRequest(user.id) }
              )
            }
          }
        }
      }
    }
  }

  // Friend Profile Dialog
  if (selectedFriendForProfile != null) {
    val f = selectedFriendForProfile!!
    AlertDialog(
      onDismissRequest = { selectedFriendForProfile = null },
      containerColor = CyberCard,
      shape = RoundedCornerShape(20.dp),
      title = null,
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          CyberAvatar(avatarId = f.avatarId, status = f.status, size = 72.dp)
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = f.username,
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
          )
          Surface(
            color = NeonCyan.copy(alpha = 0.15f),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.padding(top = 4.dp)
          ) {
            Text(
              text = f.schoolTag,
              color = NeonCyan,
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Status: ${f.activityText}",
            color = TextSecondary,
            fontSize = 13.sp
          )

          Spacer(modifier = Modifier.height(20.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = {
                val fId = f.id
                selectedFriendForProfile = null
                viewModel.openChat(fId)
              },
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = CyberBg),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Chat", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
              onClick = {
                val fId = f.id
                selectedFriendForProfile = null
                viewModel.openChat(fId)
                viewModel.sendMessage("🎮 Challenge: Game match invitation sent!")
                viewModel.setTab(ScreenTab.GAMES)
              },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonPurple)
            ) {
              Icon(Icons.Default.SportsEsports, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Play Game", fontWeight = FontWeight.Bold)
            }
          }
        }
      },
      confirmButton = {}
    )
  }
}

@Composable
private fun TabButton(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  hasBadge: Boolean = false,
  modifier: Modifier = Modifier
) {
  Button(
    onClick = onClick,
    modifier = modifier.height(38.dp),
    shape = RoundedCornerShape(8.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = if (isSelected) NeonCyan else Color.Transparent,
      contentColor = if (isSelected) CyberBg else TextSecondary
    ),
    contentPadding = PaddingValues(horizontal = 6.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = label,
        fontSize = 11.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
      )
      if (hasBadge) {
        Spacer(modifier = Modifier.width(4.dp))
        Box(
          modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(NeonCrimson)
        )
      }
    }
  }
}

@Composable
private fun FriendCard(
  friend: FriendUser,
  onChat: () -> Unit,
  onViewProfile: () -> Unit,
  onChallenge: () -> Unit
) {
  GlowingCard(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("friend_card_${friend.id}"),
    onClick = onViewProfile
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      CyberAvatar(
        avatarId = friend.avatarId,
        status = friend.status,
        size = 46.dp,
        onClick = onViewProfile
      )
      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = friend.username,
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            color = NeonPurple.copy(alpha = 0.2f),
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = friend.schoolTag,
              color = NeonPurple,
              fontSize = 9.sp,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
          }
        }
        Text(
          text = friend.activityText,
          color = TextSecondary,
          fontSize = 12.sp
        )
      }

      IconButton(
        onClick = onChallenge,
        modifier = Modifier.testTag("friend_challenge_${friend.id}")
      ) {
        Icon(Icons.Default.SportsEsports, contentDescription = "Challenge to Game", tint = NeonPurple)
      }

      IconButton(
        onClick = onChat,
        modifier = Modifier.testTag("friend_chat_${friend.id}")
      ) {
        Icon(Icons.Default.Chat, contentDescription = "Start Chat", tint = NeonCyan)
      }
    }
  }
}

@Composable
private fun RequestCard(
  friend: FriendUser,
  onAccept: () -> Unit,
  onReject: () -> Unit
) {
  GlowingCard(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      CyberAvatar(avatarId = friend.avatarId, status = friend.status, size = 44.dp)
      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = friend.username,
          color = TextPrimary,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "School: ${friend.schoolTag}",
          color = TextSecondary,
          fontSize = 12.sp
        )
      }

      IconButton(onClick = onReject) {
        Icon(Icons.Default.Close, contentDescription = "Reject", tint = NeonCrimson)
      }
      IconButton(onClick = onAccept) {
        Icon(Icons.Default.Check, contentDescription = "Accept", tint = NeonEmerald)
      }
    }
  }
}

@Composable
private fun AddFriendCard(
  friend: FriendUser,
  onSendRequest: () -> Unit
) {
  GlowingCard(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      CyberAvatar(avatarId = friend.avatarId, status = friend.status, size = 44.dp)
      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = friend.username,
          color = TextPrimary,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "${friend.schoolTag} • ${friend.activityText}",
          color = TextSecondary,
          fontSize = 12.sp
        )
      }

      if (friend.hasPendingOutgoingRequest) {
        Text(
          text = "Requested",
          color = TextMuted,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold
        )
      } else {
        Button(
          onClick = onSendRequest,
          colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = CyberBg),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun EmptyState(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  desc: String
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 40.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Icon(imageVector = icon, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
    Spacer(modifier = Modifier.height(12.dp))
    Text(text = title, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = desc,
      color = TextSecondary,
      fontSize = 13.sp,
      textAlign = androidx.compose.ui.text.style.TextAlign.Center,
      modifier = Modifier.padding(horizontal = 32.dp)
    )
  }
}
