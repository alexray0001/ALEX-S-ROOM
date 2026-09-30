package com.example.model

enum class UserStatus {
  ONLINE, IDLE, OFFLINE
}

data class UserProfile(
  val id: String = "user_me",
  val username: String = "Alex_Gamer",
  val email: String = "alex@schoolverse.dev",
  val avatarId: String = "avatar_cyber_ninja",
  val bio: String = "High school senior 🚀 Studying AP Physics & grinding competitive code.",
  val grade: String = "12th Grade",
  val status: UserStatus = UserStatus.ONLINE,
  val activityText: String = "Studying Physics in Room #101",
  val focusMinutes: Int = 345,
  val studyStreakDays: Int = 7,
  val completedTasksCount: Int = 18,
  val gamesWonCount: Int = 14
)

data class FriendUser(
  val id: String,
  val username: String,
  val avatarId: String,
  val status: UserStatus,
  val activityText: String,
  val isFriend: Boolean = true,
  val hasPendingOutgoingRequest: Boolean = false,
  val hasPendingIncomingRequest: Boolean = false,
  val schoolTag: String = "High School"
)

data class ChatMessage(
  val id: String,
  val senderId: String,
  val senderName: String,
  val text: String,
  val timestamp: Long = System.currentTimeMillis(),
  val isMine: Boolean = false
)

data class StudyTask(
  val id: String,
  val title: String,
  val isCompleted: Boolean = false,
  val assignedTo: String = "Everyone"
)

data class StudyNote(
  val id: String,
  val title: String,
  val content: String,
  val lastEditedBy: String,
  val updatedAt: Long = System.currentTimeMillis()
)

data class StudyRoom(
  val id: String,
  val title: String,
  val subject: String,
  val hostId: String,
  val hostName: String,
  val memberNames: List<String>,
  val ambientTheme: String = "Cyber Lo-Fi",
  val activeTimerMinutes: Int = 25,
  val tasks: List<StudyTask> = emptyList(),
  val notes: List<StudyNote> = emptyList()
)

enum class NotificationType {
  FRIEND_REQUEST,
  NEW_MESSAGE,
  ROOM_INVITE,
  SYSTEM
}

data class AppNotification(
  val id: String,
  val title: String,
  val message: String,
  val type: NotificationType,
  val timestamp: Long = System.currentTimeMillis(),
  val isRead: Boolean = false,
  val senderId: String? = null,
  val roomId: String? = null
)

// Game models
enum class TicTacToeCell {
  EMPTY, X, O
}

data class TicTacToeState(
  val board: List<TicTacToeCell> = List(9) { TicTacToeCell.EMPTY },
  val isXTurn: Boolean = true,
  val winner: TicTacToeCell? = null, // null = in progress, EMPTY = draw
  val winningLine: List<Int>? = null,
  val vsAi: Boolean = true,
  val xScore: Int = 0,
  val oScore: Int = 0,
  val tiesCount: Int = 0
)

enum class RpsChoice {
  ROCK, PAPER, SCISSORS
}

enum class RpsResult {
  WIN, LOSE, DRAW, PENDING
}

data class RpsGameState(
  val playerChoice: RpsChoice? = null,
  val opponentChoice: RpsChoice? = null,
  val result: RpsResult = RpsResult.PENDING,
  val streak: Int = 0,
  val bestStreak: Int = 0,
  val playerWins: Int = 0,
  val botWins: Int = 0
)

data class NumberGuessState(
  val targetNumber: Int = (1..100).random(),
  val attempts: List<Pair<Int, String>> = emptyList(), // Guess -> Feedback
  val isGameOver: Boolean = false,
  val hasWon: Boolean = false,
  val maxAttempts: Int = 8,
  val bestScore: Int = 999
)
