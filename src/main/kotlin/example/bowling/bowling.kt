package example.bowling

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.plus


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

fun GameScores.total() = sumOf { it.score }
