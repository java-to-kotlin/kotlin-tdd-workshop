package dev.javaToKotlin

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class GameTests {
    @Test
    fun `test`() {
        var game : Game = newGame(players = listOf("Fred", "Barney"), numberOfFrames = 2)
        assertTrue(game is InProgressGame)

        game = game.roll(PinCount(3))
        assertTrue(game is InProgressGame)
    }
    
}
