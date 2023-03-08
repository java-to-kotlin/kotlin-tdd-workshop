package dev.javaToKotlin

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Tests {

    @Test
    fun `zero frame game`() {
        val line = lineFor("Fred", frameCount = 0)
        assertEquals("Fred", line.scorecard())
        assertFalse(line is PlayableLine)
    }

    @Test
    fun `single frame game`() {
        var line = lineFor("Fred", frameCount = 1)
        assertEquals("Fred| , , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(4.pins)
        assertEquals("Fred|4, , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(2.pins)
        assertEquals("Fred|4,2, ", line.scorecard())
        assertTrue(line is CompletedLine)
    }

    @Test
    fun `two frame game`() {
        var line = lineFor("Fred", frameCount = 2)
        assertEquals("Fred| , | , , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(4.pins)
        assertEquals("Fred|4, | , , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(2.pins)
        assertEquals("Fred|4,2| , , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(5.pins)
        assertEquals("Fred|4,2|5, , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(3.pins)
        assertEquals("Fred|4,2|5,3, ", line.scorecard())
        assertTrue(line is CompletedLine)
    }

    @Test
    fun `two frame game with initial strike`() {
        var line = lineFor("Fred", frameCount = 2)
        assertEquals("Fred| , | , , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(10.pins)
        assertEquals("Fred| ,X| , , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(5.pins)
        assertEquals("Fred| ,X|5, , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(3.pins)
        assertEquals("Fred| ,X|5,3, ", line.scorecard())
        assertTrue(line is CompletedLine)
    }

    @Test
    fun `two frame game with initial spare`() {
        var line = lineFor("Fred", frameCount = 2)
        assertEquals("Fred| , | , , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(9.pins)
        assertEquals("Fred|9, | , , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(1.pins)
        assertEquals("Fred|9,/| , , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(5.pins)
        assertEquals("Fred|9,/|5, , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(3.pins)
        assertEquals("Fred|9,/|5,3, ", line.scorecard())
        assertTrue(line is CompletedLine)
    }

    @Test
    fun `strike in final frame gives another two rolls`() {
        var line = lineFor("Fred", frameCount = 1)
        assertEquals("Fred| , , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(10.pins)
        assertEquals("Fred|X, , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(5.pins)
        assertEquals("Fred|X,5, ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(3.pins)
        assertEquals("Fred|X,5,3", line.scorecard())
        assertTrue(line is CompletedLine)
    }

    @Test
    fun `spare in final frame gives another roll`() {
        var line = lineFor("Fred", frameCount = 1)
        assertEquals("Fred| , , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(9.pins)
        assertEquals("Fred|9, , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(1.pins)
        assertEquals("Fred|9,/, ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(3.pins)
        assertEquals("Fred|9,/,3", line.scorecard())
        assertTrue(line is CompletedLine)
    }
}

val Int.pins: PinCount
    get() = PinCount(this) ?: error("Invalid pincount value of ${this}")