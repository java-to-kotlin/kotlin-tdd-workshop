package example.bowling

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentList


typealias MultiplayerGame = PersistentList<Game>

fun newGame(playerCount: Int): MultiplayerGame =
    (1..playerCount).map { newGame }.toPersistentList()

fun MultiplayerGame.nextPlayerToBowl(): Int =
    (0..lastIndex)
        .firstOrNull { p -> playerHasUnfinishedFrame(p) || framesPlayedBy(p) < framesPlayedBy(0) }
        ?: 0

private fun MultiplayerGame.playerHasUnfinishedFrame(p: Int): Boolean {
    val lastFrame = get(p).lastOrNull()
    
    val unfinishedFrame = when (framesPlayedBy(p)) {
        10 -> lastFrame !is CompleteFinalFrame
        else -> lastFrame !is CompleteFrame
    }
    return unfinishedFrame
}

fun MultiplayerGame.scores(): List<GameScores> = map { it.score() }

private fun MultiplayerGame.framesPlayedBy(p: Int) = get(p).size

@JvmName("multiplayerGameIsOver")
fun MultiplayerGame.isOver() =
    all { it.isOver() }

@JvmName("multiplayerGameRoll")
fun MultiplayerGame.roll(rollPinfall: Int): MultiplayerGame {
    val p = nextPlayerToBowl()
    
    return set(p, get(p).roll(rollPinfall))
}

typealias Game = PersistentList<Frame>

val framesPerGame = 10
val pinCount = 10

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
data class OpenFrame(override val firstRoll: Int, val secondRoll: Int) : CompleteFinalFrame {
    override val pinfall: Int get() = firstRoll + secondRoll
}

data class Spare(override val firstRoll: Int) : CompleteFrame {
    override val pinfall: Int get() = pinCount
    
    val secondRoll: Int get() = pinfall - firstRoll
}

object Strike : CompleteFrame {
    override val firstRoll: Int get() = pinCount
    override val pinfall: Int get() = firstRoll
    
    override fun toString() = "Strike"
}

sealed interface CompleteFinalFrame : CompleteFrame

data class BonusRollForSpare(
    val spare: Spare,
    val bonusRoll: Int
) : CompleteFinalFrame {
    override val firstRoll: Int get() = spare.firstRoll
    val secondRoll get() = spare.secondRoll
    override val pinfall get() = spare.pinfall + bonusRoll
}

data class FirstBonusRollForStrike(
    val strike: Strike,
    val bonusRoll1: Int
) : Frame {
    override val firstRoll: Int get() = strike.firstRoll
    override val pinfall get() = strike.pinfall + bonusRoll1
}

data class BonusRollsForStrike(
    val strike: Strike,
    val bonusRoll1: Int,
    val bonusRoll2: Int
) : CompleteFinalFrame {
    override val firstRoll: Int get() = strike.firstRoll
    override val pinfall get() = strike.pinfall + bonusRoll1 + bonusRoll2
}

fun Game.roll(rollPinfall: Int): Game =
    when (val prev = this.lastOrNull()) {
        null ->
            newFrame(rollPinfall)
        
        is CompleteFrame -> when (size) {
            10 -> completeLastFrame(prev, rollPinfall)
            else -> newFrame(rollPinfall)
        }
        
        is IncompleteFrame ->
            completeFrame(prev, rollPinfall)
        
        is FirstBonusRollForStrike ->
            set(lastIndex, BonusRollsForStrike(prev.strike, prev.bonusRoll1, rollPinfall))
    }

private fun Game.completeFrame(
    prev: IncompleteFrame,
    secondRoll: Int
): PersistentList<Frame> {
    val firstRoll = prev.firstRoll
    return set(
        lastIndex,
        when (firstRoll + secondRoll) {
            pinCount -> Spare(firstRoll)
            else -> OpenFrame(firstRoll, secondRoll)
        }
    )
}

private fun Game.completeLastFrame(
    prev: Frame,
    rollPinfall: Int
) = when (prev) {
    is Spare -> set(lastIndex, BonusRollForSpare(prev, rollPinfall))
    is Strike -> set(lastIndex, FirstBonusRollForStrike(prev, rollPinfall))
    else -> this // ignore rolls after the end of the game
}

private fun Game.newFrame(rollPinfall: Int) = this + when (rollPinfall) {
    pinCount -> Strike
    else -> IncompleteFrame(rollPinfall)
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
        Strike -> bonus(forFrame = i, bonusRolls = 2)
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
    is BonusRollForSpare -> listOf(firstRoll, secondRoll, bonusRoll)
    is FirstBonusRollForStrike -> listOf(strike.firstRoll, bonusRoll1)
    is BonusRollsForStrike -> listOf(strike.firstRoll, bonusRoll1, bonusRoll2)
}

private fun List<Frame>.bonus(forFrame: Int, bonusRolls: Int) =
    drop(forFrame + 1).flatMap { it.rolls() }.take(bonusRolls).sum()


fun Game.isOver(): Boolean =
    size == framesPerGame && (last() is CompleteFinalFrame)
