package dev.javaToKotlin

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LineTests {
    
    @Test
    fun `an empty line is not playable`() {
        val line = Line(
            player = "Fred",
            frames = emptyList()
        )
        
        assertFalse(line is PlayableLine)
    }
    
    @Test
    fun `a non empty line is playable`() {
        val line = Line(
            player = "Barney",
            frames = listOf(Frame())
        )
        
        assertTrue(line is PlayableLine)
    }
    
    @Test
    fun `can roll on a playable line`() {
        val line = Line(
            player = "Barney",
            frames = listOf(Frame())
        )
        
        assertTrue(line is PlayableLine)
        
        val lineTwo = line.roll(PinCount(3))
        assertTrue(lineTwo is PlayableLine)
        
        val lineThree = line.roll(PinCount(4))
        assertTrue(lineThree !is PlayableLine)
    }
}


