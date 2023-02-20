@file:JvmName("Multiplexer")

package bowling.multiplexer

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.InterruptedIOException
import java.io.Writer


fun ThreadGroup.process(from: () -> String?, to: (String) -> Unit): Thread {
    val thread = Thread(this) {
        try {
            generateSequence { from() }
                .onEach { System.err.println("multiplexer routing: $it") }
                .forEach { line ->
                    to(line)
                }
        } catch (e: InterruptedIOException) {
            // end thread
        }
    }
    thread.start()
    return thread
}

fun route(vararg rs: Pair<Iterable<String>, (String) -> Unit>): (String) -> Unit {
    val routes = rs
        .flatMap { route -> route.first.map { s -> s to route.second } }
        .toMap()
    
    return fun(line: String) {
        routes[line.substringBefore(" ")]?.invoke(line)
            ?: System.err.println("unrecognised first token, cannot route: $line")
        
    }
}


fun main(args: Array<String>) {
    if (args.size != 6) {
        error("usage error")
    }
    
    // Open output streams first, then input streams to avoid deadlock.
    // All other components open input streams first and then output.
    
    val consoleWriter = File(args[1]).bufferedWriter()
    val pinsetterWriter = File(args[3]).bufferedWriter()
    val controllerWriter = File(args[5]).bufferedWriter()
    
    System.err.println("multiplexer opened output streams")
    
    val consoleReader = File(args[0]).bufferedReader()
    val pinsetterReader = File(args[2]).bufferedReader()
    val controllerReader = File(args[4]).bufferedReader()
    
    System.err.println("multiplexer opened input streams")
    
    ThreadGroup("pumps").routeMessages(
        consoleReader,
        consoleWriter,
        pinsetterReader,
        pinsetterWriter,
        controllerReader,
        controllerWriter
    )
}

fun ThreadGroup.routeMessages(
    consoleReader: BufferedReader,
    consoleWriter: BufferedWriter,
    pinsetterReader: BufferedReader,
    pinsetterWriter: BufferedWriter,
    controllerReader: BufferedReader,
    controllerWriter: BufferedWriter
) {
    val controllerWriteLock = Any()
    fun writeToController(s: String) {
        synchronized(controllerWriteLock) {
            controllerWriter.writeLine(s)
        }
    }
    
    process(from = consoleReader::readLine, to = ::writeToController)
    process(from = pinsetterReader::readLine, to = ::writeToController)
    process(
        from = controllerReader::readLine,
        to = route(
            setOf("RESET", "SET") to pinsetterWriter::writeLine,
            setOf("PLAYER", "NEXT", "WINNER") to consoleWriter::writeLine
        )
    )
}

fun Writer.writeLine(line: String) {
    write(line)
    write("\n")
    flush()
}
