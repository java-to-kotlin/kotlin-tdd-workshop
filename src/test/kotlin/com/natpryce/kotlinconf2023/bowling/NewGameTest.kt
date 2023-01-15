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

data class BowlingGame(val hasRolled: Boolean = false)

fun newGame() = BowlingGame()

fun BowlingGame.afterRoll(score: Int): BowlingGame =
    copy(hasRolled = true)

fun BowlingGame.nextPlayerToBowl(): Int =
    if (hasRolled) 1 else 0
