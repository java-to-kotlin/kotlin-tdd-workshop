package example.bowling

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class GameScoreTests {
    @Test
    fun `new game`() {
        assertTrue(StartOfGame.score() == emptyList<FrameScore>())
    }
    
    @Test
    fun `open and partial frames`() {
        assertTrue(
            StartOfGame
                .roll(1).roll(2)
                .roll(3)
                .score()
                ==
                listOf(
                    FrameScore(roll1 = 1, roll2 = 2, runningTotal = 3),
                    FrameScore(roll1 = 3, roll2 = null, runningTotal = 6)
                )
        )
    }
}
