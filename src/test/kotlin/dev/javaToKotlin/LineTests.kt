package dev.javaToKotlin

import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LineTests {
    
    @Test
    fun `an empty line is not playable`() {
        val line = Line(
            player = "Fred",
            noOfFrames = 0
        )
        
        assertFalse(line is PlayableLine)
    }
    
    @Test
    fun `can roll on a playable line`() {
        var line = Line(
            player = "Barney",
            noOfFrames = 1
        )
        
        assertTrue(line is PlayableLine)
        
        line = line.roll(PinCount(3))
        assertTrue(line is PlayableLine)
        
        line = line.roll(PinCount(4))
        assertTrue(line !is PlayableLine)
    }
    
    @Test
    fun `two frame line`() {
        var line = Line(
            player = "Barney",
            noOfFrames = 2
        )
        
        assertTrue(line is PlayableLine)
        
        line = line.roll(PinCount(1))
        assertTrue(line is PlayableLine)
        
        line = line.roll(PinCount(2))
        assertTrue(line is PlayableLine)
        
        line = line.roll(PinCount(3))
        assertTrue(line is PlayableLine)
        
        line = line.roll(PinCount(4))
        assertTrue(line !is PlayableLine)
    }
    
    @Test
    fun `rolling playable frames`() {
        val frame: UnplayedFrame = UnplayedFrame()
        
        val frame2 = frame.roll(PinCount(3))
        assertTrue(frame2 is InProgressFrame)
        
        val frame3 = frame2.roll(PinCount(4))
        assertTrue(frame3 is OpenFrame)
    }
    
    @Test
    fun `rolling a strike`() {
        val frame: UnplayedFrame = UnplayedFrame()
        
        val frame2 = frame.roll(PinCount(10))
        assertTrue(frame2 !is PlayableFrame)
        assertTrue(frame2 is Strike)
        
    }
    
    
}


