package com.natpryce.kotlinconf2023.bowling

import kotlin.test.Test
import kotlin.test.assertEquals

class NewGameTest {
    @Test
    fun `new game, player 1 to start`() {
        val game = newGame()
        assertEquals(0, game.nextPlayerToBowl())
    }
    
    @Test
    fun `first player bowls a strike`() {
        val game = newGame().afterRoll(10)
        
        assertEquals(1, game.nextPlayerToBowl())
    }
}

private fun BowlingGame.afterRoll(score: Int): BowlingGame {
    TODO("Not yet implemented")
}

private fun BowlingGame.nextPlayerToBowl(): Int = 0

object BowlingGame

fun newGame(): BowlingGame {
    return BowlingGame
}
