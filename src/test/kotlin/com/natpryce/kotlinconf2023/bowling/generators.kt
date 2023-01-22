package com.natpryce.kotlinconf2023.bowling

import io.kotest.property.Arb
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map

// Most alleys will allow a maximum of six to eight players on a lane at once.
// In competition play, there is usually a maximum of five players per team.
fun Arb.Companion.playerCount(n: IntRange = 1..10) =
    int(n)

fun Arb.Companion.bowlingGame(playerCount: IntRange = 1..10) =
    playerCount(playerCount).map(::newGame)

fun Arb.Companion.roll(pinsDown: Int = 0, maxPins: Int = 10) =
    Arb.int(0..(maxPins - pinsDown))

fun Arb.Companion.frame(maxPins: Int = 10, maxPinsInFirstRoll: Int = maxPins) =
    roll(maxPins = maxPinsInFirstRoll)
        .flatMap { i ->
            roll(pinsDown = i, maxPins = maxPins)
                .map { j -> Pair(i, j) }
        }
