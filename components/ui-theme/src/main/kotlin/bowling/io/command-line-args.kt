package bowling.io

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File

fun openStreams(
    args: Array<String>
): Pair<BufferedReader, BufferedWriter> {
    return when (args.size) {
        0 -> {
            val input = System.`in`.bufferedReader()
            val ouput = System.out.bufferedWriter()
            Pair(input, ouput)
        }
        
        1 -> {
            val deviceFile = File(args[0])
            val input = deviceFile.bufferedReader()
            val output = deviceFile.bufferedWriter()
            Pair(input, output)
        }
        
        2 -> {
            val input = File(args[0]).bufferedReader()
            val output = File(args[1]).bufferedWriter()
            Pair(input, output)
        }
        
        else -> {
            error("usage error")
        }
    }
}
