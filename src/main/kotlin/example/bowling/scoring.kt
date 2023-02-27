package example.bowling

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

typealias PlayerScores = List<FrameScore>

val PlayerScores.total: Int
    get() = lastOrNull()?.runningTotal ?: 0

data class FrameScore(
    val roll1: Int?,
    val roll2: Int?,
    val bonus: Int = 0,
    val runningTotal: Int = 0
)

val FrameScore.score get() =
    (roll1 ?: 0) + (roll2 ?: 0) + bonus

fun Frame.score(): PlayerScores =
    scoreFrames().reversed().scoreTotals()

private fun List<FrameScore>.scoreTotals(): List<FrameScore> =
    fold(emptyList()) { acc, f ->
        acc + f.copy(runningTotal = f.score + (acc.lastOrNull()?.runningTotal ?: 0))
    }

fun Frame.scoreFrames(acc: PersistentList<FrameScore> = persistentListOf()): List<FrameScore> {
    return when (this) {
        StartOfGame -> acc
        is PartialFrame -> acc + FrameScore(roll1, null, 0)
        is OpenFrame -> acc + FrameScore(roll1, roll2, 0)
        is Spare -> acc + FrameScore(roll1, 10 - roll1, 0)
        Strike -> acc + FrameScore(10, null, 0)
    }
}