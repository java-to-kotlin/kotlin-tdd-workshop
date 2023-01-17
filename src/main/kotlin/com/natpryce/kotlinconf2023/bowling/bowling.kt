package com.natpryce.kotlinconf2023.bowling

data class BowlingGame(
    val playerCount: Int,
    val rollsByPlayer : List<Int> = emptyList()
)

fun newGame(playerCount: Int) =
    BowlingGame(playerCount = playerCount)

fun BowlingGame.afterRoll(score: Int): BowlingGame =
    copy(rollsByPlayer = rollsByPlayer + score)

fun BowlingGame.nextPlayerToBowl(): Int =
    if (rollsByPlayer.isNotEmpty() && rollsByPlayer.last() < 10) {
        rollsByPlayer.lastIndex
    } else {
        rollsByPlayer.size % playerCount
    }
