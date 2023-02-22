package example.bowling

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File

fun main(args: Array<String>) =
    controller(File(args[0]).bufferedReader(), File(args[1]).bufferedWriter())


sealed interface Game
object Starting : Game
data class GameInProgress(val perPlayer: List<Frame>): Game

sealed interface Frame
object UnplayedFrame : Frame

sealed interface Event
data class StartGame(val playerCount: Int) : Event
object PinsetterReady : Event


sealed interface Command
sealed interface PinsetterCommand : Command
object Reset : PinsetterCommand
object SetFull : PinsetterCommand
object SetPartial : PinsetterCommand
sealed interface ConsoleCommand : Command

data class CommandOutcome(
    val newState: Game,
    val events: List<Command>
)

fun controller(input: BufferedReader, output: BufferedWriter) {
    var game: Game = Starting
    
    while (true) {
        val inputLine = input.readLine() ?: break // end of stream
        val event = inputLine.toEvent() ?: continue // failed to parse, try next one
        
        val effect = game.eval(event)
        game = effect.newState
        effect.events
            .flatMap { command -> command.toLines() }
            .forEach { outputLine -> output.appendLine(outputLine) }
        output.flush()
    }
}

private fun Command.toLines(): List<String> = when (this) {
    Reset -> listOf("RESET")
    SetFull -> listOf("SET FULL")
    SetPartial -> listOf("SET PARTIAL")
}

private fun Game.eval(inputMessage: Event): CommandOutcome = when (inputMessage) {
    is StartGame ->
        CommandOutcome(newGame(inputMessage.playerCount), listOf(Reset))
    
    PinsetterReady -> CommandOutcome(this, listOf())
}

private fun newGame(playerCount: Int) =
    GameInProgress(perPlayer = (1..playerCount).map { UnplayedFrame })

private fun String.toEvent(): Event? {
    val type = substringBefore(' ')
    return when (type) {
        "START" -> StartGame(2)
        "READY" -> PinsetterReady
        else -> null.also {
            System.err.println("unrecognised event: $this")
        }
    }
}
