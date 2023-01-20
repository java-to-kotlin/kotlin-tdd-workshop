package com.natpryce.kotlinconf2023.bowling

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.kotest.property.checkAll


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
            val finalRoll =
                newGame.repeated(times = newGame.playerCount - 1) { beforeRoll ->
                    val player = beforeRoll.nextPlayerToBowl()
                    
                    val afterRoll = beforeRoll.afterRoll(10)
                    
                    assert(afterRoll.nextPlayerToBowl() == player + 1)
                    
                    afterRoll
                }
            
            assert(finalRoll.afterRoll(10).nextPlayerToBowl() == 0)
        }
    }
    
    @Test
    suspend fun `players score less than 10 in first roll, back to first player`() {
        checkAll(Arb.bowlingGame()) { newGame ->
            val finalRoll = newGame
                .repeated(times = newGame.playerCount) {
                    val (first, second) = Arb.frame(maxPinsInFirstRoll = 9).next()
                    
                    it.afterRoll(first).afterRoll(second)
                }
            
            assert(finalRoll.nextPlayerToBowl() == 0)
        }
    }
    
    @Test
    suspend fun `players score less than 10 in both rolls, back to first player`() {
        checkAll(Arb.bowlingGame()) { newGame ->
            val finalRoll = newGame
                .repeated(times = newGame.playerCount) { turn ->
                    val (first, second) = Arb.frame(maxPinsInFirstRoll = 9, maxPins = 9).next()
                    
                    val player = turn.nextPlayerToBowl()
    
                    val afterFirstRoll = turn.afterRoll(first)
                    assert(afterFirstRoll.playerFrames[player].size == turn.playerFrames[player].size)
                    val afterSecondRoll = afterFirstRoll.afterRoll(second)

                    afterSecondRoll
                }
            
            assert(finalRoll.nextPlayerToBowl() == 0)
        }
    }
}

