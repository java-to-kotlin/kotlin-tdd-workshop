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
    fun `should render partial and open frames`() {
        var line = newLine(2)
        line = line.roll(Pinfall(3)) as PlayableLine
        assertEquals("3, , | , , ", line.render())
        line = line.roll(Pinfall(2)) as PlayableLine
        assertEquals("3,2, | , , ", line.render())
    }

    @Test
    fun `should render strike`() {
        var line = newLine(2)
        line = line.roll(Pinfall(10)) as PlayableLine
        assertEquals(" ,X, | , , ", line.render())
    }

    @Test
    fun `should render misses`() {
        var line: Line = newLine(2)
        line = (line as PlayableLine).roll(Pinfall(0))
        assertEquals("-, , | , , ", line.render())

        line = (line as PlayableLine).roll(Pinfall(0))
        assertEquals("-,-, | , , ", line.render())

        line = (line as PlayableLine).roll(Pinfall(2))
        assertEquals("-,-, |2, , ", line.render())

        line = (line as PlayableLine).roll(Pinfall(0))
        assertEquals("-,-, |2,-, ", line.render())
    }
}
