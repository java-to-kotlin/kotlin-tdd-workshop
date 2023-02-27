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
    
    @Test
    fun `strike, no bonus yet`() {
        assertTrue(
            StartOfGame
                .roll(10)
                .score()
                ==
                listOf(
                    FrameScore(roll1 = 10, roll2 = null, runningTotal = 10)
                )
        )
    }
    
    @Test
    fun `strike, bonus after one roll`() {
        assertTrue(
            StartOfGame
                .roll(10)
                .roll(4)
                .score()
                ==
                listOf(
                    FrameScore(roll1 = 10, roll2 = null, runningTotal = 14),
                    FrameScore(roll1 = 4, roll2 = null, runningTotal = 18)
                )
        )
    }
    
    @Test
    fun `strike, bonus after two rolls`() {
        assertTrue(
            StartOfGame
                .roll(10)
                .roll(4)
                .roll(3)
                .score()
                ==
                listOf(
                    FrameScore(roll1 = 10, roll2 = null, runningTotal = 17),
                    FrameScore(roll1 = 4, roll2 = 3, runningTotal = 24)
                )
        )
    }
    
    @Test
    fun `spare, bonus after one roll`() {
        StartOfGame
            .roll(6)
            .roll(4)
            .also {
                assertTrue(
                    it.score() == listOf(
                        FrameScore(roll1 = 6, roll2 = 4, runningTotal = 10),
                    )
                )
            }
            .roll(3)
            .also {
                assertTrue(
                    it.score() == listOf(
                        FrameScore(roll1 = 6, roll2 = 4, runningTotal = 13),
                        FrameScore(roll1 = 3, roll2 = null, runningTotal = 16),
                    )
                )
            }
            .roll(2)
            .also {
                assertTrue(
                    it.score() == listOf(
                        FrameScore(roll1 = 6, roll2 = 4, runningTotal = 13),
                        FrameScore(roll1 = 3, roll2 = 2, runningTotal = 18),
                    )
                )
            }
    }
}
