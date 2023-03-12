package dev.javaToKotlin

import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LineTests {
    @Test
    fun `an empty line is not playable`() {
        val line = Line("Fred", NonNegativeInt(0))
        assertFalse(line is PlayableLine)
    }

    @Test
    fun `a new line with one frame is playable`() {
        val line = Line("Fred", NonNegativeInt(1))
        assertTrue(line is PlayableLine)
    }

    @Test
    fun `a single roll on a one frame line is playable`() {
        var line = Line("Fred", NonNegativeInt(1))
        assertTrue(line is PlayableLine)

        line = line.roll(3.pins)
        assertTrue(line is PlayableLine)
    }

    @Test
    fun `two rolls makes a one frame line not playable`() {
        var line = Line("Fred", NonNegativeInt(1))
        assertTrue(line is PlayableLine)

        line = line.roll(3.pins)
        assertTrue(line is PlayableLine)

        line = line.roll(4.pins)
        assertFalse(line is PlayableLine)
    }

    @Test
    fun `two rolls make an unplayed frame not playable`() {
        var frame: Frame = UnplayedFrame()
        assertTrue(frame is PlayableFrame)

        frame = frame.roll(3.pins)
        assertTrue(frame is PlayableFrame)

        frame = frame.roll(3.pins)
        assertFalse(frame is PlayableFrame)
    }
}

val Int.pins get() = PinCount(this)!!

