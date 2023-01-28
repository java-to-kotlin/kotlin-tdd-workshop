@file:JvmName("Multiplexer")

package bowling.multiplexer

import java.io.File
import java.io.Writer
import java.util.concurrent.SynchronousQueue


fun process(from: () -> String?, to: (String) -> Unit) =
    Thread {
        generateSequence { if (Thread.interrupted()) null else from() }
            .forEach(to)
    }.apply { start() }

fun route(vararg rs: Pair<Iterable<String>, (String) -> Unit>): (String) -> Unit {
    val routes = rs.flatMap { route -> route.first.map { s -> s to route.second } }.toMap()
    return fun(line: String) {
        routes[line.substringBefore(" ")]?.invoke(line)
    }
}


fun main(args: Array<String>) {
    val consoleIn: File
    val consoleOut: File
    val pinsetterIn: File
    val pinsetterOut: File
    val controllerIn: File
    val controllerOut: File
    
    when (args.size) {
        0 -> {
            val stdin = File("/dev/stdin")
            val stdout = File("/dev/stdout")
            
            consoleIn = stdin
            consoleOut = stdout
            pinsetterIn = stdin
            pinsetterOut = stdout
            controllerIn = stdin
            controllerOut = stdout
        }
        
        3 -> {
            val consoleDevice = File(args[0])
            val pinsetterDevice = File(args[1])
            val controllerDevice = File(args[2])
            consoleIn = consoleDevice
            consoleOut = consoleDevice
            pinsetterIn = pinsetterDevice
            pinsetterOut = pinsetterDevice
            controllerIn = controllerDevice
            controllerOut = controllerDevice
        }
        
        6 -> {
            consoleIn = File(args[0])
            consoleOut = File(args[1])
            pinsetterIn = File(args[2])
            pinsetterOut = File(args[3])
            controllerIn = File(args[4])
            controllerOut = File(args[5])
        }
        
        else -> {
            error("usage error message TBD")
        }
    }
    
    run(
        consoleIn = consoleIn,
        consoleOut = consoleOut,
        pinsetterIn = pinsetterIn,
        pinsetterOut = pinsetterOut,
        controllerIn = controllerIn,
        controllerOut = controllerOut
    )
}

internal fun run(
    consoleIn: File,
    consoleOut: File,
    pinsetterIn: File,
    pinsetterOut: File,
    controllerIn: File,
    controllerOut: File
) {
    val controllerEvents = SynchronousQueue<String>()
    
    val controllerEventsWriter = controllerOut.bufferedWriter()
    val pinsetterCommandWriter = pinsetterOut.bufferedWriter()
    val viewEventsWriter = consoleOut.bufferedWriter()
    
    process(from = consoleIn.bufferedReader()::readLine, to = controllerEvents::put)
    process(from = pinsetterIn.bufferedReader()::readLine, to = controllerEvents::put)
    process(from = controllerEvents::take, to = controllerEventsWriter::writeLine)
    process(
        from = controllerIn.bufferedReader()::readLine,
        to = route(
            setOf("SET") to pinsetterCommandWriter::writeLine,
            setOf("PLAYER", "NEXT", "WINNER") to viewEventsWriter::writeLine
        )
    )
}

fun Writer.writeLine(line: String) {
    write(line)
    write("\n")
    flush()
}