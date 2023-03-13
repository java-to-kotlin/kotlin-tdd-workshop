package dev.javaToKotlin

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GameTests {
    @Test
    fun `test`() {
        var game : Game = newGame(players = listOf("Fred", "Barney"), numberOfFrames = 2)
        assertTrue(game is InProgressGame)
        assertEquals("Fred", game.currentLine.player)

        game = game.roll(PinCount(10))
        assertTrue(game is InProgressGame)
        assertEquals("Barney", game.currentLine.player)
    
        game = game.roll(PinCount(10))
        assertTrue(game is InProgressGame)
    
        game = game.roll(PinCount(1))
        assertTrue(game is InProgressGame)
    
        game = game.roll(PinCount(2))
        assertTrue(game is InProgressGame)
    
        game = game.roll(PinCount(3))
        assertTrue(game is InProgressGame)
    
        game = game.roll(PinCount(4))
        assertTrue(game !is InProgressGame)
        
    }
    
}
