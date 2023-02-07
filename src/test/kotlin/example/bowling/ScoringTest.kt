package example.bowling

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.property.Arb
import io.kotest.property.checkAll
import kotlin.test.assertTrue


class ScoringTest : AnnotationSpec() {
    @Test
    fun `new game - no frames, total score 0`() {
        val scores = newGame.score()
        
        assertTrue(scores.isEmpty())
        assertTrue(scores.total() == 0)
    }
    
    @Test
    suspend fun `one roll that is not a strike`() {
        checkAll(Arb.roll(max = 9.pins)) { n ->
            val scores = newGame.roll(n).score()
            
            assertTrue(
                scores == listOf(
                    FrameScore(n, bonus = 0)
                )
            )
            assertTrue(scores.total() == n.score())
        }
    }
    
    @Test
    suspend fun `two rolls, open frame`() {
        checkAll(Arb.openFrame()) { (first, second) ->
            val scores = newGame.roll(first).roll(second).score()
            
            assertTrue(
                scores == listOf(
                    FrameScore(first, second)
                )
            )
            assertTrue(scores.total() == first.score() + second.score())
        }
    }
    
    @Test
    fun `two rolls, spare`() {
        val scores = newGame.roll(6.pins).roll(4.pins).score()
        
        assertTrue(
            scores == listOf(
                FrameScore(6.pins, 4.pins)
            )
        )
        assertTrue(scores.total() == 10)
    }
    
    @Test
    fun `a strike`() {
        val scores = newGame.roll(10.pins).score()
        
        assertTrue(
            scores == listOf(
                FrameScore(firstRoll = 10.pins)
            )
        )
        assertTrue(scores.total() == 10)
    }
    
    @Test
    suspend fun `roll after open frame`() {
        checkAll(Arb.openFrame(), Arb.roll(max = 9.pins)) { (first, second), third ->
            val scores = newGame.roll(first).roll(second).roll(third).score()
            
            assertTrue(
                scores == listOf(
                    FrameScore(first, second),
                    FrameScore(third)
                )
            )
            assertTrue(scores.total() == first.score() + second.score() + third.score())
        }
    }
    
    @Test
    suspend fun `strike after open frame`() {
        checkAll(Arb.openFrame()) { (first, second) ->
            val scores = newGame.roll(first).roll(second).roll(10.pins).score()
            
            assertTrue(
                scores == listOf(
                    FrameScore(first, second),
                    FrameScore(10.pins)
                )
            )
            assertTrue(scores.total() == first.score() + second.score() + 10)
        }
    }
    
    @Test
    fun `roll after spare`() {
        val scores = newGame.roll(3.pins).roll(7.pins).roll(4.pins).score()
        
        assertTrue(
            scores == listOf(
                FrameScore(3.pins, 7.pins, bonus = 4),
                FrameScore(4.pins)
            )
        )
        assertTrue(scores.total() == 18)
    }
    
    @Test
    fun `single roll after strike`() {
        val scores = newGame.roll(10.pins).roll(3.pins).score()
        
        assertTrue(
            scores == listOf(
                FrameScore(10.pins, bonus=3),
                FrameScore(3.pins)
            )
        )
        assertTrue(scores.total() == 16)
    }
    
    @Test
    fun `open frame after strike`() {
        val scores = newGame.roll(10.pins).roll(3.pins).roll(2.pins).score()
        
        assertTrue(
            scores == listOf(
                FrameScore(10.pins, bonus=5),
                FrameScore(3.pins, 2.pins)
            )
        )
        assertTrue(scores.total() == 20)
    }
    
    @Test
    fun `spare after strike`() {
        val scores = newGame.roll(10.pins).roll(4.pins).roll(6.pins).score()
        
        assertTrue(
            scores == listOf(
                FrameScore(10.pins, bonus=10),
                FrameScore(4.pins, 6.pins)
            )
        )
        assertTrue(scores.total() == 30)
    }
    
    @Test
    fun `two strikes in a row`() {
        val scores = newGame.roll(10.pins).roll(10.pins).score()
        
        assertTrue(
            scores == listOf(
                FrameScore(10.pins, bonus=10),
                FrameScore(10.pins)
            )
        )
        assertTrue(scores.total() == 30)
    }
    
    @Test
    fun `three strikes in a row`() {
        val scores = newGame.roll(10.pins).roll(10.pins).roll(10.pins).score()
        
        assertTrue(
            scores == listOf(
                FrameScore(10.pins, bonus=20),
                FrameScore(10.pins, bonus = 10),
                FrameScore(10.pins)
            )
        )
        assertTrue(scores.total() == 60)
    }
}
