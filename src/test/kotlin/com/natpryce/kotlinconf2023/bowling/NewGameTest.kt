package com.natpryce.kotlinconf2023.bowling

import kotlin.test.Test
import kotlin.test.assertEquals

class NewGameTest {
    @Test
    fun `new game, player 1 to start`() {
        val game = newGame()
        assertEquals(0, game.nextPlayerToBowl())
    }
    
}

private fun BowlingGame.nextPlayerToBowl(): Int {
    TODO("Not yet implemented")
}

object BowlingGame

fun newGame(): BowlingGame {
    TODO("Not yet implemented")
}
