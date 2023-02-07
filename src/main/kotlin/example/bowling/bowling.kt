package example.bowling



typealias MultiplayerGame = List<Game>

fun newGame(playerCount: Int): MultiplayerGame =
    (1..playerCount).map { newGame }

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
fun MultiplayerGame.roll(rollPinfall: PinCount): MultiplayerGame {
    val p = nextPlayerToBowl()
    
    return set(p, get(p).roll(rollPinfall))
}

typealias Game = List<Frame>

val framesPerGame = 10

val newGame = listOf<Frame>()


sealed interface Frame {
    val firstRoll: PinCount
    val secondRoll: PinCount?
}

data class IncompleteFrame(override val firstRoll: PinCount) : Frame {
    override val secondRoll: PinCount? get() = null
}

sealed interface CompleteFrame : Frame

// See https://en.wikipedia.org/wiki/Glossary_of_bowling
data class OpenFrame(
    override val firstRoll: PinCount,
    override val secondRoll: PinCount
) : CompleteFinalFrame

data class Spare(override val firstRoll: PinCount) : CompleteFrame {
    override val secondRoll: PinCount get() = PinCount.MAX - firstRoll
}

object Strike : CompleteFrame {
    override val firstRoll: PinCount get() = PinCount.MAX
    override val secondRoll: PinCount? get() = null
    
    override fun toString() = "Strike"
}

sealed interface CompleteFinalFrame : CompleteFrame

data class BonusRollForSpare(
    val spare: Spare,
    val bonusRoll: PinCount
) : Frame by spare, CompleteFinalFrame {
    override val secondRoll: PinCount get() = spare.secondRoll
}

data class FirstBonusRollForStrike(
    val strike: Strike,
    val bonusRoll1: PinCount
) : Frame by Strike

data class BonusRollsForStrike(
    val strike: Strike,
    val bonusRoll1: PinCount,
    val bonusRoll2: PinCount
) : Frame by Strike, CompleteFinalFrame

fun Game.roll(rollPinfall: PinCount): Game =
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
    secondRoll: PinCount
): List<Frame> {
    val firstRoll = prev.firstRoll
    return set(
        lastIndex,
        when (firstRoll + secondRoll) {
            PinCount.MAX -> Spare(firstRoll)
            else -> OpenFrame(firstRoll, secondRoll)
        }
    )
}

private fun Game.completeLastFrame(
    prev: Frame,
    rollPinfall: PinCount
) = when (prev) {
    is Spare -> set(lastIndex, BonusRollForSpare(prev, rollPinfall))
    is Strike -> set(lastIndex, FirstBonusRollForStrike(prev, rollPinfall))
    else -> this // ignore rolls after the end of the game
}

private fun Game.newFrame(rollPinfall: PinCount) = this + when (rollPinfall) {
    PinCount.MAX -> Strike
    else -> IncompleteFrame(rollPinfall)
}

data class FrameScore(
    val firstRoll: PinCount,
    val secondRoll: PinCount? = null,
    val bonus: Int = 0
)

fun FrameScore.total() =
    firstRoll.score() + (secondRoll?.score() ?: 0) + bonus

typealias GameScores = List<FrameScore>

fun Game.score(): GameScores =
    mapIndexed { i, frame ->
        scoreForFrame(i)
    }


private fun Game.scoreForFrame(i: Int): FrameScore {
    val frame = this[i]
    val bonus = when (frame) {
        is Spare -> bonus(forFrame = i, bonusRolls = 1)
        Strike -> bonus(forFrame = i, bonusRolls = 2)
        is BonusRollForSpare -> frame.bonusRoll.score()
        is BonusRollsForStrike -> frame.bonusRoll1.score() + frame.bonusRoll2.score()
        is OpenFrame -> 0
        is FirstBonusRollForStrike -> frame.bonusRoll1.score()
        is IncompleteFrame -> 0
    }
    
    return FrameScore(frame.firstRoll, frame.secondRoll, bonus)
}

fun GameScores.total() = sumOf { it.total() }


private fun Frame.rolls(): List<PinCount> = when (this) {
    is IncompleteFrame -> listOf(firstRoll)
    is OpenFrame -> listOf(firstRoll, secondRoll)
    is Spare -> listOf(firstRoll, secondRoll)
    is Strike -> listOf(firstRoll)
    is BonusRollForSpare -> listOf(firstRoll, secondRoll, bonusRoll)
    is FirstBonusRollForStrike -> listOf(strike.firstRoll, bonusRoll1)
    is BonusRollsForStrike -> listOf(strike.firstRoll, bonusRoll1, bonusRoll2)
}

private fun List<Frame>.bonus(forFrame: Int, bonusRolls: Int) =
    drop(forFrame + 1).flatMap { it.rolls() }.map { it.score() }.take(bonusRolls).sum()


fun Game.isOver(): Boolean =
    size == framesPerGame && (last() is CompleteFinalFrame)
