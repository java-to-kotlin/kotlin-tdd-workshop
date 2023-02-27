package example.bowling

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

typealias PlayerScores = List<FrameScore>

val PlayerScores.total: Int
    get() = lastOrNull()?.runningTotal ?: 0

data class FrameScore(
    val roll1: Int?,
    val roll2: Int?,
    val runningTotal: Int = 0
)


fun Frame.score(): PlayerScores =
    scoreFrames().calculateTotals()

private fun List<FrameBonus>.calculateTotals(): List<FrameScore> =
    fold(emptyList()) { acc, f ->
        acc + FrameScore(
            roll1 = f.roll1,
            roll2 = f.roll2,
            runningTotal = f.score + (acc.lastOrNull()?.runningTotal ?: 0)
        )
    }

private data class FrameBonus(
    val roll1: Int?,
    val roll2: Int?,
    val bonus: Int
)

private val FrameBonus.score
    get() =
        (roll1 ?: 0) + (roll2 ?: 0) + bonus


private fun Frame.scoreFrames(
    acc: PersistentList<FrameBonus> = persistentListOf(),
    bonusRolls: Pair<Int, Int> = Pair(0, 0)
): List<FrameBonus> {
    return when (this) {
        StartOfGame -> acc
        is PartialFrame ->
            prev.scoreFrames(acc, bonusRolls.push(roll1)) +
                FrameBonus(roll1, null, 0)
        is OpenFrame ->
            prev.scoreFrames(acc, bonusRolls.push(roll1, roll2)) +
                FrameBonus(roll1, roll2, 0)
        is Spare ->
            prev.scoreFrames(acc, bonusRolls.push(roll1, roll2)) +
                FrameBonus(roll1, 10 - roll1, bonusRolls.first)
        is Strike ->
            prev.scoreFrames(acc, bonusRolls.push(10)) +
                FrameBonus(10, null, bonusRolls.first + bonusRolls.second)
    }
}


private fun Pair<Int, Int>.push(i: Int) =
    Pair(first = i, second = first)

@Suppress("UnusedReceiverParameter")
private fun Pair<Int,Int>.push(roll1: Int, roll2: Int) =
    Pair(roll1, roll2)
