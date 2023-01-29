package example.bowling

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.plus
import kotlin.test.assertTrue

class ScoringTest : AnnotationSpec() {
    @Test
    fun `new game - no frames, total score 0`() {
        assertTrue(newGame.totalScore() == 0)
        assertTrue(newGame.frames().isEmpty())
    }
    
    @Test
    suspend fun `one roll that is not a strike`() {
        checkAll(Arb.int(0..9)) { n ->
            val game = newGame.roll(n)
            assertTrue(game.totalScore() == n)
            assertTrue(game.frames() == persistentListOf(IncompleteFrame(pinsDown = n)))
        }
    }
    
    @Test
    fun `two rolls not a spare`() {
        val game = newGame.roll(4).roll(3)
        
        assertTrue(game.totalScore() == 7)
        assertTrue(game.frames() == persistentListOf(CompleteFrame(firstRoll = 4, secondRoll = 3)))
    }
    
    @Test
    fun `a strike`() {
        val game = newGame.roll(10)
        
        assertTrue(game.totalScore() == 10)
        assertTrue(game.frames() == persistentListOf(Strike))
        
    }
    
    @Test
    fun `a spare`() {
        val game = newGame.roll(6).roll(4)
        
        assertTrue(game.totalScore() == 10)
        assertTrue(game.frames() == persistentListOf(Spare(firstRoll = 6)))
    }
}

sealed interface Frame {
    val pinsDown: Int
}

data class IncompleteFrame(override val pinsDown: Int) : Frame

data class CompleteFrame(val firstRoll: Int, val secondRoll: Int) : Frame {
    override val pinsDown: Int get() = firstRoll + secondRoll
}

data class Spare(val firstRoll: Int) : Frame {
    override val pinsDown: Int get() = 10
}

object Strike : Frame {
    override val pinsDown: Int get() = 10
}

typealias Game = PersistentList<Frame>

val newGame = persistentListOf<Frame>()

private fun Game.roll(pinsDown: Int): Game {
    val lastFrame = this.lastOrNull()
    return when {
        lastFrame is IncompleteFrame -> {
            val firstRoll = lastFrame.pinsDown
            val secondRoll = pinsDown
            this.set(
                this.lastIndex,
                when (firstRoll + secondRoll) {
                    10 -> Spare(firstRoll)
                    else -> CompleteFrame(firstRoll, secondRoll)
                }
            )
        }
        
        pinsDown == 10 ->
            this + Strike
        
        else ->
            this + IncompleteFrame(pinsDown)
    }
}

private fun Game.frames() = this

private fun Game.totalScore(): Int = firstOrNull()?.pinsDown ?: 0