package com.natpryce.kotlinconf2023.bowling

import io.kotest.core.spec.style.AnnotationSpec
import kotlinx.collections.immutable.persistentListOf
import kotlin.test.assertTrue

class ScoringTest : AnnotationSpec() {
    @Test
    fun `no strikes, rolls 1, 2`() {
        val rolls = listOf(1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2)
        val endGame = rolls.fold(newPlayerFrames) { acc, roll -> acc.plusScore(roll) }
        assertTrue(endGame.score() == 30)
    }
    
    @Test
    fun `no strikes, rolls 9,0`() {
        val rolls = listOf(9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0)
        val endGame = rolls.fold(newPlayerFrames) { acc, roll -> acc.plusScore(roll) }
        assertTrue(endGame.score() == 90)
    }
    
    @Test
    fun `no strikes, different rolls`() {
        val rolls = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
        val endGame = rolls.fold(newPlayerFrames) { acc, roll -> acc.plusScore(roll) }
        assertTrue(endGame.score() == 90)
    }
    
    @Test
    fun `different spares`() {
        val rolls = listOf(0, 10, 1, 9, 2, 8, 3, 7, 4, 6, 5, 5, 6, 4, 7, 3, 8, 2, 9, 1, 4)
        val endGame = rolls.fold(newPlayerFrames) { acc, roll -> acc.plusScore(roll) }
        assertTrue(
            endGame.score() ==
                10 + 1 + 10 + 2 + 10 + 3 + 10 + 4 + 10 + 5 + 10 + 6 + 10 + 7 + 10 + 8 + 10 + 9 + 10 + 4
        )
    }
    
    @Test
    fun `only strikes`() {
        val rolls = listOf(10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10)
        val endGame = rolls.fold(newPlayerFrames) { acc, roll -> acc.plusScore(roll) }
        assertTrue(endGame.score() == 300)
    }
}
