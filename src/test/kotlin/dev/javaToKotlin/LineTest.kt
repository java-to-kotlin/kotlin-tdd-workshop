package dev.javaToKotlin

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class LineTest {
    @Test
    fun `2 rolls completes a single frame line`() {
        var line: Line = newLine(frameCount = 1)
        assertTrue(line is PlayableLine)

        line = line.roll(Pinfall(1))
        assertTrue(line is PlayableLine)

        line = line.roll(Pinfall(1))
        assertTrue(line is CompleteLine)
    }

    @Test
    fun `more frames`() {
        var line: Line = newLine(frameCount = 2)
        assertTrue(line is PlayableLine)

        line = line.roll(Pinfall(1))
        assertTrue(line is PlayableLine)

        line = line.roll(Pinfall(1))
        assertTrue(line is PlayableLine)

        line = line.roll(Pinfall(1))
        assertTrue(line is PlayableLine)

        line = line.roll(Pinfall(1))
        assertTrue(line is CompleteLine)
    }

}




