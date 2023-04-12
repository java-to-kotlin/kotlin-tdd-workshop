package dev.javaToKotlin

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
    fun `four rolls complete the 2 frame line`() {
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

    @Test
    fun `strike in two rolls should be detected`() {
        var line: Line = newLine(frameCount = 2)
        assertTrue(line is PlayableLine)

        line = line.roll(Pinfall(10))
        assertTrue(line is PlayableLine)

        line = line.roll(Pinfall(1))
        assertTrue(line is PlayableLine)

        line = line.roll(Pinfall(1))
        assertTrue(line is CompleteLine)
    }

    @Test
    fun `open frame then strike`() {
        var line: Line = newLine(frameCount = 2)
        assertTrue(line is PlayableLine)

        line = line.roll(Pinfall(1))
        assertTrue(line is PlayableLine)

        line = line.roll(Pinfall(1))
        assertTrue(line is PlayableLine)

        line = line.roll(Pinfall(10))
        assertTrue(line is CompleteLine)
    }

}




