package example.bowling

import io.kotest.core.spec.style.AnnotationSpec
import kotlin.test.assertTrue


// See https://codingdojo.org/kata/Bowling/#suggested-test-cases
class FullGameScoringTest : AnnotationSpec() {
    @Test
    fun `all strikes scores 300`() {
        val game = (1..10).fold(newGame) { game, _ -> game.roll(10.pins) }.roll(10.pins).roll(10.pins)
        assertTrue(game.isOver())
        assertTrue(game.score().total() == 300)
    }
    
    @Test
    fun `all spares score includes bonus roll`() {
        val game = (1..10).fold(newGame) { game, _ -> game.roll(5.pins).roll(5.pins) }.roll(5.pins)
        
        assertTrue(game.isOver())
        assertTrue(game.score().total() == 150)
    }
    
    @Test
    fun `all open frames`() {
        val game = (1..10).fold(newGame) { game, _ -> game.roll(9.pins).roll(0.pins) }
        
        assertTrue(game.isOver())
        assertTrue(game.score().total() == 90)
    }
}