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
    suspend fun `game is over after one bonus roll when last frame is a spare`() {
        checkAll(
            Arb.list(Arb.openFrame(), 9..9),
            Arb.spare(),
            Arb.roll()
        ) { frames, lastFrame, bonusRoll ->
            val game = frames
                .fold(newGame) { game, (first, second) ->
                    game.roll(first).roll(second)
                }
                .roll(lastFrame.first).roll(lastFrame.second)
                .also { assertTrue(!it.isOver()) }
                .roll(bonusRoll)
            
            assertTrue(game.isOver())
        }
    }
    
    @Test
    suspend fun `game is over after two bonus rolls when last frame is a strike`() {
        checkAll(
            Arb.list(Arb.openFrame(), 9..9),
            Arb.roll(),
            Arb.roll()
        ) { frames, bonusRoll1, bonusRoll2 ->
            val game = frames
                .fold(newGame) { game, (first, second) ->
                    game.roll(first).roll(second)
                }
                .roll(10)
                .also { assertTrue(!it.isOver()) }
                .roll(bonusRoll1)
                .also { assertTrue(!it.isOver()) }
                .roll(bonusRoll2)
            
            assertTrue(game.isOver())
        }
    }
}
