package example.bowling.ftest

import example.bowling.Pinfall
import example.bowling.PinsetterReady
import example.bowling.StartGame
import example.bowling.toControllerInput
import io.kotest.core.spec.style.AnnotationSpec
import kotlin.test.assertTrue

class StringToControllerInputTest : AnnotationSpec() {
    @Test
    fun `valid values`() {
        assertTrue("START 2".toControllerInput() == StartGame(playerCount = 2))
        assertTrue("START 3".toControllerInput() == StartGame(playerCount = 3))
        assertTrue("READY".toControllerInput() == PinsetterReady)
        assertTrue("PINFALL 0".toControllerInput() == Pinfall(0))
        assertTrue("PINFALL 1".toControllerInput() == Pinfall(1))
        assertTrue("PINFALL 4".toControllerInput() == Pinfall(4))
        assertTrue("PINFALL 9".toControllerInput() == Pinfall(9))
        assertTrue("PINFALL 10".toControllerInput() == Pinfall(10))
    }
}