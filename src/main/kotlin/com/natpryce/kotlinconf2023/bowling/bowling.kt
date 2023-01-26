package com.natpryce.kotlinconf2023.bowling

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentList

data class Frame(
    val rolls: PersistentList<Int>
) {
    override fun toString(): String {
        return if (rolls.isEmpty()) "-" else rolls.joinToString("+") + "=" + pins()
    }
}

val unplayedFrame = Frame(persistentListOf())

val pinCount = 10
val framesPerGame = 10

fun Frame.isComplete(): Boolean =
    rolls.size == 2 || pins() == pinCount

private fun Frame.isCompleteFinalFrame(): Boolean =
    when {
        rolls.size < 2 -> false
        isStrike() || isSpare() -> rolls.size == 3
        else -> true
    }

fun Frame.isSpare() =
    rolls.size >= 2 && (rolls[0] + rolls[1]) == pinCount

fun Frame.isStrike() =
    rolls.size >= 1 && rolls[0] == pinCount

fun Frame.pins() =
    when {
        rolls.isEmpty() -> 0
        rolls[0] == 10 -> 10
        else -> rolls[0] + rolls.getOrElse(1) { 0 }
    }

private fun Frame.plusRoll(roll: Int): Frame =
    copy(rolls = rolls + roll)

// Frames played by a single player in the game
typealias PlayerFrames = PersistentList<Frame>

val newPlayerFrames = persistentListOf(unplayedFrame)

private fun <T> PersistentList<T>.setLast(e: T) =
    set(size - 1, e)


fun PlayerFrames.roll(score: Int) =
    if (latestFrameComplete()) {
        this + unplayedFrame.plusRoll(score)
    } else {
        setLast(last().plusRoll(score))
    }

fun PlayerFrames.isOver(): Boolean =
    size == framesPerGame && latestFrameComplete()

fun PlayerFrames.latestFrameComplete() =
    when (size) {
        framesPerGame -> last().isCompleteFinalFrame()
        else -> last().isComplete()
    }

fun PlayerFrames.isReadyToBowl() =
    !latestFrameComplete()

private fun PlayerFrames.maxNextRoll(): Int {
    val lastFrame = last()
    return if (lastFrame.isComplete()) pinCount else (pinCount - lastFrame.pins())
}
fun PlayerFrames.score(): Int =
    (0 until size).sumOf(this::scoreForFrame)

fun PlayerFrames.scoreForFrame(i: Int): Int {
    val frame = get(i)
    val pins = frame.pins()
    val bonus = when {
        frame.isStrike() -> strikeBonusForFrame(frame, i)
        frame.isSpare() -> spareBonusForFrame(frame, i)
        else -> 0
    }
    
    return pins + bonus
}

private fun PlayerFrames.spareBonusForFrame(frame: Frame, i: Int): Int =
    bonusRolls(frame, i).firstOrNull() ?: 0

private fun PlayerFrames.strikeBonusForFrame(frame: Frame, i: Int): Int =
    bonusRolls(frame, i).take(2).sum()

private fun PlayerFrames.bonusRolls(frame: Frame, i: Int) =
    frame.rolls.drop(if (frame.isStrike()) 1 else 2) +
        (getOrNull(i + 1)?.rolls.orEmpty()) +
        (getOrNull(i + 2)?.rolls.orEmpty())


// A game played by two or more players
data class BowlingGame(
    val players: PersistentList<PlayerFrames>
)

val BowlingGame.playerCount: Int
    get() = players.size

fun newGame(playerCount: Int) =
    BowlingGame(
        players = (1..playerCount).map { newPlayerFrames }.toPersistentList()
    )

fun BowlingGame.afterRoll(score: Int): BowlingGame {
    val player = nextPlayerToBowl()
    
    return copy(players = players.rollByPlayer(player, score))
}

private fun PersistentList<PlayerFrames>.rollByPlayer(player: Int, roll: Int) =
    set(player, get(player).roll(roll))

fun BowlingGame.nextPlayerToBowl(): Int =
    players
        .indexOfFirst {
            it.isReadyToBowl() || it.size < players.first().size
        }
        .coerceAtLeast(0)

fun BowlingGame.maxNextRoll(): Int {
    return players[nextPlayerToBowl()].maxNextRoll()
}

fun BowlingGame.isOver(): Boolean =
    players.all { it.isOver() }

