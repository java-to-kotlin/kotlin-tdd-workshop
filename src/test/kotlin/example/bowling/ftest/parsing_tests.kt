package example.bowling.ftest

import example.bowling.Pinfall
import example.bowling.PinsetterReady
import example.bowling.StartGame
import example.bowling.toControllerInput
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.property.assume
import kotlin.test.assertTrue

class StringToControllerInputTest : AnnotationSpec() {
    companion object {
        val examples = listOf(
            "START 2" to StartGame(playerCount = 2),
            "START 3" to StartGame(playerCount = 3),
            "READY" to PinsetterReady,
            "PINFALL 0" to Pinfall(0),
            "PINFALL 1" to Pinfall(1),
            "PINFALL 4" to Pinfall(4),
            "PINFALL 9" to Pinfall(9),
            "PINFALL 10" to Pinfall(10)
        )
    }
    
    
    @Test
    fun `valid values`() {
        examples.forEach { (line, expected )->
            assertTrue(line.toControllerInput() == expected)
        }
    }
    
    @Test
    @Ignore
    fun `TODO does not throw on invalid input`() {
        assume(false)
    }
}