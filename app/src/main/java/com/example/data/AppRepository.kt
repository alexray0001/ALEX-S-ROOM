package com.example.data

import android.content.Context
import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AppRepository(private val context: Context) {
  private val scope = CoroutineScope(Dispatchers.Main)

  // Current user state
  private val _currentUser = MutableStateFlow(
    UserProfile(
      id = "user_alex",
      username = "Alex",
      email = "alex@schoolverse.dev",
      avatarId = "neon_ninja",
      bio = "🎮 Coding games, grinding AP Calc & Physics. Welcome to my room!",
      grade = "Class of 2026",
      status = UserStatus.ONLINE,
      activityText = "Study Room #1 - Calculus Finals",
      focusMinutes = 240,
      studyStreakDays = 5,
      completedTasksCount = 14,
      gamesWonCount = 28
    )
  )
  val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

  private val _isLoggedIn = MutableStateFlow(true)
  val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

  // Friends
  private val _friends = MutableStateFlow<List<FriendUser>>(
    listOf(
      FriendUser(
        id = "f_chloe",
        username = "Chloe_Sci",
        avatarId = "cyber_cat",
        status = UserStatus.ONLINE,
        activityText = "Studying Physics ⚡",
        isFriend = true,
        schoolTag = "AP Physics"
      ),
      FriendUser(
        id = "f_marcus",
        username = "Marcus_Apex",
        avatarId = "pixel_warrior",
        status = UserStatus.ONLINE,
        activityText = "In Room: Coding Club 💻",
        isFriend = true,
        schoolTag = "Comp Sci"
      ),
      FriendUser(
        id = "f_elena",
        username = "Elena_Art",
        avatarId = "cosmic_girl",
        status = UserStatus.IDLE,
        activityText = "Listening to Lo-Fi 🎧",
        isFriend = true,
        schoolTag = "Art & Lit"
      ),
      FriendUser(
        id = "f_liam",
        username = "Liam_Math",
        avatarId = "robo_geek",
        status = UserStatus.OFFLINE,
        activityText = "Offline (Active 2h ago)",
        isFriend = true,
        schoolTag = "Calculus"
      ),
      FriendUser(
        id = "f_sophia",
        username = "Sophia_Bio",
        avatarId = "gamer_girl",
        status = UserStatus.ONLINE,
        activityText = "Biology Flashcards 🧬",
        isFriend = false,
        hasPendingIncomingRequest = true,
        schoolTag = "AP Bio"
      ),
      FriendUser(
        id = "f_jordan",
        username = "Jordan_Pro",
        avatarId = "cyber_skater",
        status = UserStatus.ONLINE,
        activityText = "Challenging Tic Tac Toe 🎮",
        isFriend = false,
        hasPendingOutgoingRequest = false,
        schoolTag = "History"
      )
    )
  )
  val friends: StateFlow<List<FriendUser>> = _friends.asStateFlow()

  // Chat messages by friend ID
  private val _chats = MutableStateFlow<Map<String, List<ChatMessage>>>(
    mapOf(
      "f_chloe" to listOf(
        ChatMessage(
          id = "m1",
          senderId = "f_chloe",
          senderName = "Chloe_Sci",
          text = "Hey Alex! Did you finish problem set #4 on electromagnetism?",
          timestamp = System.currentTimeMillis() - 1000 * 60 * 18,
          isMine = false
        ),
        ChatMessage(
          id = "m2",
          senderId = "user_alex",
          senderName = "Alex",
          text = "Yeah, almost done! Just reviewing question 7. Want to join my study room?",
          timestamp = System.currentTimeMillis() - 1000 * 60 * 10,
          isMine = true
        ),
        ChatMessage(
          id = "m3",
          senderId = "f_chloe",
          senderName = "Chloe_Sci",
          text = "Awesome, hop in! Let's start a 25-min focus timer.",
          timestamp = System.currentTimeMillis() - 1000 * 60 * 3,
          isMine = false
        )
      ),
      "f_marcus" to listOf(
        ChatMessage(
          id = "m4",
          senderId = "f_marcus",
          senderName = "Marcus_Apex",
          text = "Yo Alex, up for a quick Rock Paper Scissors or Tic Tac Toe?",
          timestamp = System.currentTimeMillis() - 1000 * 60 * 45,
          isMine = false
        )
      )
    )
  )
  val chats: StateFlow<Map<String, List<ChatMessage>>> = _chats.asStateFlow()

  // Typing indicator per friend
  private val _typingFriendId = MutableStateFlow<String?>(null)
  val typingFriendId: StateFlow<String?> = _typingFriendId.asStateFlow()

  // Study Rooms
  private val _studyRooms = MutableStateFlow<List<StudyRoom>>(
    listOf(
      StudyRoom(
        id = "room_calc",
        title = "Calculus BC & Physics Finals",
        subject = "STEM",
        hostId = "user_alex",
        hostName = "Alex",
        memberNames = listOf("Alex", "Chloe_Sci", "Liam_Math"),
        ambientTheme = "Neon Lo-Fi Beats",
        activeTimerMinutes = 25,
        tasks = listOf(
          StudyTask(id = "t1", title = "Review Taylor series formulas", isCompleted = true, assignedTo = "Alex"),
          StudyTask(id = "t2", title = "Solve electric flux integrals (Q1-5)", isCompleted = false, assignedTo = "Chloe_Sci"),
          StudyTask(id = "t3", title = "Verify unit circle radian shortcuts", isCompleted = false, assignedTo = "Everyone")
        ),
        notes = listOf(
          StudyNote(
            id = "n1",
            title = "Formula Cheat Sheet",
            content = "1. E-field = k*q/r^2\n2. Gauss Law: Flux = Q_enc / epsilon_0\n3. d/dx [sin(x)] = cos(x)\n4. Integral of 1/x dx = ln|x| + C",
            lastEditedBy = "Chloe_Sci"
          )
        )
      ),
      StudyRoom(
        id = "room_cs",
        title = "Android & Kotlin Dev Club",
        subject = "Computer Science",
        hostId = "f_marcus",
        hostName = "Marcus_Apex",
        memberNames = listOf("Marcus_Apex", "Elena_Art"),
        ambientTheme = "Cyber Synthwave",
        activeTimerMinutes = 45,
        tasks = listOf(
          StudyTask(id = "t4", title = "Build Jetpack Compose navigation", isCompleted = true, assignedTo = "Marcus_Apex"),
          StudyTask(id = "t5", title = "Implement local Room database", isCompleted = false, assignedTo = "Everyone")
        ),
        notes = listOf(
          StudyNote(
            id = "n2",
            title = "Jetpack Compose Best Practices",
            content = "- Use Scaffold with navigation bars insets\n- State hoisting in ViewModels\n- Custom canvas glowing lines for game UI",
            lastEditedBy = "Marcus_Apex"
          )
        )
      )
    )
  )
  val studyRooms: StateFlow<List<StudyRoom>> = _studyRooms.asStateFlow()

  // Notifications
  private val _notifications = MutableStateFlow<List<AppNotification>>(
    listOf(
      AppNotification(
        id = "notif_1",
        title = "Friend Request",
        message = "Sophia_Bio sent you a friend request from 11th Grade Bio.",
        type = NotificationType.FRIEND_REQUEST,
        timestamp = System.currentTimeMillis() - 1000 * 60 * 15,
        senderId = "f_sophia"
      ),
      AppNotification(
        id = "notif_2",
        title = "Study Room Invite",
        message = "Marcus_Apex invited you to 'Android & Kotlin Dev Club'.",
        type = NotificationType.ROOM_INVITE,
        timestamp = System.currentTimeMillis() - 1000 * 60 * 60,
        roomId = "room_cs"
      ),
      AppNotification(
        id = "notif_3",
        title = "New Message",
        message = "Chloe_Sci: 'Awesome, hop in! Let's start a 25-min focus timer.'",
        type = NotificationType.NEW_MESSAGE,
        timestamp = System.currentTimeMillis() - 1000 * 60 * 3,
        senderId = "f_chloe"
      ),
      AppNotification(
        id = "notif_4",
        title = "Daily Study Streak",
        message = "🔥 7-day study streak reached! Keep up the great focus!",
        type = NotificationType.SYSTEM,
        timestamp = System.currentTimeMillis() - 1000 * 60 * 180
      )
    )
  )
  val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

  // Games State
  private val _ticTacToe = MutableStateFlow(TicTacToeState())
  val ticTacToe: StateFlow<TicTacToeState> = _ticTacToe.asStateFlow()

  private val _rpsGame = MutableStateFlow(RpsGameState())
  val rpsGame: StateFlow<RpsGameState> = _rpsGame.asStateFlow()

  private val _numberGuess = MutableStateFlow(NumberGuessState())
  val numberGuess: StateFlow<NumberGuessState> = _numberGuess.asStateFlow()

  // Auth Operations
  fun login(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
    if (email.isBlank() || pass.length < 4) {
      onResult(false, "Please provide a valid email and at least 4 characters password.")
      return
    }

    val auth = FirebaseConfigHelper.getAuth(context)
    if (auth != null) {
      auth.signInWithEmailAndPassword(email, pass)
        .addOnSuccessListener {
          _isLoggedIn.value = true
          _currentUser.value = _currentUser.value.copy(
            email = email,
            username = email.substringBefore("@")
          )
          onResult(true, null)
        }
        .addOnFailureListener { e ->
          onResult(false, e.localizedMessage ?: "Authentication failed")
        }
    } else {
      // Local Interactive Fallback mode
      _isLoggedIn.value = true
      _currentUser.value = _currentUser.value.copy(
        email = email,
        username = email.substringBefore("@").replaceFirstChar { it.uppercase() }
      )
      onResult(true, null)
    }
  }

  fun register(username: String, email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
    if (username.isBlank() || email.isBlank() || pass.length < 4) {
      onResult(false, "All fields are required (password min 4 chars).")
      return
    }

    val auth = FirebaseConfigHelper.getAuth(context)
    if (auth != null) {
      auth.createUserWithEmailAndPassword(email, pass)
        .addOnSuccessListener {
          _isLoggedIn.value = true
          _currentUser.value = _currentUser.value.copy(
            username = username,
            email = email
          )
          onResult(true, null)
        }
        .addOnFailureListener { e ->
          onResult(false, e.localizedMessage ?: "Registration failed")
        }
    } else {
      _isLoggedIn.value = true
      _currentUser.value = _currentUser.value.copy(
        username = username,
        email = email
      )
      onResult(true, null)
    }
  }

  fun resetPassword(email: String, onResult: (Boolean, String?) -> Unit) {
    if (email.isBlank()) {
      onResult(false, "Please enter your email.")
      return
    }
    val auth = FirebaseConfigHelper.getAuth(context)
    if (auth != null) {
      auth.sendPasswordResetEmail(email)
        .addOnSuccessListener { onResult(true, "Password reset email sent!") }
        .addOnFailureListener { onResult(false, it.localizedMessage) }
    } else {
      onResult(true, "Password reset link simulated for $email")
    }
  }

  fun logout() {
    val auth = FirebaseConfigHelper.getAuth(context)
    auth?.signOut()
    _isLoggedIn.value = false
  }

  // Profile Updates
  fun updateProfile(username: String, bio: String, grade: String, avatarId: String) {
    _currentUser.value = _currentUser.value.copy(
      username = username,
      bio = bio,
      grade = grade,
      avatarId = avatarId
    )
  }

  // Friends & Requests
  fun sendFriendRequest(friendId: String) {
    _friends.value = _friends.value.map {
      if (it.id == friendId) it.copy(hasPendingOutgoingRequest = true) else it
    }
  }

  fun acceptFriendRequest(friendId: String) {
    _friends.value = _friends.value.map {
      if (it.id == friendId) it.copy(isFriend = true, hasPendingIncomingRequest = false) else it
    }
    _notifications.value = _notifications.value.filterNot { it.senderId == friendId }
  }

  fun rejectFriendRequest(friendId: String) {
    _friends.value = _friends.value.map {
      if (it.id == friendId) it.copy(hasPendingIncomingRequest = false) else it
    }
    _notifications.value = _notifications.value.filterNot { it.senderId == friendId }
  }

  // Chat Messaging
  fun sendMessage(friendId: String, text: String) {
    if (text.isBlank()) return

    val currentList = _chats.value[friendId].orEmpty()
    val myMessage = ChatMessage(
      id = UUID.randomUUID().toString(),
      senderId = _currentUser.value.id,
      senderName = _currentUser.value.username,
      text = text.trim(),
      timestamp = System.currentTimeMillis(),
      isMine = true
    )

    val updatedMap = _chats.value.toMutableMap()
    updatedMap[friendId] = currentList + myMessage
    _chats.value = updatedMap

    // Simulate friend response if chatting with school friends
    val targetFriend = _friends.value.find { it.id == friendId }
    if (targetFriend != null) {
      scope.launch {
        delay(900)
        _typingFriendId.value = friendId
        delay(1600)
        _typingFriendId.value = null

        val friendReplies = listOf(
          "Got it! Let's conquer this homework together 💪",
          "Nice! I'll test that out in our study room right now.",
          "Awesome idea! Want to play a round of Rock Paper Scissors after this?",
          "Thanks for the update! See you in Alex's Room!",
          "Great points! Added it to our study group notes."
        )
        val replyText = friendReplies.random()

        val replyMsg = ChatMessage(
          id = UUID.randomUUID().toString(),
          senderId = friendId,
          senderName = targetFriend.username,
          text = replyText,
          timestamp = System.currentTimeMillis(),
          isMine = false
        )

        val freshMap = _chats.value.toMutableMap()
        freshMap[friendId] = freshMap[friendId].orEmpty() + replyMsg
        _chats.value = freshMap
      }
    }
  }

  // Study Rooms
  fun createStudyRoom(title: String, subject: String, ambient: String) {
    val newRoom = StudyRoom(
      id = "room_" + UUID.randomUUID().toString().take(6),
      title = title,
      subject = subject,
      hostId = _currentUser.value.id,
      hostName = _currentUser.value.username,
      memberNames = listOf(_currentUser.value.username),
      ambientTheme = ambient,
      activeTimerMinutes = 25,
      tasks = listOf(
        StudyTask(id = UUID.randomUUID().toString(), title = "Start session focus", isCompleted = false)
      ),
      notes = emptyList()
    )
    _studyRooms.value = listOf(newRoom) + _studyRooms.value
  }

  fun addTaskToRoom(roomId: String, title: String) {
    if (title.isBlank()) return
    _studyRooms.value = _studyRooms.value.map { room ->
      if (room.id == roomId) {
        val newTask = StudyTask(
          id = UUID.randomUUID().toString(),
          title = title.trim(),
          isCompleted = false,
          assignedTo = _currentUser.value.username
        )
        room.copy(tasks = room.tasks + newTask)
      } else room
    }
  }

  fun toggleTaskCompletion(roomId: String, taskId: String) {
    _studyRooms.value = _studyRooms.value.map { room ->
      if (room.id == roomId) {
        room.copy(
          tasks = room.tasks.map { task ->
            if (task.id == taskId) {
              val nextDone = !task.isCompleted
              if (nextDone) {
                _currentUser.value = _currentUser.value.copy(
                  completedTasksCount = _currentUser.value.completedTasksCount + 1
                )
              }
              task.copy(isCompleted = nextDone)
            } else task
          }
        )
      } else room
    }
  }

  fun saveRoomNote(roomId: String, noteId: String?, title: String, content: String) {
    _studyRooms.value = _studyRooms.value.map { room ->
      if (room.id == roomId) {
        val existingIndex = room.notes.indexOfFirst { it.id == noteId }
        val updatedNotes = if (existingIndex >= 0) {
          room.notes.toMutableList().apply {
            this[existingIndex] = this[existingIndex].copy(
              title = title,
              content = content,
              lastEditedBy = _currentUser.value.username,
              updatedAt = System.currentTimeMillis()
            )
          }
        } else {
          room.notes + StudyNote(
            id = UUID.randomUUID().toString(),
            title = title.ifBlank { "Study Notes" },
            content = content,
            lastEditedBy = _currentUser.value.username,
            updatedAt = System.currentTimeMillis()
          )
        }
        room.copy(notes = updatedNotes)
      } else room
    }
  }

  fun addFocusMinutes(minutes: Int) {
    _currentUser.value = _currentUser.value.copy(
      focusMinutes = _currentUser.value.focusMinutes + minutes
    )
  }

  // Games Logic - Tic Tac Toe
  fun playTicTacToeMove(index: Int) {
    val state = _ticTacToe.value
    if (state.board[index] != TicTacToeCell.EMPTY || state.winner != null) return

    val currentTurn = if (state.isXTurn) TicTacToeCell.X else TicTacToeCell.O
    val newBoard = state.board.toMutableList()
    newBoard[index] = currentTurn

    val winCheck = checkTicTacToeWinner(newBoard)
    if (winCheck != null) {
      val isXWin = winCheck.first == TicTacToeCell.X
      _ticTacToe.value = state.copy(
        board = newBoard,
        winner = winCheck.first,
        winningLine = winCheck.second,
        xScore = if (isXWin) state.xScore + 1 else state.xScore,
        oScore = if (!isXWin && winCheck.first == TicTacToeCell.O) state.oScore + 1 else state.oScore,
        tiesCount = if (winCheck.first == TicTacToeCell.EMPTY) state.tiesCount + 1 else state.tiesCount
      )
      if (isXWin) {
        _currentUser.value = _currentUser.value.copy(gamesWonCount = _currentUser.value.gamesWonCount + 1)
      }
      return
    }

    val isNextX = !state.isXTurn
    _ticTacToe.value = state.copy(board = newBoard, isXTurn = isNextX)

    // AI Move if single player mode
    if (state.vsAi && !isNextX) {
      scope.launch {
        delay(400)
        makeAiTicTacToeMove()
      }
    }
  }

  private fun makeAiTicTacToeMove() {
    val state = _ticTacToe.value
    if (state.winner != null || state.isXTurn) return

    val emptyIndices = state.board.indices.filter { state.board[it] == TicTacToeCell.EMPTY }
    if (emptyIndices.isEmpty()) return

    // Pick best move or random
    val move = emptyIndices.random()
    val newBoard = state.board.toMutableList()
    newBoard[move] = TicTacToeCell.O

    val winCheck = checkTicTacToeWinner(newBoard)
    if (winCheck != null) {
      val isOWin = winCheck.first == TicTacToeCell.O
      _ticTacToe.value = state.copy(
        board = newBoard,
        winner = winCheck.first,
        winningLine = winCheck.second,
        oScore = if (isOWin) state.oScore + 1 else state.oScore,
        tiesCount = if (winCheck.first == TicTacToeCell.EMPTY) state.tiesCount + 1 else state.tiesCount
      )
    } else {
      _ticTacToe.value = state.copy(board = newBoard, isXTurn = true)
    }
  }

  private fun checkTicTacToeWinner(board: List<TicTacToeCell>): Pair<TicTacToeCell, List<Int>>? {
    val lines = listOf(
      listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
      listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
      listOf(0, 4, 8), listOf(2, 4, 6)
    )
    for (line in lines) {
      val a = board[line[0]]
      val b = board[line[1]]
      val c = board[line[2]]
      if (a != TicTacToeCell.EMPTY && a == b && b == c) {
        return Pair(a, line)
      }
    }
    if (board.none { it == TicTacToeCell.EMPTY }) {
      return Pair(TicTacToeCell.EMPTY, emptyList()) // Draw
    }
    return null
  }

  fun resetTicTacToe(toggleMode: Boolean = false) {
    val cur = _ticTacToe.value
    _ticTacToe.value = TicTacToeState(
      vsAi = if (toggleMode) !cur.vsAi else cur.vsAi,
      xScore = cur.xScore,
      oScore = cur.oScore,
      tiesCount = cur.tiesCount
    )
  }

  // Rock Paper Scissors
  fun playRps(choice: RpsChoice) {
    val botChoice = RpsChoice.values().random()
    val result = when {
      choice == botChoice -> RpsResult.DRAW
      choice == RpsChoice.ROCK && botChoice == RpsChoice.SCISSORS -> RpsResult.WIN
      choice == RpsChoice.PAPER && botChoice == RpsChoice.ROCK -> RpsResult.WIN
      choice == RpsChoice.SCISSORS && botChoice == RpsChoice.PAPER -> RpsResult.WIN
      else -> RpsResult.LOSE
    }

    val cur = _rpsGame.value
    val newStreak = if (result == RpsResult.WIN) cur.streak + 1 else 0
    val newBest = maxOf(newStreak, cur.bestStreak)
    val pWins = if (result == RpsResult.WIN) cur.playerWins + 1 else cur.playerWins
    val bWins = if (result == RpsResult.LOSE) cur.botWins + 1 else cur.botWins

    _rpsGame.value = cur.copy(
      playerChoice = choice,
      opponentChoice = botChoice,
      result = result,
      streak = newStreak,
      bestStreak = newBest,
      playerWins = pWins,
      botWins = bWins
    )

    if (result == RpsResult.WIN) {
      _currentUser.value = _currentUser.value.copy(gamesWonCount = _currentUser.value.gamesWonCount + 1)
    }
  }

  fun resetRps() {
    val cur = _rpsGame.value
    _rpsGame.value = RpsGameState(bestStreak = cur.bestStreak, playerWins = cur.playerWins, botWins = cur.botWins)
  }

  // Number Guess Game
  fun submitGuess(number: Int) {
    val cur = _numberGuess.value
    if (cur.isGameOver) return

    val feedback = when {
      number == cur.targetNumber -> "🎉 BINGO! Target Decrypted!"
      (number - cur.targetNumber) in 1..5 -> "🔥 Too high, but scalding hot!"
      (cur.targetNumber - number) in 1..5 -> "🔥 Too low, but scalding hot!"
      number > cur.targetNumber -> "Too high! Try a lower number."
      else -> "Too low! Try a higher number."
    }

    val updatedAttempts = cur.attempts + Pair(number, feedback)
    val hasWon = number == cur.targetNumber
    val isOver = hasWon || updatedAttempts.size >= cur.maxAttempts
    val best = if (hasWon) minOf(cur.bestScore, updatedAttempts.size) else cur.bestScore

    _numberGuess.value = cur.copy(
      attempts = updatedAttempts,
      isGameOver = isOver,
      hasWon = hasWon,
      bestScore = best
    )

    if (hasWon) {
      _currentUser.value = _currentUser.value.copy(gamesWonCount = _currentUser.value.gamesWonCount + 1)
    }
  }

  fun restartNumberGuess() {
    val cur = _numberGuess.value
    _numberGuess.value = NumberGuessState(
      targetNumber = (1..100).random(),
      bestScore = cur.bestScore
    )
  }

  // Notifications
  fun markAllNotificationsRead() {
    _notifications.value = _notifications.value.map { it.copy(isRead = true) }
  }

  fun clearNotification(id: String) {
    _notifications.value = _notifications.value.filterNot { it.id == id }
  }
}
