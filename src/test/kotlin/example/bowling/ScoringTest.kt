package example.bowling

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
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
        checkAll(Arb.int(0..9)) { n ->
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
    fun `two rolls, open frame`() {
        val scores = newGame.roll(4).roll(3).score()
        
        assertTrue(
            scores == listOf(
                OpenFrame(firstRoll = 4, secondRoll = 3).scoredAs(7)
            )
        )
        assertTrue(scores.total() == 7)
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
    fun `roll after open frame`() {
        val scores = newGame.roll(3).roll(5).roll(4).score()
    
        assertTrue(
            scores == listOf(
                OpenFrame(firstRoll = 3, secondRoll = 5).scoredAs(8),
                IncompleteFrame(firstRoll = 4).scoredAs(4)
            )
        )
        assertTrue(scores.total() == 12)
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
}
