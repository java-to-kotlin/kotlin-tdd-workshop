package example.bowling

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File

fun main(args: Array<String>) =
    controller(File(args[0]).bufferedReader(), File(args[1]).bufferedWriter())


sealed interface Lane
object BetweenGames : Lane
data class ResettingPinsetter(val playerCount: Int): Lane
data class GameInProgress(val playerGames: List<Frame>): Lane


sealed interface Frame
object UnplayedFrame : Frame

sealed interface ControllerInput
data class StartGame(val playerCount: Int) : ControllerInput
object PinsetterReady : ControllerInput
data class Pinfall(val pinfall: Int) : ControllerInput


sealed interface PinsetterCommand
object Reset : PinsetterCommand
object SetFull : PinsetterCommand
object SetPartial : PinsetterCommand

data class ViewState(
    val playerScores : List<PlayerScores>,
    val nextPlayerToBowl: Int
)

data class PlayerScores(
    val frames : List<FrameScore>,
    val total: Int
)
data class FrameScore(
    val roll1: Int?,
    val roll2: Int?,
    val runningTotal: Int
)

data class CommandOutcome(
    val newState: Lane,
    val command: PinsetterCommand? = null
)

fun controller(input: BufferedReader, output: BufferedWriter) {
    var game: Lane = BetweenGames
    
    while (true) {
        val inputLine = input.readLine() ?: break // end of stream
        val event = inputLine.toControllerInput() ?: continue // failed to parse, try next one
        
        val effect = game.eval(event)
        game = effect.newState
        
        effect.command?.toLine()?.let(output::appendLine)
        game.toViewState()?.toLines()?.forEach(output::appendLine)
        output.flush()
    }
}

private fun ViewState.toLines(): List<String> =
    playerScores.map { "PLAYER 0" } + "NEXT $nextPlayerToBowl"

fun Frame.score() : PlayerScores =
    PlayerScores(frames = emptyList(), total = 0)

private fun Lane.toViewState(): ViewState? = when(this) {
    BetweenGames -> null
    is ResettingPinsetter -> null
    is GameInProgress -> ViewState(playerScores = playerGames.map { it.score() }, 0)
}

private fun PinsetterCommand.toLine(): String  = when(this) {
    Reset -> "RESET"
    SetFull -> "SET FULL"
    SetPartial -> "SET PARTIAL"
}


private fun Lane.eval(inputMessage: ControllerInput): CommandOutcome = when (inputMessage) {
    is StartGame ->
        CommandOutcome(ResettingPinsetter(inputMessage.playerCount), Reset)
    
    PinsetterReady -> when (this) {
        is ResettingPinsetter ->
            CommandOutcome(GameInProgress(playerGames = (1..playerCount).map { UnplayedFrame }))
        else -> CommandOutcome(this)
    }
    
    is Pinfall -> CommandOutcome(this)
}

internal fun String.toControllerInput(): ControllerInput? {
    val parts = split(' ')
    val type = parts.getOrNull(0)
    return when (type) {
        "START" -> StartGame(parts[1].toInt())
        "READY" -> PinsetterReady
        "PINFALL" -> Pinfall(parts[1].toInt())
        else -> null.also {
            System.err.println("unrecognised event: $this")
        }
    }
}
