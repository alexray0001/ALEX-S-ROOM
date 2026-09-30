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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.GlowingCard
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

@Composable
fun GamesScreen(
  viewModel: AppViewModel,
  modifier: Modifier = Modifier
) {
  var selectedGame by remember { mutableStateOf(0) } // 0 = Tic Tac Toe, 1 = Rock Paper Scissors, 2 = Number Guess

  val tttState by viewModel.ticTacToe.collectAsState()
  val rpsState by viewModel.rpsGame.collectAsState()
  val numState by viewModel.numberGuess.collectAsState()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(CyberBg),
    contentPadding = PaddingValues(bottom = 88.dp)
  ) {
    // Game Selection HUD Tabs
    item {
      Surface(
        color = CyberBgElevated,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "MINI GAMES ARCADE",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
          )
          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(CyberCard)
              .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            GameTabButton(
              title = "Tic Tac Toe",
              icon = Icons.Default.Grid3x3,
              isSelected = selectedGame == 0,
              onClick = { selectedGame = 0 },
              modifier = Modifier.weight(1f)
            )
            GameTabButton(
              title = "RPS Clash",
              icon = Icons.Default.FrontHand,
              isSelected = selectedGame == 1,
              onClick = { selectedGame = 1 },
              modifier = Modifier.weight(1f)
            )
            GameTabButton(
              title = "Code Breaker",
              icon = Icons.Default.Key,
              isSelected = selectedGame == 2,
              onClick = { selectedGame = 2 },
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }

    // Active Game View
    when (selectedGame) {
      0 -> {
        item {
          TicTacToeSection(state = tttState, viewModel = viewModel)
        }
      }
      1 -> {
        item {
          RockPaperScissorsSection(state = rpsState, viewModel = viewModel)
        }
      }
      2 -> {
        item {
          NumberGuessSection(state = numState, viewModel = viewModel)
        }
      }
    }
  }
}

@Composable
private fun GameTabButton(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Button(
    onClick = onClick,
    modifier = modifier.height(44.dp),
    shape = RoundedCornerShape(8.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = if (isSelected) NeonCyan else Color.Transparent,
      contentColor = if (isSelected) CyberBg else TextSecondary
    ),
    contentPadding = PaddingValues(horizontal = 4.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp))
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
      )
    }
  }
}

// ---------------------------------------------------------
// 1. TIC TAC TOE
// ---------------------------------------------------------
@Composable
private fun TicTacToeSection(
  state: TicTacToeState,
  viewModel: AppViewModel
) {
  GlowingCard(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp)
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Header & Mode Toggle
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(text = "TIC TAC TOE", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
          Text(
            text = if (state.vsAi) "Mode: Player (X) vs Bot (O)" else "Mode: 2-Player Pass & Play",
            color = NeonCyan,
            fontSize = 12.sp
          )
        }

        OutlinedButton(
          onClick = { viewModel.resetTicTacToe(toggleAiMode = true) },
          shape = RoundedCornerShape(8.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
        ) {
          Text(text = if (state.vsAi) "Switch to 2P" else "Switch to AI", fontSize = 11.sp)
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Scoreboard
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(CyberBgElevated)
          .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "Player (X)", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          Text(text = "${state.xScore}", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "Ties", color = TextMuted, fontSize = 11.sp)
          Text(text = "${state.tiesCount}", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = if (state.vsAi) "Bot (O)" else "Player (O)", color = NeonPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          Text(text = "${state.oScore}", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Turn / Winner Banner
      val statusText = when {
        state.winner == TicTacToeCell.X -> "🎉 PLAYER X WINS!"
        state.winner == TicTacToeCell.O -> "🤖 ${if (state.vsAi) "BOT" else "PLAYER O"} WINS!"
        state.winner == TicTacToeCell.EMPTY -> "🤝 IT'S A DRAW!"
        state.isXTurn -> "Turn: Player X"
        else -> if (state.vsAi) "Bot is calculating..." else "Turn: Player O"
      }

      Text(
        text = statusText,
        color = when (state.winner) {
          TicTacToeCell.X -> NeonCyan
          TicTacToeCell.O -> NeonPurple
          TicTacToeCell.EMPTY -> NeonAmber
          else -> TextPrimary
        },
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 4.dp)
      )

      Spacer(modifier = Modifier.height(12.dp))

      // 3x3 Grid
      Column(
        modifier = Modifier.size(270.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        for (row in 0..2) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            for (col in 0..2) {
              val index = row * 3 + col
              val cell = state.board[index]
              val isWinningCell = state.winningLine?.contains(index) == true

              Box(
                modifier = Modifier
                  .weight(1f)
                  .fillMaxHeight()
                  .clip(RoundedCornerShape(12.dp))
                  .background(
                    if (isWinningCell) NeonCyan.copy(alpha = 0.25f) else CyberBgElevated
                  )
                  .border(
                    1.5.dp,
                    if (isWinningCell) NeonCyan else CyberCardBorder,
                    RoundedCornerShape(12.dp)
                  )
                  .clickable(enabled = cell == TicTacToeCell.EMPTY && state.winner == null) {
                    viewModel.playTicTacToe(index)
                  }
                  .testTag("ttt_cell_$index"),
                contentAlignment = Alignment.Center
              ) {
                when (cell) {
                  TicTacToeCell.X -> {
                    Text(
                      text = "X",
                      color = NeonCyan,
                      fontSize = 36.sp,
                      fontWeight = FontWeight.ExtraBold
                    )
                  }
                  TicTacToeCell.O -> {
                    Text(
                      text = "O",
                      color = NeonPurple,
                      fontSize = 36.sp,
                      fontWeight = FontWeight.ExtraBold
                    )
                  }
                  TicTacToeCell.EMPTY -> {}
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = { viewModel.resetTicTacToe() },
        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = CyberBg),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("ttt_reset_button")
      ) {
        Icon(Icons.Default.Refresh, contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text("Restart Board", fontWeight = FontWeight.Bold)
      }
    }
  }
}

// ---------------------------------------------------------
// 2. ROCK PAPER SCISSORS
// ---------------------------------------------------------
@Composable
private fun RockPaperScissorsSection(
  state: RpsGameState,
  viewModel: AppViewModel
) {
  GlowingCard(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp)
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = "ROCK PAPER SCISSORS", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
      Text(text = "Battle the School Gaming Bot", color = NeonCyan, fontSize = 12.sp)

      Spacer(modifier = Modifier.height(14.dp))

      // Streaks and Wins
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(CyberBgElevated)
          .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "Win Streak", color = NeonAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          Text(text = "🔥 ${state.streak}", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "Player Wins", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          Text(text = "${state.playerWins}", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "Bot Wins", color = NeonPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          Text(text = "${state.botWins}", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Battle Arena
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Player choice
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "YOU", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(6.dp))
          Box(
            modifier = Modifier
              .size(76.dp)
              .clip(CircleShape)
              .background(NeonCyan.copy(alpha = 0.2f))
              .border(2.dp, NeonCyan, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = when (state.playerChoice) {
                RpsChoice.ROCK -> "✊"
                RpsChoice.PAPER -> "✋"
                RpsChoice.SCISSORS -> "✌️"
                null -> "❓"
              },
              fontSize = 34.sp
            )
          }
        }

        Text(text = "VS", color = NeonAmber, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)

        // Bot choice
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "BOT", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(6.dp))
          Box(
            modifier = Modifier
              .size(76.dp)
              .clip(CircleShape)
              .background(NeonPurple.copy(alpha = 0.2f))
              .border(2.dp, NeonPurple, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = when (state.opponentChoice) {
                RpsChoice.ROCK -> "✊"
                RpsChoice.PAPER -> "✋"
                RpsChoice.SCISSORS -> "✌️"
                null -> "🤖"
              },
              fontSize = 34.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Result banner
      val resultText = when (state.result) {
        RpsResult.WIN -> "🎉 VICTORY! +1 WIN"
        RpsResult.LOSE -> "💀 DEFEAT! TRY AGAIN"
        RpsResult.DRAW -> "⚖️ DRAW ROUND!"
        RpsResult.PENDING -> "Choose your hand to battle!"
      }

      Text(
        text = resultText,
        color = when (state.result) {
          RpsResult.WIN -> NeonEmerald
          RpsResult.LOSE -> NeonCrimson
          RpsResult.DRAW -> NeonAmber
          RpsResult.PENDING -> TextSecondary
        },
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        RpsChoiceButton(
          label = "Rock",
          emoji = "✊",
          color = NeonCyan,
          onClick = { viewModel.playRps(RpsChoice.ROCK) },
          modifier = Modifier.weight(1f)
        )
        RpsChoiceButton(
          label = "Paper",
          emoji = "✋",
          color = NeonPurple,
          onClick = { viewModel.playRps(RpsChoice.PAPER) },
          modifier = Modifier.weight(1f)
        )
        RpsChoiceButton(
          label = "Scissors",
          emoji = "✌️",
          color = NeonPink,
          onClick = { viewModel.playRps(RpsChoice.SCISSORS) },
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@Composable
private fun RpsChoiceButton(
  label: String,
  emoji: String,
  color: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Button(
    onClick = onClick,
    modifier = modifier.height(64.dp),
    shape = RoundedCornerShape(12.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = color.copy(alpha = 0.2f),
      contentColor = TextPrimary
    ),
    border = androidx.compose.foundation.BorderStroke(1.dp, color)
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(text = emoji, fontSize = 22.sp)
      Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
  }
}

// ---------------------------------------------------------
// 3. NUMBER GUESSING (CODE BREAKER)
// ---------------------------------------------------------
@Composable
private fun NumberGuessSection(
  state: NumberGuessState,
  viewModel: AppViewModel
) {
  var guessInput by remember { mutableStateOf("") }

  GlowingCard(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp)
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = "CODE BREAKER: GUESS NUMBER", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
      Text(text = "Crack the 2-digit passcode between 1 and 100", color = NeonCyan, fontSize = 12.sp)

      Spacer(modifier = Modifier.height(14.dp))

      // Status HUD
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(CyberBgElevated)
          .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "Attempts Used", color = TextSecondary, fontSize = 11.sp)
          Text(text = "${state.attempts.size} / ${state.maxAttempts}", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "Best Record", color = NeonCyan, fontSize = 11.sp)
          Text(text = if (state.bestScore < 999) "${state.bestScore} tries" else "--", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      if (!state.isGameOver) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = guessInput,
            onValueChange = {
              if (it.length <= 3 && it.all { ch -> ch.isDigit() }) guessInput = it
            },
            placeholder = { Text("Enter 1-100", color = TextMuted) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
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
              val n = guessInput.toIntOrNull()
              if (n != null && n in 1..100) {
                viewModel.submitGuess(n)
                guessInput = ""
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = CyberBg),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.height(52.dp)
          ) {
            Text("Decrypt", fontWeight = FontWeight.Bold)
          }
        }
      } else {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = if (state.hasWon) "🎉 ACCESS GRANTED! CODE DECRYPTED!" else "❌ LOCKDOWN! The code was ${state.targetNumber}",
            color = if (state.hasWon) NeonEmerald else NeonCrimson,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )

          Spacer(modifier = Modifier.height(10.dp))

          Button(
            onClick = { viewModel.restartNumberGuess() },
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = CyberBg),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Play New Round", fontWeight = FontWeight.Bold)
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Guess History
      Text(
        text = "ATTEMPT LOG",
        color = TextSecondary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.align(Alignment.Start)
      )

      Spacer(modifier = Modifier.height(6.dp))

      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        state.attempts.forEachIndexed { i, attempt ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(6.dp))
              .background(CyberBgElevated)
              .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "#${i + 1}: Guess ${attempt.first}", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(text = attempt.second, color = NeonCyan, fontSize = 12.sp)
          }
        }
      }
    }
  }
}
