package dev.javaToKotlin

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class LineTest {
    @Test
    fun `test`() {
        var line: Line = newLine(frameCount = 2)
        assertTrue(line is PlaybleLine)

        line = line.roll(Pinfall(1))
        assertTrue(line is PlaybleLine)
    }
}

private fun PlaybleLine.roll(pinfall: Pinfall): Line {
    return this
}

private fun newLine(frameCount: Int): PlaybleLine {
    return PlaybleLine()
}

interface Line

class PlaybleLine : Line {

}
