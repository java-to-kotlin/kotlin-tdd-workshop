package com.natpryce.kotlinconf2023.bowling

import io.kotest.core.spec.style.AnnotationSpec

class EndGameTest : AnnotationSpec() {
    @Test
    fun `end game after 10 complete frames each, no bonus rolls`() {
        val playerCount = 2
        
        val game = newGame(playerCount).repeated(10) { game ->
            game.repeated(playerCount) { turn ->
                turn
                    .also { assert(!it.isOver()) }
                    .afterRoll(1)
                    .also { assert(!it.isOver()) }
                    .afterRoll(2)
            }
        }
        
        assert(game.isOver())
    }
}
