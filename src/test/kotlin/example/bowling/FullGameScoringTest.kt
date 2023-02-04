package example.bowling

import io.kotest.core.spec.style.AnnotationSpec
import kotlin.test.assertTrue


// See https://codingdojo.org/kata/Bowling/#suggested-test-cases
class FullGameScoringTest : AnnotationSpec() {
    @Test
    fun `all strikes scores 300`() {
        val game = (1..10).fold(newGame) { game, _ -> game.roll(10) }.roll(10).roll(10)
        assertTrue(game.isOver())
        assertTrue(game.score().total() == 300)
    }
    
    @Test
    fun `all spares score includes bonus roll`() {
        val game = (1..10).fold(newGame) { game, _ -> game.roll(5).roll(5) }.roll(5)
        
        assertTrue(game.isOver())
        assertTrue(game.score().total() == 150)
    }
    
    @Test
    fun `all open frames`() {
        val game = (1..10).fold(newGame) { game, _ -> game.roll(9).roll(0) }
        
        assertTrue(game.isOver())
        assertTrue(game.score().total() == 90)
    }
}