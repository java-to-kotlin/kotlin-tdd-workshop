package bowling.multiplexer

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.io.PipedReader
import java.io.PipedWriter
import java.io.StringWriter
import java.util.concurrent.TimeUnit.SECONDS
import kotlin.test.assertTrue

class MultiplexerTest {
    private val consoleToMultiplexer = PipedWriter()
    private val multiplexerToConsole = PipedReader()
    private val pinsetterToMultiplexer = PipedWriter()
    private val multiplexerToPinsetter = PipedReader()
    private val controllerToMultiplexer = PipedWriter()
    private val multiplexerToController = PipedReader()
    
    private val group = ThreadGroup("testing")
    
    
    @BeforeEach
    fun setUp() {
        group.routeMessages(
            consoleToMultiplexer.reader().buffered(),
            multiplexerToConsole.writer().buffered(),
            pinsetterToMultiplexer.reader().buffered(),
            multiplexerToPinsetter.writer().buffered(),
            controllerToMultiplexer.reader().buffered(),
            multiplexerToController.writer().buffered()
        )
    }
    
    @AfterEach
    fun stopThreads() {
        group.interrupt()
    }
    
    @Test
    @Timeout(1, unit=SECONDS)
    fun `routing from console to controller`() {
        consoleToMultiplexer.writeLine("START 2")
        val received = multiplexerToController.readLine()
        assertTrue(received == "START 2")
    }
    
    @Test
    @Timeout(1, unit=SECONDS)
    fun `routing from controller to console`() {
        for (line in listOf("PLAYER 1,2,3 2,3,8 8", "NEXT 1", "WINNER 1 2")) {
            controllerToMultiplexer.writeLine(line)
            val received = multiplexerToConsole.readLine()
            assertTrue(received == line)
        }
    }
    
    @Test
    @Timeout(1, unit=SECONDS)
    fun `routing from pinsetter to controller`() {
        for (line in listOf("READY", "PINFALL 4")) {
            pinsetterToMultiplexer.writeLine(line)
            val received = multiplexerToController.readLine()
            assertTrue(received == line)
        }
    }
    
    @Test
    @Timeout(1, unit=SECONDS)
    fun `routing from controller to pinsetter`() {
        for (line in listOf("RESET", "SET PARTIAL", "SET FULL")) {
            controllerToMultiplexer.writeLine(line)
            val received = multiplexerToPinsetter.readLine()
            assertTrue(received == line)
        }
    }
}

private fun PipedReader.readLine(): String {
    val b = StringWriter()
    while(true) {
        val c = read()
        if (c == -1 || c == '\n'.code ) {
            return b.toString()
        } else {
            b.write(c)
        }
    }
}


private fun PipedWriter.reader() : PipedReader =
    PipedReader(this)

private fun PipedReader.writer() : PipedWriter =
    PipedWriter(this)