package dev.javaToKotlin

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class FrameTest {

    @Test
    fun `frame starts as UnplayedFrame`() {
        val frame = UnplayedFrame()

        assertTrue(frame is Frame)

        val nextState: PartialFrame =
            frame.roll(Pinfall(1)) as PartialFrame
    }



}


