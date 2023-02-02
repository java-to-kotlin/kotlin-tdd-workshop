package example.bowling

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.plus


typealias Game = PersistentList<Frame>

val newGame = persistentListOf<Frame>()


sealed interface Frame {
    val firstRoll: Int
    val pinfall: Int
}

data class IncompleteFrame(override val firstRoll: Int) : Frame {
    override val pinfall: Int get() = firstRoll
}

sealed interface CompleteFrame : Frame

// See https://en.wikipedia.org/wiki/Glossary_of_bowling
data class OpenFrame(override val firstRoll: Int, val secondRoll: Int) : CompleteFrame {
    override val pinfall: Int get() = firstRoll + secondRoll
}

data class Spare(override val firstRoll: Int) : CompleteFrame {
    override val pinfall: Int get() = 10
    
    val secondRoll: Int get() = pinfall - firstRoll
}

object Strike : CompleteFrame {
    override val firstRoll: Int get() = 10
    override val pinfall: Int get() = firstRoll
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
        is Spare -> bonus(forFrame = i, bonusRolls = 1)
        Strike -> bonus(forFrame = i,  bonusRolls = 2)
        else -> 0
    }
    
    return pinfall + bonus
}

fun GameScores.total() = sumOf { it.score }


private fun Frame.rolls() = when (this) {
    is IncompleteFrame -> listOf(firstRoll)
    is OpenFrame -> listOf(firstRoll, secondRoll)
    is Spare -> listOf(firstRoll, secondRoll)
    is Strike -> listOf(firstRoll)
}

private fun List<Frame>.bonus(forFrame: Int, bonusRolls: Int) =
    drop(forFrame+1).flatMap { it.rolls() }.take(bonusRolls).sum()


fun Game.isOver(): Boolean =
    size == 10 && (last() is OpenFrame)

