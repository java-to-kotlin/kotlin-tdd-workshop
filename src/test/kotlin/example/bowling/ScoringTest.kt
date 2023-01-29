package example.bowling

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.plus
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class ScoringTest {
    @Test
    fun `new game - no frames, total score 0`() {
        assertTrue(newGame.totalScore() == 0)
        assertTrue(newGame.frames().isEmpty())
    }
    
    @Test
    fun `one roll that is not a strike`() {
        val game = newGame.roll(5)
        assertTrue(game.totalScore() == 5)
        assertTrue(game.frames() == persistentListOf(Frame(pinsDown = 5)))
    }
}

data class Frame(val pinsDown: Int)

val newGame = persistentListOf<Frame>()

private fun PersistentList<Frame>.roll(pinsDown: Int): PersistentList<Frame> =
    this + Frame(pinsDown)

private fun PersistentList<Frame>.frames() = this

private fun PersistentList<Frame>.totalScore(): Int = firstOrNull()?.pinsDown ?: 0
