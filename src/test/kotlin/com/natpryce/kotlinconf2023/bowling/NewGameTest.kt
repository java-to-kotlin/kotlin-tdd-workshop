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
    
    @Test
    fun `second player bowls a strike, back to first player`() {
        val game = newGame().afterRoll(10).afterRoll(10)
        
        assertEquals(0, game.nextPlayerToBowl())
    }
}

data class BowlingGame(val rollCount: Int = 0)

fun newGame() = BowlingGame()

fun BowlingGame.afterRoll(score: Int): BowlingGame =
    copy(rollCount = rollCount + 1)

fun BowlingGame.nextPlayerToBowl(): Int =
    rollCount % 2
