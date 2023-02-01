package example.bowling

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import kotlin.test.assertTrue

class EndOfGameTest : AnnotationSpec() {
    @Test
    suspend fun `game is over after ten open frames`() {
        checkAll(Arb.list(Arb.openFrame(), 10..10)) { rolls ->
            val game = rolls.fold(newGame) { game, (first, second) -> game.roll(first).roll(second) }
            
            assertTrue(game.isOver())
        }
    }
    
    @Test
    suspend fun `game is not over if last roll is a spare`() {
        checkAll(Arb.list(Arb.openFrame(), 9..9), Arb.spare()) { frames, lastFrame ->
            val game = frames
                .fold(newGame) { game, (first, second) ->
                    game.roll(first).roll(second)
                }
                .roll(lastFrame.first).roll(lastFrame.second)
            
            assertTrue(!game.isOver())
        }
    }
}
