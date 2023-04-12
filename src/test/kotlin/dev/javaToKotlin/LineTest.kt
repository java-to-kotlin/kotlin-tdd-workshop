package dev.javaToKotlin

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class LineTest {
    @Test
    fun `test`() {
        var line: Line = newLine(frameCount = 1)
        assertTrue(line is PlayableLine)

        line = line.roll(Pinfall(1))
        assertTrue(line is PlayableLine)

        line = line.roll(Pinfall(1))
        assertTrue(line is CompleteLine)
    }
}




