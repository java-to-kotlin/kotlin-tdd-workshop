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

typealias Game = PersistentList<Frame>

val newGame = persistentListOf<Frame>()

private fun Game.roll(pinsDown: Int): Game =
    this + Frame(pinsDown)

private fun Game.frames() = this

private fun Game.totalScore(): Int = firstOrNull()?.pinsDown ?: 0
