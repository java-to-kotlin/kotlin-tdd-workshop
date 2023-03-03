package example.bowling

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class NextPlayerToBowlTest {
    private val newGame = GameInProgress(
        StartOfGame,
        StartOfGame,
        StartOfGame
    )
    
    @Test
    fun `start of game`() {
        assertTrue(newGame.nextPlayerToBowl() == 0)
    }
    
    @Test
    fun `first player, first roll - partial frame`() {
        val game = newGame.roll(1)
        assertTrue(game.nextPlayerToBowl() == 0)
    }
    
    @Test
    fun `first player, first frame - open frame`() {
        val game = newGame.roll(2).roll(6)
        assertTrue(game.nextPlayerToBowl() == 1)
    }
    
    @Test
    fun `first player, first frame - spare`() {
        val game = newGame.roll(1).roll(9)
        assertTrue(game.nextPlayerToBowl() == 1)
    }
    
    @Test
    fun `first player, first frame - strike`() {
        val game = newGame.roll(1).roll(9)
        assertTrue(game.nextPlayerToBowl() == 1)
    }
    
    @Test
    fun `first two players, second player open frame`() {
        newGame.roll(1).roll(2)
            .roll(3)
            .also { assertTrue(it.nextPlayerToBowl() == 1) }
            .roll(4)
            .also { assertTrue(it.nextPlayerToBowl() == 2) }
    }
    
    @Test
    fun `complete one turn, back to first player`() {
        newGame.roll(1).roll(2)
            .roll(3).roll(4)
            .roll(5).roll(1)
            .also { assertTrue(it.nextPlayerToBowl() == 0) }
    }
    
    @Test
    fun `next player after final open frame`() {
        startOfLastFrame()
            .roll(1)
            .also { assertTrue(it.nextPlayerToBowl() == 0) }
            .roll(2)
            .also { assertTrue(it.nextPlayerToBowl() == 1) }
    }
    
    @Test
    fun `next player after bonus roll for final spare`() {
        startOfLastFrame()
            .roll(4)
            .also { assertTrue(it.nextPlayerToBowl() == 0) }
            .roll(6)
            .also { assertTrue(it.nextPlayerToBowl() == 0) }
            .roll(2)
            .also { assertTrue(it.nextPlayerToBowl() == 1) }
    }
    
    @Test
    fun `next player after bonus rolls for final strike`() {
        startOfLastFrame()
            .roll(10)
            .also { assertTrue(it.nextPlayerToBowl() == 0) }
            .roll(1)
            .also { assertTrue(it.nextPlayerToBowl() == 0) }
            .roll(2)
            .also { assertTrue(it.nextPlayerToBowl() == 1) }
    }
    
    private fun startOfLastFrame(): GameInProgress {
        val lastFrame = (1..9).fold(newGame) { game, _ ->
            (1..3).fold(game) { turn, _ ->
                turn.roll(1).roll(1)
            }
        }
        
        assertTrue(lastFrame.nextPlayerToBowl() == 0)
        
        return lastFrame
    }
}

class EndOfGameTest {
    @Test
    fun `game over after final open frame`() {
        val startOfLastFrame = (1..9)
            .fold(StartOfGame as Frame) { game, _ -> game.roll(1).roll(2) }
        
        startOfLastFrame
            .roll(1)
            .also { assertTrue(it !is GameOver) }
            .roll(2)
            .also { assertTrue(it is GameOver) }
    }
    
    @Test
    fun `game over after one bonus roll after final spare`() {
        val startOfLastFrame = (1..9)
            .fold(StartOfGame as Frame) { game, _ -> game.roll(1).roll(2) }
        
        startOfLastFrame
            .roll(3)
            .also { assertTrue(it !is GameOver) }
            .roll(7)
            .also { assertTrue(it !is GameOver) }
            .roll(1)
            .also { assertTrue(it is GameOver) }
    }
    
    @Test
    fun `game over after two bonus rolls after final strike`() {
        val startOfLastFrame = (1..9)
            .fold(StartOfGame as Frame) { game, _ -> game.roll(1).roll(2) }
        
        startOfLastFrame
            .roll(10)
            .also { assertTrue(it !is GameOver) }
            .roll(1)
            .also { assertTrue(it !is GameOver) }
            .roll(2)
            .also { assertTrue(it is GameOver) }
    }
}

fun GameInProgress(vararg playerState: Frame) =
    GameInProgress(playerState.asList())

