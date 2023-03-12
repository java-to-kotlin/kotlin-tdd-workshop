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
        val line = Line("Fred", NonNegativeInt(1))
        assertTrue(line is PlayableLine)

        val line2 = line.roll(3.pins)
        assertTrue(line2 is PlayableLine)
    }

    @Test
    fun `two rolls makes a one frame line not playable`() {
        val line = Line("Fred", NonNegativeInt(1))
        assertTrue(line is PlayableLine)

        val line2 = line.roll(3.pins)
        assertTrue(line2 is PlayableLine)

        val line3 = line.roll(4.pins)
        assertFalse(line3 is PlayableLine)
    }
}

val Int.pins get() = PinCount(this)!!

