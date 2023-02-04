package example.bowling

import io.kotest.core.spec.style.AnnotationSpec
import kotlin.test.assertTrue

class PlayerTurnsTest : AnnotationSpec() {
    @Test
    fun `first player starts`() {
        val game: MultiplayerGame = newGame(3)
        assertTrue(game.playerToRoll() == 0)
    }
}
