package com.natpryce.kotlinconf2023.bowling

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.kotest.property.checkAll
import kotlin.test.assertTrue


class NewFrameTest : AnnotationSpec() {
    @Test
    suspend fun `two frames in which players score less than 10 in both rolls`() {
        checkAll(Arb.bowlingGame(2..10)) { game ->
            game
                .playRound()
                .playRound()
        }
    }
    
    private fun BowlingGame.playRound(): BowlingGame {
        return repeatedIndexed(playerCount) { player, turn ->
            assertTrue(turn.nextPlayerToBowl() == player)
            
            val (first, second) = Arb.frame(maxPinsInFirstRoll = 9, maxPins = 9).next()
            val nextTurn = turn.afterRoll(first).afterRoll(second)
            
            assertTrue(nextTurn.nextPlayerToBowl() != player)
            
            nextTurn
        }
    }
}