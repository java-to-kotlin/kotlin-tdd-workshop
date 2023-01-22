package com.natpryce.kotlinconf2023.bowling

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.checkAll
import kotlin.test.assertTrue


fun Arb.Companion.frames(n: Int): Arb<PlayerFrames> = frames(n..n)

fun Arb.Companion.frames(n: IntRange): Arb<PlayerFrames> =
    Arb.list(Arb.frame(), n)
        .map { frames ->
            frames.fold(newPlayerFrames) { acc, (first, second) ->
                acc.plusScore(first).let {
                    if (first == 10) it else it.plusScore(second)
                }
            }
        }



fun PlayerFrames.isNotComplete(): Boolean = !isOver()

class PlayerFramesTest : AnnotationSpec() {
    @Test
    suspend fun `game is not over if fewer than ten turns`() {
        checkAll(Arb.frames(0..9)) { gameSoFar ->
            assertTrue(gameSoFar.isNotComplete())
        }
    }
    
    @Test
    suspend fun `game is over after ten turns with no bonus roll`() {
        checkAll(Arb.frames(9), Arb.frame(maxPins = 9)) { gameSoFar, (firstRoll, secondRoll) ->
            val afterLastFrame = gameSoFar.plusScore(firstRoll).plusScore(secondRoll)
            
            assertTrue(afterLastFrame.isOver())
        }
    }
    
    @Test
    suspend fun `game is over after ten turns with one bonus roll if last frame is a spare`() {
        checkAll(Arb.frames(9), Arb.roll(maxPins = 9), Arb.roll()) { gameSoFar, firstRoll, bonusRoll ->
            val beforeBonusRoll = gameSoFar.plusScore(firstRoll).plusScore(10 - firstRoll)
            assertTrue(beforeBonusRoll.isNotComplete())
            
            val afterBonusRoll = beforeBonusRoll.plusScore(bonusRoll)
            assertTrue(afterBonusRoll.isOver())
        }
    }
    
    @Test
    suspend fun `game is over after ten turns with two bonus rolls if last frame is a strike`() {
        checkAll(Arb.frames(9), Arb.roll(), Arb.roll()) { gameSoFar, firstBonusRoll, secondBonusRoll ->
            val strikeInFinalFrame = gameSoFar.plusScore(10)
            assertTrue(strikeInFinalFrame.isNotComplete())
            
            val afterFirstBonusRoll = strikeInFinalFrame.plusScore(firstBonusRoll)
            assertTrue(afterFirstBonusRoll.isNotComplete())
            
            val afterSecondBonusRoll = afterFirstBonusRoll.plusScore(secondBonusRoll)
            assertTrue(afterSecondBonusRoll.isOver())
        }
    }
}
