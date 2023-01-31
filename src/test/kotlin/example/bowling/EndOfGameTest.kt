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
            val game = rolls.fold(newGame) { game, (first, second) -> game.roll(first).roll(second )}
            
            assertTrue(game.isOver())
        }
        
    }
}

private fun Game.isOver(): Boolean = false

