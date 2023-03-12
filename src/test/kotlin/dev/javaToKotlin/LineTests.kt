package dev.javaToKotlin

import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LineTests {
    @Test
    fun `an empty line is not playable`() {
        val line = Line("Fred", NonNegativeInt(0))
        assertFalse(line is PlayableLine)
    }

    @Test
    fun `a new line with one frame is playable`() {
        val line = Line("Fred", NonNegativeInt(1))
        assertTrue(line is PlayableLine)
    }
}