package com.natpryce.kotlinconf2023.bowling

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

data class Frame(
    val scores: PersistentList<Int>
)

val unplayedFrame = Frame(persistentListOf())

private fun Frame.isIncomplete(): Boolean =
    !isComplete()

private fun Frame.isComplete(): Boolean =
    scores.size == 2 || pins() == 10

private fun Frame.pins() =
    scores.sum()

private fun Frame.plusScore(score: Int): Frame =
    copy(scores = scores + score)



// Frames played by a single player in the game
typealias PlayerFrames = PersistentList<Frame>

val newPlayerFrames = persistentListOf(unplayedFrame)

private fun PlayerFrames.plusScore(score: Int) =
    set(0, this[0].plusScore(score))

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
    val frames = if (player == 0) playerFrames.withNewFrames() else playerFrames
    
    return copy(
        playerFrames = frames.plusScoreForPlayer(player, score)
    )
}

private fun PersistentList<PlayerFrames>.plusScoreForPlayer(player: Int, score: Int) =
    set(player, get(player).plusScore(score))

fun BowlingGame.nextPlayerToBowl(): Int =
    playerFrames
        .indexOfFirst { it[0].isIncomplete() }
        .takeUnless { it < 0 }
        ?: 0

