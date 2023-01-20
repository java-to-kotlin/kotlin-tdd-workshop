package com.natpryce.kotlinconf2023.bowling

import io.kotest.core.spec.style.AnnotationSpec

class EndGameTest : AnnotationSpec() {
    @Test
    @Ignore // ignoring test while fixing bug
    fun `end game after 10 complete frames each, no bonus rolls`() {
        val playerCount = 2
        
        val game = newGame(playerCount).repeated(10) {
            it.repeated(playerCount) { game ->
                game
                    .also { assert(!it.isOver()) }
                    .afterRoll(1)
                    .also { assert(!it.isOver()) }
                    .afterRoll(2)
            }
        }
        
        assert(game.isOver())
    }
}

fun BowlingGame.isOver(): Boolean =
    playerFrames.all { it.isComplete() }

private fun PlayerFrames.isComplete(): Boolean =
    size == 10
