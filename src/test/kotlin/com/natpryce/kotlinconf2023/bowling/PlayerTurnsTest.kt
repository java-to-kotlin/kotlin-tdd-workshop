package com.natpryce.kotlinconf2023.bowling

import kotlin.test.Test

class PlayerTurnsTest {
    @Test
    fun `new game, first player to start`() {
        val game = newGame(playerCount = 2)
        
        assert(game.nextPlayerToBowl() == 0)
    }
    
    @Test
    fun `first player bowls a strike, second player up next`() {
        val game = newGame(playerCount = 2).afterRoll(10)
        
        assert(game.nextPlayerToBowl() == 1)
    }
    
    @Test
    fun `second player bowls a strike, back to first player`() {
        val game = newGame(playerCount = 2).afterRoll(10).afterRoll(10)
        
        assert(game.nextPlayerToBowl() == 0)
    }
    
    @Test
    fun `game with three players`() {
        newGame(playerCount = 3)
            .then { assert(it.nextPlayerToBowl() == 0) }
            .afterRoll(10)
            .then { assert(it.nextPlayerToBowl() == 1) }
            .afterRoll(10)
            .then { assert(it.nextPlayerToBowl() == 2) }
            .afterRoll(10)
            .then { assert(it.nextPlayerToBowl() == 0) }
    }
    
    @Test
    fun `player scores less than 10 in two rolls`() {
        newGame(playerCount = 2)
            .afterRoll(4)
            .then { assert(it.nextPlayerToBowl() == 0) }
            .afterRoll(4)
            .then { assert(it.nextPlayerToBowl() == 1) }
    }
}

inline fun <T> T.then(statement: (T)->Unit): T = also(statement)

data class BowlingGame(
    val playerCount: Int,
    val rollCount: Int = 0
)

fun newGame(playerCount: Int) =
    BowlingGame(playerCount = playerCount)

fun BowlingGame.afterRoll(score: Int): BowlingGame =
    copy(rollCount = rollCount + 1)

fun BowlingGame.nextPlayerToBowl(): Int =
    rollCount % playerCount
