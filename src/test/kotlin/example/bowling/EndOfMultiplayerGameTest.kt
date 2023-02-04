package example.bowling

import io.kotest.core.spec.style.AnnotationSpec
import kotlin.test.assertTrue


class EndOfMultiplayerGameTest : AnnotationSpec() {
    @Test
    fun `game is over when all players' last frame is an open frame`() {
        val game = (1..10).fold(newGame(3)) { game, _ ->
            (1..3).fold(game) { turn, _ ->
                turn
                    .also { assertTrue(!it.isOver()) }
                    .roll(1)
                    .also { assertTrue(!it.isOver()) }
                    .roll(2)
            }
        }
        
        assertTrue(game.isOver())
    }
    
    @Test
    fun `game is over when all players have played bonus rolls`() {
        val lastRound = (1..9).fold(newGame(3)) { game, _ ->
            (1..3).fold(game) { turn, _ ->
                turn
                    .roll(1)
                    .also { assertTrue(!it.isOver()) }
                    .roll(2)
            }
        }
        
        assertTrue(!lastRound.isOver())
        
        val endGame = lastRound
            // Player 0 rolls a final open frame
            .roll(6)
            .roll(3)
            .also {
                assertTrue(!it.isOver())
            }
            // Player 1 rolls a strike and two bonus rolls
            .roll(10)
            .also { assertTrue(!it.isOver()) }
            .roll(1)
            .also { assertTrue(!it.isOver()) }
            .roll(2)
            .also {
                assertTrue(!it.isOver())
            }
            // Player 2 rolls a spare and one bonus roll
            .roll(6)
            .also { assertTrue(!it.isOver()) }
            .roll(4)
            .also { assertTrue(!it.isOver()) }
            .roll(7)
        
        assertTrue(endGame.isOver())
    }
}