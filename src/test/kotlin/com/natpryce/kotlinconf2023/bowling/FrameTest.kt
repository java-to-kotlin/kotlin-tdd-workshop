package com.natpryce.kotlinconf2023.bowling

import io.kotest.core.spec.style.AnnotationSpec
import kotlinx.collections.immutable.persistentListOf
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FrameTest : AnnotationSpec() {
    @Test
    fun `is spare`() {
        assertFalse(Frame(persistentListOf()).isSpare())
        
        assertFalse(Frame(persistentListOf(1)).isSpare())
        assertFalse(Frame(persistentListOf(10)).isSpare())
        
        assertTrue(Frame(persistentListOf(1,9)).isSpare())
        assertTrue(Frame(persistentListOf(0,10)).isSpare())
        
        // final frame with bonus ball
        assertTrue(Frame(persistentListOf(3,7,4)).isSpare())
    }
    
    @Test
    fun `is strike`() {
        assertFalse(Frame(persistentListOf()).isStrike())
        
        assertFalse(Frame(persistentListOf(1)).isStrike())
        
        assertTrue(Frame(persistentListOf(10)).isStrike())
        
        assertFalse(Frame(persistentListOf(1,9)).isStrike())
        assertFalse(Frame(persistentListOf(0,10)).isStrike())
        
        // final frame with bonus balls
        assertFalse(Frame(persistentListOf(10,2,4)).isSpare())
    }
}