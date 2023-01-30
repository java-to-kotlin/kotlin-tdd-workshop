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
        val scores = newGame.score()
        
        assertTrue(scores.isEmpty())
        assertTrue(scores.total() == 0)
    }
    
    @Test
    suspend fun `one roll that is not a strike`() {
        checkAll(Arb.int(0..9)) { n ->
            val scores = newGame.roll(n).score()
            
            assertTrue(
                scores == listOf(
                    IncompleteFrame(firstRoll = n).scoredAs(n)
                )
            )
            assertTrue(scores.total() == n)
        }
    }
    
    @Test
    fun `two rolls, open frame`() {
        val scores = newGame.roll(4).roll(3).score()
        
        assertTrue(
            scores == listOf(
                OpenFrame(firstRoll = 4, secondRoll = 3).scoredAs(7)
            )
        )
        assertTrue(scores.total() == 7)
    }
    
    @Test
    fun `two rolls, spare`() {
        val scores = newGame.roll(6).roll(4).score()
        
        assertTrue(
            scores == listOf(
                Spare(firstRoll = 6).scoredAs(10)
            )
        )
        assertTrue(scores.total() == 10)
    }
    
    @Test
    fun `a strike`() {
        val scores = newGame.roll(10).score()
    
        assertTrue(
            scores == listOf(
                Strike.scoredAs(10)
            )
        )
        assertTrue(scores.total() == 10)
    }
    
    @Test
    fun `roll after open frame`() {
        val scores = newGame.roll(3).roll(5).roll(4).score()
    
        assertTrue(
            scores == listOf(
                OpenFrame(firstRoll = 3, secondRoll = 5).scoredAs(8),
                IncompleteFrame(firstRoll = 4).scoredAs(4)
            )
        )
        assertTrue(scores.total() == 12)
    }
    
    @Test
    fun `roll after spare`() {
        val scores = newGame.roll(3).roll(7).roll(4).score()
    
        assertTrue(
            scores == listOf(
                Spare(3).scoredAs(14),
                IncompleteFrame(4).scoredAs(4)
            )
        )
        assertTrue(scores.total() == 18)
    }
}

typealias Game = PersistentList<Frame>

val newGame = persistentListOf<Frame>()


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

data class FrameScore(
    val frame: Frame,
    val score: Int
)

fun Frame.scoredAs(score: Int) =
    FrameScore(this, score)

typealias GameScores = List<FrameScore>

fun Game.score(): GameScores =
    mapIndexed { i, frame -> frame.scoredAs(scoreForFrame(i)) }


private fun Game.scoreForFrame(i: Int): Int {
    val frame = this[i]
    val pinfall = frame.pinfall
    val bonus = when (frame) {
        is Spare -> this.getOrNull(i + 1)?.pinfall ?: 0
        else -> 0
    }
    
    return pinfall + bonus
}

private fun GameScores.total() = sumOf { it.score }
