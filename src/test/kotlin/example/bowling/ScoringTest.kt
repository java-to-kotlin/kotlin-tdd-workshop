package example.bowling

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.checkAll
import kotlin.test.assertTrue

fun Arb.Companion.roll(max: Int = 10) = int(0..max)

fun Arb.Companion.openFrame(): Arb<Pair<Int, Int>> =
    roll(max = 9).flatMap { i -> roll(max = 9 - i).map { j -> Pair(first = i, second = j) } }

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
}
