package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ScreenTab {
  HOME, FRIENDS, STUDY, GAMES, PROFILE
}

class AppViewModel(application: Application) : AndroidViewModel(application) {
  val repository = AppRepository(application.applicationContext)

  val currentUser = repository.currentUser
  val isLoggedIn = repository.isLoggedIn
  val friends = repository.friends
  val chats = repository.chats
  val typingFriendId = repository.typingFriendId
  val studyRooms = repository.studyRooms
  val notifications = repository.notifications
  val ticTacToe = repository.ticTacToe
  val rpsGame = repository.rpsGame
  val numberGuess = repository.numberGuess

  // Navigation states
  private val _currentTab = MutableStateFlow(ScreenTab.HOME)
  val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

  private val _activeChatFriendId = MutableStateFlow<String?>(null)
  val activeChatFriendId: StateFlow<String?> = _activeChatFriendId.asStateFlow()

  private val _activeStudyRoomId = MutableStateFlow<String?>("room_calc")
  val activeStudyRoomId: StateFlow<String?> = _activeStudyRoomId.asStateFlow()

  private val _showSettings = MutableStateFlow(false)
  val showSettings: StateFlow<Boolean> = _showSettings.asStateFlow()

  private val _showNotifications = MutableStateFlow(false)
  val showNotifications: StateFlow<Boolean> = _showNotifications.asStateFlow()

  private val _showFirebaseDialog = MutableStateFlow(false)
  val showFirebaseDialog: StateFlow<Boolean> = _showFirebaseDialog.asStateFlow()

  private val _showCreateRoomDialog = MutableStateFlow(false)
  val showCreateRoomDialog: StateFlow<Boolean> = _showCreateRoomDialog.asStateFlow()

  // Focus Timer inside Study Room
  private val _timerRunning = MutableStateFlow(false)
  val timerRunning: StateFlow<Boolean> = _timerRunning.asStateFlow()

  private val _timerSecondsLeft = MutableStateFlow(25 * 60)
  val timerSecondsLeft: StateFlow<Int> = _timerSecondsLeft.asStateFlow()

  private val _timerTotalSeconds = MutableStateFlow(25 * 60)
  val timerTotalSeconds: StateFlow<Int> = _timerTotalSeconds.asStateFlow()

  private var timerJob: Job? = null

  // Friends search query
  private val _friendSearchQuery = MutableStateFlow("")
  val friendSearchQuery: StateFlow<String> = _friendSearchQuery.asStateFlow()

  fun setTab(tab: ScreenTab) {
    _currentTab.value = tab
    _activeChatFriendId.value = null
    _showSettings.value = false
    _showNotifications.value = false
  }

  fun openChat(friendId: String) {
    _activeChatFriendId.value = friendId
  }

  fun closeChat() {
    _activeChatFriendId.value = null
  }

  fun openStudyRoom(roomId: String) {
    _activeStudyRoomId.value = roomId
    _currentTab.value = ScreenTab.STUDY
  }

  fun toggleSettings(show: Boolean) {
    _showSettings.value = show
  }

  fun toggleNotifications(show: Boolean) {
    _showNotifications.value = show
    if (show) {
      repository.markAllNotificationsRead()
    }
  }

  fun toggleFirebaseDialog(show: Boolean) {
    _showFirebaseDialog.value = show
  }

  fun toggleCreateRoomDialog(show: Boolean) {
    _showCreateRoomDialog.value = show
  }

  fun setFriendSearch(query: String) {
    _friendSearchQuery.value = query
  }

  // Timer controls
  fun startTimer() {
    if (_timerRunning.value) return
    _timerRunning.value = true
    timerJob = viewModelScope.launch {
      while (_timerSecondsLeft.value > 0 && _timerRunning.value) {
        delay(1000)
        _timerSecondsLeft.value -= 1
      }
      if (_timerSecondsLeft.value <= 0) {
        _timerRunning.value = false
        // Add completed minutes to user profile
        repository.addFocusMinutes(_timerTotalSeconds.value / 60)
      }
    }
  }

  fun pauseTimer() {
    _timerRunning.value = false
    timerJob?.cancel()
  }

  fun resetTimer(minutes: Int = 25) {
    pauseTimer()
    _timerTotalSeconds.value = minutes * 60
    _timerSecondsLeft.value = minutes * 60
  }

  fun setTimerDuration(minutes: Int) {
    resetTimer(minutes)
  }

  // Actions delegate to repository
  fun login(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
    repository.login(email, pass, onResult)
  }

  fun register(username: String, email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
    repository.register(username, email, pass, onResult)
  }

  fun resetPassword(email: String, onResult: (Boolean, String?) -> Unit) {
    repository.resetPassword(email, onResult)
  }

  fun logout() {
    repository.logout()
  }

  fun sendMessage(text: String) {
    val fId = _activeChatFriendId.value ?: return
    repository.sendMessage(fId, text)
  }

  fun sendFriendRequest(friendId: String) = repository.sendFriendRequest(friendId)
  fun acceptFriendRequest(friendId: String) = repository.acceptFriendRequest(friendId)
  fun rejectFriendRequest(friendId: String) = repository.rejectFriendRequest(friendId)

  fun createStudyRoom(title: String, subject: String, ambient: String) {
    repository.createStudyRoom(title, subject, ambient)
    _showCreateRoomDialog.value = false
  }

  fun addTask(roomId: String, title: String) = repository.addTaskToRoom(roomId, title)
  fun toggleTask(roomId: String, taskId: String) = repository.toggleTaskCompletion(roomId, taskId)
  fun saveNote(roomId: String, noteId: String?, title: String, content: String) =
    repository.saveRoomNote(roomId, noteId, title, content)

  fun updateProfile(username: String, bio: String, grade: String, avatarId: String) =
    repository.updateProfile(username, bio, grade, avatarId)

  // Game actions
  fun playTicTacToe(index: Int) = repository.playTicTacToeMove(index)
  fun resetTicTacToe(toggleAiMode: Boolean = false) = repository.resetTicTacToe(toggleAiMode)

  fun playRps(choice: RpsChoice) = repository.playRps(choice)
  fun resetRps() = repository.resetRps()

  fun submitGuess(number: Int) = repository.submitGuess(number)
  fun restartNumberGuess() = repository.restartNumberGuess()
}
