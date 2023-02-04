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
}
