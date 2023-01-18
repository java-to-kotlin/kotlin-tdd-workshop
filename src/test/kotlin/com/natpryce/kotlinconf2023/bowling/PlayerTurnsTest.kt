package com.natpryce.kotlinconf2023.bowling

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.next
import io.kotest.property.checkAll

private val BowlingGame.playerCount: Int
    get() = playerFrames.size

// Most alleys will allow a maximum of six to eight players on a lane at once.
// In competition play, there is usually a maximum of five players per team.
private fun Arb.Companion.playerCount() = int(2..10)
private fun Arb.Companion.bowlingGame() = playerCount().map(::newGame)
private fun Arb.Companion.roll(pinsDown: Int = 0, maxPins: Int = 10) = Arb.int(0..(maxPins - pinsDown))
private fun Arb.Companion.frame(maxPins: Int = 10, maxPinsInFirstRoll: Int = maxPins) =
    roll(maxPins = maxPinsInFirstRoll)
        .flatMap { i ->
            roll(pinsDown = i, maxPins = maxPins)
                .map { j -> Pair(i, j) }
        }


fun <T> T.repeated(times: Int, f: (T) -> T): T {
    var result = this
    repeat(times) {
        result = f(result)
    }
    return result
}


class PlayerTurnsTest : AnnotationSpec() {
    @Test
    suspend fun `new game, first player to start`() {
        checkAll(Arb.bowlingGame()) { game ->
            assert(game.nextPlayerToBowl() == 0)
        }
    }
    
    @Test
    suspend fun `all players bowl a strike, back to first player`() {
        checkAll(Arb.bowlingGame()) { newGame ->
            newGame
                .repeated(times = newGame.playerCount - 1) { beforeRoll ->
                    val player = beforeRoll.nextPlayerToBowl()
                    
                    val result = beforeRoll.afterRoll(10)
                    
                    assert(result.nextPlayerToBowl() == player + 1)
                    
                    result
                    
                }
                .then { finalRoll ->
                    assert(finalRoll.afterRoll(10).nextPlayerToBowl() == 0)
                }
        }
    }
    
    @Test
    suspend fun `players score less than 10 in first roll, back to first player`() {
        checkAll(Arb.bowlingGame()) { newGame ->
            newGame
                .repeated(times = newGame.playerCount) {
                    val (first, second) = Arb.frame(maxPinsInFirstRoll = 9).next()
                    
                    it.afterRoll(first).afterRoll(second)
                }
                .then {
                    assert(it.nextPlayerToBowl() == 0)
                }
        }
    }
}

inline fun <T> T.then(statement: (T) -> Unit): T = also(statement)

