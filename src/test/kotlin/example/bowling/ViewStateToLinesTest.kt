package example.bowling

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class ViewStateToLinesTest {
    @Test
    fun `game in progress`() {
        val viewState = GameInProgressViewState(
            playerScores = listOf(
                listOf(FrameScore(1, 2, 3)),
                listOf(FrameScore(10, null, 10))
            ),
            nextPlayerToBowl = 0
        )
        
        val lines = viewState.toLines()
        
        assertTrue(
            lines ==
                listOf(
                    "PLAYER 1,2,3 3",
                    "PLAYER 10,,10 10",
                    "NEXT 0"
                )
        )
    }
    
    @Test
    fun `game over`() {
        val viewState = GameOverViewState(
            playerScores = listOf(
                listOf(FrameScore(1, 2, 3), FrameScore(4, 5, 12)),
                listOf(FrameScore(10, null, 20), FrameScore(6, 4, 30))
            ),
            winners = listOf(1)
        )
        
        val lines = viewState.toLines()
        
        assertTrue(
            lines ==
                listOf(
                    "PLAYER 1,2,3 4,5,12 12",
                    "PLAYER 10,,20 6,4,30 30",
                    "WINNER 1"
                )
        )
    }
}