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
        assertEquals("Fred| , , , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(4.pins)
        assertEquals("Fred|4, , ,4", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(2.pins)
        assertEquals("Fred|4,2, ,6", line.scorecard())
        assertTrue(line is CompletedLine)
    }

    @Test
    fun `two frame game`() {
        var line = lineFor("Fred", frameCount = 2)
        assertEquals("Fred| , , | , , , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(4.pins)
        assertEquals("Fred|4, ,4| , , , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(2.pins)
        assertEquals("Fred|4,2,6| , , , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(5.pins)
        assertEquals("Fred|4,2,6|5, , ,11", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(3.pins)
        assertEquals("Fred|4,2,6|5,3, ,14", line.scorecard())
        assertTrue(line is CompletedLine)
    }

    @Test
    fun `two frame game with initial strike`() {
        var line = lineFor("Fred", frameCount = 2)
        assertEquals("Fred| , , | , , , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(10.pins)
        assertEquals("Fred| ,X,10| , , , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(5.pins)
        assertEquals("Fred| ,X,15|5, , ,20", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(3.pins)
        assertEquals("Fred| ,X,18|5,3, ,26", line.scorecard())
        assertTrue(line is CompletedLine)
    }

    @Test
    fun `two frame game with initial spare`() {
        var line = lineFor("Fred", frameCount = 2)
        assertEquals("Fred| , , | , , , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(9.pins)
        assertEquals("Fred|9, ,9| , , , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(1.pins)
        assertEquals("Fred|9,/,10| , , , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(5.pins)
        assertEquals("Fred|9,/,15|5, , ,20", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(3.pins)
        assertEquals("Fred|9,/,15|5,3, ,23", line.scorecard())
        assertTrue(line is CompletedLine)
    }

    @Test
    fun `strike in final frame gives another two rolls`() {
        var line = lineFor("Fred", frameCount = 1)
        assertEquals("Fred| , , , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(10.pins)
        assertEquals("Fred|X, , ,10", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(5.pins)
        assertEquals("Fred|X,5, ,15", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(3.pins)
        assertEquals("Fred|X,5,3,18", line.scorecard())
        assertTrue(line is CompletedLine)
    }

    @Test
    fun `spare in final frame gives another roll`() {
        var line = lineFor("Fred", frameCount = 1)
        assertEquals("Fred| , , , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(9.pins)
        assertEquals("Fred|9, , ,9", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(1.pins)
        assertEquals("Fred|9,/, ,10", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(3.pins)
        assertEquals("Fred|9,/,3,13", line.scorecard())
        assertTrue(line is CompletedLine)
    }

    @Test
    fun `three frame game with three strikes`() {
        var line = lineFor("Fred", frameCount = 3)
        assertEquals("Fred| , , | , , | , , , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(10.pins)
        assertEquals("Fred| ,X,10| , , | , , , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(10.pins)
        assertEquals("Fred| ,X,20| ,X,30| , , , ", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(10.pins)
        assertEquals("Fred| ,X,30| ,X,50|X, , ,60", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(10.pins)
        assertEquals("Fred| ,X,30| ,X,60|X,X, ,80", line.scorecard())
        assertTrue(line is PlayableLine)

        line = line.roll(10.pins)
        assertEquals("Fred| ,X,30| ,X,60|X,X,X,90", line.scorecard())
        assertTrue(line is CompletedLine)
    }
}

val Int.pins: PinCount
    get() = PinCount(this) ?: error("Invalid pincount value of ${this}")