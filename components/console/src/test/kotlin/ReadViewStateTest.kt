import bowling.console.FrameScore
import bowling.console.GameInProgressState
import bowling.console.GameOverState
import bowling.console.PlayerScores
import bowling.console.readViewState
import bowling.console.unplayedFrame
import io.kotest.core.spec.style.AnnotationSpec
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ReadViewStateTest : AnnotationSpec() {
    @Test
    fun `game in progress`() {
        val text = """
            PLAYER 1,0,1 1
            PLAYER 10,,10 10
            NEXT 0
        """.trimIndent()
        
        val parsed = text.reader().buffered().readViewState()
        
        assertTrue(
            parsed == GameInProgressState(
                scoresPerPlayer = listOf(
                    PlayerScores(
                        frames = listOf(FrameScore(1, 0, 1)),
                        total = 1
                    ),
                    PlayerScores(
                        frames = listOf(FrameScore(10, null, 10)),
                        total = 10
                    )
                ),
                nextPlayerToBowl = 0
            )
        )
    }
    
    @Test
    fun `controller can send empty frames`() {
        val text = """
            PLAYER 1,0,1 ,, ,, ,, ,, ,, ,, ,, ,, ,, 1
            NEXT 0
        """.trimIndent()
        
        val parsed = text.reader().buffered().readViewState()
        
        assertTrue(
            parsed == GameInProgressState(
                scoresPerPlayer = listOf(
                    PlayerScores(
                        frames = listOf(FrameScore(1, 0, 1)) + (2..10).map { unplayedFrame },
                        total = 1
                    )
                ),
                
                nextPlayerToBowl = 0
            )
        )
    }
    
    @Test
    fun `game over`() {
        val text = """
            PLAYER 1,0,1 1,0,2 1,0,3 1,0,4 1,0,5 1,0,6 1,0,7 1,0,8 1,0,9 1,0,10 10
            WINNER 0
        """.trimIndent()
        
        val parsed = text.reader().buffered().readViewState()
        
        assertTrue(
            parsed == GameOverState(
                scoresPerPlayer = listOf(
                    PlayerScores(
                        frames = (1..10).map { FrameScore(1, 0, it) },
                        total = 10
                    )
                ),
                winningPlayers = listOf(0)
            )
        )
    }
    
    @Test
    fun `multiple winners`() {
        val text = """
            PLAYER 1,0,1 1,0,2 1,0,3 1,0,4 1,0,5 1,0,6 1,0,7 1,0,8 1,0,9 0,0,9 9
            PLAYER 1,0,1 1,0,2 1,0,3 1,0,4 1,0,5 1,0,6 1,0,7 1,0,8 1,0,9 1,0,10 10
            PLAYER 1,0,1 1,0,2 1,0,3 1,0,4 1,0,5 1,0,6 1,0,7 1,0,8 1,0,9 1,0,10 10
            WINNER 1 2
        """.trimIndent()
        
        val parsed = text.reader().buffered().readViewState()
        
        assertIs<GameOverState>(parsed)
        
        assertTrue(parsed.winningPlayers == listOf(1, 2))
    }
}