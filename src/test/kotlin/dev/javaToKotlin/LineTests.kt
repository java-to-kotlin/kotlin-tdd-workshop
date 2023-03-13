package dev.javaToKotlin

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class LineTests {
    
    @Test
    fun `an empty line is not playable`() {
        val line = Line(
            player = "Fred",
            frames = emptyList()
        )
        
        assertFalse(line is PlayableLine)
    }
}


