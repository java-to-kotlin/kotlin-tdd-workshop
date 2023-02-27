package example.bowling

import io.kotest.core.spec.style.AnnotationSpec
import kotlin.test.assertTrue

class GameplayTest : AnnotationSpec() {
    @Test
    fun `complete first frame as open frame`() {
        StartOfGame
            .roll(2)
            .also { assertTrue(it !is CompleteFrame) }
            .roll(1)
            .also { assertTrue(it is CompleteFrame) }
    }
    
    @Test
    fun `complete first frame as spare`() {
        StartOfGame
            .roll(8)
            .also { assertTrue(it !is CompleteFrame) }
            .roll(2)
            .also {
                assertTrue(it is CompleteFrame)
                assertTrue(it is Spare)
            }
    }
    
    @Test
    fun `complete first frame as strike`() {
        StartOfGame
            .roll(10)
            .also { 
                assertTrue(it is CompleteFrame) 
                assertTrue(it is Strike)
            }
    }
}
