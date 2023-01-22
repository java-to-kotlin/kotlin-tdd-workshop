package com.natpryce.kotlinconf2023.bowling

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentList

data class Frame(
    val scores: PersistentList<Int>
) {
    override fun toString(): String {
        return if (scores.isEmpty()) "-" else scores.joinToString("+") + "=" + pins()
    }
}

val unplayedFrame = Frame(persistentListOf())

private fun Frame.isIncomplete(): Boolean =
    !isComplete()

val pinCount = 10
val frameLimit = pinCount

private fun Frame.isComplete(): Boolean =
    scores.size == 2 || pins() == pinCount

private fun Frame.isCompleteFinalFrame(): Boolean =
    when {
        scores.size < 2 -> false
        isStrike() || isSpare() == pinCount -> scores.size == 3
        else -> true
    }

fun Frame.isSpare() =
    scores[0] + scores[1]

fun Frame.isStrike() =
    scores[0] == pinCount

fun Frame.pins() =
    scores.sum()

private fun Frame.plusScore(score: Int): Frame =
    copy(scores = scores + score)

// Frames played by a single player in the game
typealias PlayerFrames = PersistentList<Frame>

val newPlayerFrames = persistentListOf(unplayedFrame)

private fun <T> PersistentList<T>.setLast(e: T) =
    set(size - 1, e)


fun PlayerFrames.plusScore(score: Int) =
    if (latestFrameComplete()) {
        this + unplayedFrame.plusScore(score)
    } else {
        setLast(last().plusScore(score))
    }

fun PlayerFrames.isOver(): Boolean =
    size == frameLimit && latestFrameComplete()

fun PlayerFrames.latestFrameComplete() =
    when (size) {
        frameLimit -> last().isCompleteFinalFrame()
        else -> last().isComplete()
    }

fun PlayerFrames.isReadyToBowl() =
    !latestFrameComplete()

// A game played by two or more players
data class BowlingGame(
    val playerFrames: PersistentList<PlayerFrames>
)

val BowlingGame.playerCount: Int
    get() = playerFrames.size

fun newGame(playerCount: Int) =
    BowlingGame(
        playerFrames = (1..playerCount).map { newPlayerFrames }.toPersistentList()
    )

fun BowlingGame.afterRoll(score: Int): BowlingGame {
    val player = nextPlayerToBowl()
    
    return copy(playerFrames = playerFrames.plusScoreForPlayer(player, score))
}

private fun PersistentList<PlayerFrames>.plusScoreForPlayer(player: Int, score: Int) =
    set(player, get(player).plusScore(score))

fun BowlingGame.nextPlayerToBowl(): Int =
    playerFrames
        .indexOfFirst {
            it.isReadyToBowl() || it.size < playerFrames.first().size
        }
        .coerceAtLeast(0)

fun BowlingGame.isOver(): Boolean =
    playerFrames.all { it.isOver() }
