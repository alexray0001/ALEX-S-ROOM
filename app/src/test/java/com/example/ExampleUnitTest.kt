package com.example

import com.example.model.RpsChoice
import com.example.model.RpsResult
import com.example.model.TicTacToeCell
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun rpsRules_rockBeatsScissors() {
    val player = RpsChoice.ROCK
    val bot = RpsChoice.SCISSORS
    val result = when {
      player == bot -> RpsResult.DRAW
      player == RpsChoice.ROCK && bot == RpsChoice.SCISSORS -> RpsResult.WIN
      else -> RpsResult.LOSE
    }
    assertEquals(RpsResult.WIN, result)
  }

  @Test
  fun ticTacToe_cellEnum() {
    val board = List(9) { TicTacToeCell.EMPTY }
    assertEquals(9, board.size)
    assertEquals(TicTacToeCell.EMPTY, board[0])
  }
}

