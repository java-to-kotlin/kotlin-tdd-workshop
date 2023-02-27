package example.bowling.ftest

import example.bowling.controller
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.Closeable
import java.io.IOException
import java.io.InterruptedIOException
import java.io.PipedReader
import java.io.PipedWriter
import java.util.concurrent.TimeUnit.SECONDS
import kotlin.concurrent.thread
import kotlin.test.assertTrue


class ControllerController(
    private val thread: Thread,
    val toController: BufferedWriter,
    val fromController: BufferedReader
) : Closeable {
    override fun close() {
        try {
            toController.close()
        } catch (e: IOException) {
            // ignore it
        }
        try {
            fromController.close()
        } catch (e: IOException) {
            // ignore it
        }
    }
    
    fun interrupt() {
        thread.interrupt()
    }
}

fun runController(): ControllerController {
    val controllerInput = PipedReader()
    val controllerOutput = PipedWriter()
    
    return ControllerController(
        thread = thread(name = "bowling controller thread", start = true) {
            try {
                controller(controllerInput.buffered(), controllerOutput.buffered())
            } catch (e: InterruptedIOException) {
                // Ok
            }
        },
        toController = PipedWriter(controllerInput).buffered(),
        fromController = PipedReader(controllerOutput).buffered()
    )
}

infix fun ControllerController.`⟵`(data: String): ControllerController = apply {
    toController.appendLine(data)
    toController.flush()
}

infix fun ControllerController.`⟶`(expectedData: String): ControllerController = apply {
    assertTrue(fromController.readLine() == expectedData)
}


class FunctionalTest {
    val theController: ControllerController = runController()
    
    @Test
    @Timeout(1, unit = SECONDS)
    fun `start of game, all players play the first frame`() {
        theController `⟵` "START 2"
        theController `⟶` "RESET"
        theController `⟵` "READY"
        theController `⟶` "PLAYER 0"
        theController `⟶` "PLAYER 0"
        theController `⟶` "NEXT 0"
        theController `⟵` "PINFALL 2"
        theController `⟶` "SET PARTIAL"
        theController `⟶` "PLAYER 2,,2 2"
        theController `⟶` "PLAYER 0"
        theController `⟶` "NEXT 0"
        theController `⟵` "PINFALL 5"
        theController `⟶` "SET FULL"
        theController `⟶` "PLAYER 2,5,7 7"
        theController `⟶` "PLAYER 0"
        theController `⟶` "NEXT 1"
        theController `⟵` "PINFALL 10"
        theController `⟶` "SET FULL"
        theController `⟶` "PLAYER 2,5,7 7"
        theController `⟶` "PLAYER 10,,10 10"
        theController `⟶` "NEXT 0"
    }
    
    @AfterEach
    fun stopIt() {
        theController.interrupt()
        theController.close()
    }
}
