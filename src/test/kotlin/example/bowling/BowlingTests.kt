package example.bowling

import kotlin.test.assertTrue
import kotlin.test.Test

val Int.pins get() : Pinfall = Pinfall(this) ?: error("bad pinfall $this")

class BowlingTests {
    @Test
    fun `test it`() {
        assertTrue(
            UnplayedFrame.roll(10.pins) == Strike)
    }
}
