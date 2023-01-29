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
            assertTrue(game.frames() == persistentListOf(IncompleteFrame(firstRoll = n)))
        }
    }
    
    @Test
    fun `two rolls, open frame`() {
        val game = newGame.roll(4).roll(3)
        
        assertTrue(game.totalScore() == 7)
        assertTrue(game.frames() == persistentListOf(OpenFrame(firstRoll = 4, secondRoll = 3)))
    }
    
    @Test
    fun `two rolls, spare`() {
        val game = newGame.roll(6).roll(4)
        
        assertTrue(game.totalScore() == 10)
        assertTrue(game.frames() == persistentListOf(Spare(firstRoll = 6)))
    }
    
    @Test
    fun `a strike`() {
        val game = newGame.roll(10)
        
        assertTrue(game.totalScore() == 10)
        assertTrue(game.frames() == persistentListOf(Strike))
        
    }
    
    @Test
    fun `roll after open frame`() {
        val game = newGame.roll(3).roll(5).roll(4)
        
        assertTrue(game.totalScore() == 12)
        assertTrue(
            game.frames() == persistentListOf(
                OpenFrame(firstRoll = 3, secondRoll = 5),
                IncompleteFrame(firstRoll = 4)
            )
        )
    }
}

typealias Game = PersistentList<Frame>

sealed interface Frame {
    val pinfall: Int
}

data class IncompleteFrame(val firstRoll: Int) : Frame {
    override val pinfall: Int get() = firstRoll
}

sealed interface CompleteFrame : Frame

// See https://en.wikipedia.org/wiki/Glossary_of_bowling
data class OpenFrame(val firstRoll: Int, val secondRoll: Int) : CompleteFrame {
    override val pinfall: Int get() = firstRoll + secondRoll
}

data class Spare(val firstRoll: Int) : CompleteFrame {
    override val pinfall: Int get() = 10
}

object Strike : CompleteFrame {
    override val pinfall: Int get() = 10
}


val newGame = persistentListOf<Frame>()

fun Game.roll(rollPinfall: Int): Game =
    when (val prev = this.lastOrNull()) {
        null, is CompleteFrame -> {
            this + when (rollPinfall) {
                10 -> Strike
                else -> IncompleteFrame(rollPinfall)
            }
        }
        
        is IncompleteFrame -> {
            val firstRoll = prev.firstRoll
            val secondRoll = rollPinfall
            this.set(
                this.lastIndex,
                when (firstRoll + secondRoll) {
                    10 -> Spare(firstRoll)
                    else -> OpenFrame(firstRoll, secondRoll)
                }
            )
        }
    }

fun Game.frames() = this

fun Game.totalScore(): Int = firstOrNull()?.pinfall ?: 0
