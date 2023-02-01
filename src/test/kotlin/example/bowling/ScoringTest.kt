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
        checkAll(Arb.roll(max = 9)) { n ->
            val scores = newGame.roll(n).score()
            
            assertTrue(
                scores == listOf(
                    IncompleteFrame(firstRoll = n).scoredAs(n)
                )
            )
            assertTrue(scores.total() == n)
        }
    }
    
    @Test
    suspend fun `two rolls, open frame`() {
        checkAll(Arb.openFrame()) { (first, second) ->
            val scores = newGame.roll(first).roll(second).score()
            
            assertTrue(
                scores == listOf(
                    OpenFrame(firstRoll = first, secondRoll = second).scoredAs(first + second)
                )
            )
            assertTrue(scores.total() == first + second)
        }
    }
    
    @Test
    fun `two rolls, spare`() {
        val scores = newGame.roll(6).roll(4).score()
        
        assertTrue(
            scores == listOf(
                Spare(firstRoll = 6).scoredAs(10)
            )
        )
        assertTrue(scores.total() == 10)
    }
    
    @Test
    fun `a strike`() {
        val scores = newGame.roll(10).score()
        
        assertTrue(
            scores == listOf(
                Strike.scoredAs(10)
            )
        )
        assertTrue(scores.total() == 10)
    }
    
    @Test
    suspend fun `roll after open frame`() {
        checkAll(Arb.openFrame(), Arb.roll(max = 9)) { (first, second), third ->
            val scores = newGame.roll(first).roll(second).roll(third).score()
            
            assertTrue(
                scores == listOf(
                    OpenFrame(firstRoll = first, secondRoll = second).scoredAs(first + second),
                    IncompleteFrame(firstRoll = third).scoredAs(third)
                )
            )
            assertTrue(scores.total() == first + second + third)
        }
    }
    
    @Test
    suspend fun `strike after open frame`() {
        checkAll(Arb.openFrame()) { (first, second) ->
            val scores = newGame.roll(first).roll(second).roll(10).score()
            
            assertTrue(
                scores == listOf(
                    OpenFrame(firstRoll = first, secondRoll = second).scoredAs(first + second),
                    Strike.scoredAs(10)
                )
            )
            assertTrue(scores.total() == first + second + 10)
        }
    }
    
    @Test
    fun `roll after spare`() {
        val scores = newGame.roll(3).roll(7).roll(4).score()
        
        assertTrue(
            scores == listOf(
                Spare(3).scoredAs(14),
                IncompleteFrame(4).scoredAs(4)
            )
        )
        assertTrue(scores.total() == 18)
    }
    
    @Test
    fun `single roll after strike`() {
        val scores = newGame.roll(10).roll(3).score()
        
        assertTrue(
            scores == listOf(
                Strike.scoredAs(13),
                IncompleteFrame(3).scoredAs(3)
            )
        )
        assertTrue(scores.total() == 16)
    }
    
    @Test
    fun `open frame after strike`() {
        val scores = newGame.roll(10).roll(3).roll(2).score()
        
        assertTrue(
            scores == listOf(
                Strike.scoredAs(15),
                OpenFrame(3,2).scoredAs(5)
            )
        )
        assertTrue(scores.total() == 20)
    }
    
    @Test
    fun `spare after strike`() {
        val scores = newGame.roll(10).roll(4).roll(6).score()
        
        assertTrue(
            scores == listOf(
                Strike.scoredAs(20),
                Spare(4).scoredAs(10)
            )
        )
        assertTrue(scores.total() == 30)
    }
    
    @Test
    fun `two strikes in a row`() {
        val scores = newGame.roll(10).roll(10).score()
    
        assertTrue(
            scores == listOf(
                Strike.scoredAs(20),
                Strike.scoredAs(10)
            )
        )
        assertTrue(scores.total() == 30)
    }
    
    @Test
    fun `three strikes in a row`() {
        val scores = newGame.roll(10).roll(10).roll(10).score()
        
        assertTrue(
            scores == listOf(
                Strike.scoredAs(30),
                Strike.scoredAs(20),
                Strike.scoredAs(10)
            )
        )
        assertTrue(scores.total() == 60)
    }
}
