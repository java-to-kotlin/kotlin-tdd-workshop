package dev.javaToKotlin

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LineRenderingTest {

    @Test
    fun `should render a line`() {
        var line: Line = newLine(frameCount = 2)
        assertEquals(" , , | , , ", line.render())
    }
}
