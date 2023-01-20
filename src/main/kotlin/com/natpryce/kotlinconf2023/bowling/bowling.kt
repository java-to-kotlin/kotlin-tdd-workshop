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

private fun Frame.isComplete(): Boolean =
    scores.size == 2 || pins() == 10

fun Frame.pins() =
    scores.sum()

private fun Frame.plusScore(score: Int): Frame =
    copy(scores = scores + score)



// Frames played by a single player in the game
typealias PlayerFrames = PersistentList<Frame>

val newPlayerFrames = persistentListOf(unplayedFrame)

private fun <T> PersistentList<T>.setLast(e: T) =
    set(size - 1, e)

private fun PlayerFrames.plusScore(score: Int) =
    setLast(last().plusScore(score))

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

fun PersistentList<PlayerFrames>.withNewFrames() =
    map { it + unplayedFrame }.toPersistentList()

fun BowlingGame.afterRoll(score: Int): BowlingGame {
    val player = nextPlayerToBowl()
    
    return copy(playerFrames = playerFrames.plusScoreForPlayer(player, score))
        .startNewRound()
}

private fun BowlingGame.startNewRound(): BowlingGame =
    if (playerFrames.last().last().isComplete()) {
        copy(playerFrames=playerFrames.withNewFrames())
    } else {
        this
    }

private fun PersistentList<PlayerFrames>.plusScoreForPlayer(player: Int, score: Int) =
    set(player, get(player).plusScore(score))

fun BowlingGame.nextPlayerToBowl(): Int =
    playerFrames
        .indexOfFirst { it.last().isIncomplete() }
        .takeUnless { it < 0 }
        ?: 0
