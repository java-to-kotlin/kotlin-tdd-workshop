package dev.javaToKotlin

import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LineTests {
    @Test
    fun `an empty line is not playable`() {
        val line = Line("Fred", 0.frames)
        assertFalse(line is PlayableLine)
    }

    @Test
    fun `a new line with one frame is playable`() {
        val line = Line("Fred", 1.frames)
        assertTrue(line is PlayableLine)
    }

    @Test
    fun `two rolls completes a one frame line`() {
        var line = Line("Fred", 1.frames)
        assertTrue(line is PlayableLine)

        line = line.roll(3.pins)
        assertTrue(line is PlayableLine)

        line = line.roll(4.pins)
        assertFalse(line is PlayableLine)
    }

    @Test
    fun `four rolls completes a two frame line`() {
        var line = Line("Fred", 2.frames)
        assertTrue(line is PlayableLine)

        line = line.roll(3.pins)
        assertTrue(line is PlayableLine)

        line = line.roll(4.pins)
        assertTrue(line is PlayableLine)

        line = line.roll(5.pins)
        assertTrue(line is PlayableLine)

        line = line.roll(6.pins)
        assertFalse(line is PlayableLine)
    }

    @Test
    fun `a strike and two rolls completes a two frame line`() {
        var line = Line("Fred", 2.frames)
        assertTrue(line is PlayableLine)

        line = line.roll(10.pins)
        assertTrue(line is PlayableLine)

        line = line.roll(5.pins)
        assertTrue(line is PlayableLine)

        line = line.roll(6.pins)
        assertFalse(line is PlayableLine)
    }


    @Test
    fun `a strike in the final frame gives two extra rolls`() {
        var line = Line("Fred", 1.frames)
        assertTrue(line is PlayableLine)

        line = line.roll(10.pins)
        assertTrue(line is PlayableLine)

        line = line.roll(3.pins)
        assertTrue(line is PlayableLine)

        line = line.roll(4.pins)
        assertFalse(line is PlayableLine)

    }
}

val Int.pins get() = PinCount(this)!!
private val Int.frames get() = NonNegativeInt(this)



