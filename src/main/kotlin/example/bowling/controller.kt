package example.bowling

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File

fun main(args: Array<String>) =
    controller(File(args[0]).bufferedReader(), File(args[1]).bufferedWriter())

fun controller(input: BufferedReader, output: BufferedWriter) {
    var game: LaneState = BetweenGames
    
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


sealed interface LaneState
object BetweenGames : LaneState
data class ResettingPinsetter(val playerCount: Int) : LaneState
data class GameInProgress(val playerGames: List<Frame>) : LaneState



private fun LaneState.toViewState(): ViewState? = when (this) {
    BetweenGames -> null
    is ResettingPinsetter -> null
    is GameInProgress -> toViewState()
}

private fun GameInProgress.toViewState() =
    ViewState(
        playerScores = playerGames.map { it.score() },
        nextPlayerToBowl = nextPlayerToBowl()
    )


data class Step(
    val newState: LaneState,
    val command: PinsetterCommand? = null
)


private fun LaneState.eval(inputMessage: ControllerInput): Step = when (inputMessage) {
    is StartGame ->
        Step(newState = ResettingPinsetter(playerCount = inputMessage.playerCount), command = Reset)
    
    PinsetterReady -> when (this) {
        is ResettingPinsetter ->
            Step(newState = GameInProgress(playerGames = (1..playerCount).map { StartOfGame }))
        
        else -> ignoreInput()
    }
    
    is Pinfall ->
        when (this) {
            BetweenGames -> ignoreInput()
            is ResettingPinsetter -> ignoreInput()
            is GameInProgress ->
                Step(
                    newState = copy(playerGames = playerGames.set(0, playerGames.get(0).roll(inputMessage.pinfall))),
                    command = SetPartial
                )
        }
}


fun LaneState.ignoreInput() =
    Step(this)


private fun <E> List<E>.set(i: Int, e: E): List<E> =
    toMutableList().apply { set(i, e) }


sealed interface ControllerInput
data class StartGame(val playerCount: Int) : ControllerInput
object PinsetterReady : ControllerInput
data class Pinfall(val pinfall: Int) : ControllerInput

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

