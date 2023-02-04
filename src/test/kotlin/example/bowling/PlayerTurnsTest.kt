package example.bowling

import io.kotest.core.spec.style.AnnotationSpec
import kotlin.test.assertTrue

class PlayerTurnsTest : AnnotationSpec() {
    @Test
    fun `first player starts`() {
        val game = newGame(3)
        assertTrue(game.playerToRoll() == 0)
    }
    
    @Test
    fun `after first player rolls a strike, second player rolls`() {
        val game = newGame(3).roll(10)
        assertTrue(game.playerToRoll() == 1)
    }
    
    @Test
    fun `first player plays both rolls of an open frame before the second player rolls`() {
        newGame(3)
            .roll(1)
            .also { assertTrue(it.playerToRoll() == 0) }
            .roll(2)
            .also { assertTrue(it.playerToRoll() == 1) }
    }
    
    @Test
    fun `all players have played a strike, back to first player`() {
        val game = (1..3).fold(newGame(3)) { game, _ -> game.roll(10) }
        
        assertTrue(game.playerToRoll() == 0)
    }
    
    @Test
    fun `all players have played an open frame, back to first player`() {
        val game = (1..3).fold(newGame(3)) { game, _ -> game.roll(1).roll(2) }
        
        assertTrue(game.playerToRoll() == 0)
    }
}
