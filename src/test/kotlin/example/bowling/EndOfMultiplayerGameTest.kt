package example.bowling

import io.kotest.core.spec.style.AnnotationSpec
import kotlin.test.assertTrue


class EndOfMultiplayerGameTest : AnnotationSpec() {
    @Test
    fun `game is over when all players' last frame is an open frame`() {
        val game = (1..10).fold(newGame(3)) { game, _ ->
            (1..3).fold(game) { turn, _ ->
                turn.roll(1).roll(2)
            }
        }
        
        assertTrue(game.isOver())
    }
    
}