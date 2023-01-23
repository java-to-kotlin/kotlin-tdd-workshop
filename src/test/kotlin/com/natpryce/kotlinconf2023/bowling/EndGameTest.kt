package com.natpryce.kotlinconf2023.bowling

import io.kotest.core.spec.style.AnnotationSpec
import kotlin.test.assertTrue

class EndGameTest : AnnotationSpec() {
    @Test
    fun `end game after 10 complete frames each, no bonus rolls`() {
        val playerCount = 2
        
        val game = newGame(playerCount).repeated(10) { game ->
            game.repeated(playerCount) { turn ->
                turn
                    .also { assertTrue(!it.isOver()) }
                    .afterRoll(1)
                    .also { assertTrue(!it.isOver()) }
                    .afterRoll(2)
            }
        }
        
        assert(game.isOver())
    }
    
    @Test
    fun `end game when last frame is a spare, one bonus roll`() {
        val playerCount = 2
        
        val lastTurn = newGame(playerCount).repeated(9) { game ->
            game.repeated(playerCount) { turn ->
                turn
                    .also { assertTrue(!it.isOver()) }
                    .afterRoll(1)
                    .also { assertTrue(!it.isOver()) }
                    .afterRoll(2)
            }
        }
        
        val game = lastTurn.repeated(playerCount) { turn ->
            turn
                .also { assertTrue(!it.isOver()) }
                .afterRoll(5)
                .also { assertTrue(!it.isOver()) }
                .afterRoll(5)
                .also { assertTrue(!it.isOver()) }
                .afterRoll(5)
        }
        
        assert(game.isOver())
    }
    
    @Test
    fun `end game when last frame is a strike, two bonus rolls`() {
        val playerCount = 2
        
        val lastTurn = newGame(playerCount).repeated(9) { game ->
            game.repeated(playerCount) { turn ->
                turn
                    .also { assertTrue(!it.isOver()) }
                    .afterRoll(1)
                    .also { assertTrue(!it.isOver()) }
                    .afterRoll(2)
            }
        }
        
        val game = lastTurn.repeated(playerCount) { turn ->
            turn
                .also { assertTrue(!it.isOver()) }
                .afterRoll(10)
                .also { assertTrue(!it.isOver()) }
                .afterRoll(1)
                .also { assertTrue(!it.isOver()) }
                .afterRoll(2)
        }
        
        assert(game.isOver())
    }
}
