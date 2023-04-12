package dev.javaToKotlin

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LineRenderingTest {

    @Test
    fun `should render a new line`() {
        var line: Line = newLine(frameCount = 2)
        assertEquals(" , , | , , ", line.render())
    }

    @Test
    fun `should render frames`() {
        var line = newLine(2)
        line = line.roll(Pinfall(3)) as PlayableLine
        assertEquals("3, , | , , ", line.render())
    }
}
