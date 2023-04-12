package dev.javaToKotlin

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class FrameTest {

    @Test
    fun `Two rolls in an UnplayedFrame`() {
        var frame: Frame = UnplayedFrame()
        assertTrue(frame is UnplayedFrame)

        frame = frame.roll(Pinfall(1))
        assertTrue(frame is PartialFrame)

        frame = frame.roll(Pinfall(3))
        assertTrue(frame is CompleteFrame)
    }

    @Test
    fun `Throw a strike`() {
        val frame = UnplayedFrame().roll(Pinfall(10))
        assertTrue(frame is CompleteFrame)
    }


}



