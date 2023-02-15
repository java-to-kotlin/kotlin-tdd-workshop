package example.bowling

import kotlin.test.assertTrue
import kotlin.test.Test
import kotlin.test.assertIs

val Int.pins get() : Pinfall = Pinfall(this) ?: error("bad pinfall $this")

class BowlingTests {
    @Test
    fun `rolling a strike`() {
        assertTrue(
            UnplayedFrame.roll(10.pins) is Strike)
    }
    
    @Test
    fun `rolling 5`() {
        assertTrue(
            UnplayedFrame.roll(5.pins) is IncompleteFrame
        )
    }
    
    @Test
    fun `completing a frame`() {
        val afterFirstRoll = UnplayedFrame.roll(5.pins)
        
        assertTrue(afterFirstRoll is IncompleteFrame)
        assertTrue(
            afterFirstRoll.roll(2.pins) is OpenFrame
        )
    }
}
