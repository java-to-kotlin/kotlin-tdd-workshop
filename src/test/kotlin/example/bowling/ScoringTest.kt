package example.bowling

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class ScoringTest {
    @Test
    fun `new game - no frames, total score 0`() {
        val game = newGame
        assertTrue(game.totalScore() == 0)
        assertTrue(game.isEmpty())
    }
}

val newGame = persistentListOf<Any>()

private fun PersistentList<Any>.totalScore(): Int = 0
