@file:JvmName("Controller")

package example.bowling

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File

fun main(args: Array<String>) =
    controller(File(args[0]).bufferedReader(), File(args[1]).bufferedWriter())

fun controller(input: BufferedReader, output: BufferedWriter) {
    var game: LaneState = Initialising
    
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
object Initialising : LaneState
data class ResettingPinsetter(val playerCount: Int) : LaneState
data class GameInProgress(val playerGames: List<Frame>) : LaneState


private fun LaneState.toViewState(): ViewState? = when (this) {
    Initialising -> null
    is ResettingPinsetter -> null
    is GameInProgress -> toViewState()
}

private fun GameInProgress.toViewState(): ViewState {
    val playerScores = scores()
    
    return if (gameIsOver()) {
        GameOverViewState(
            playerScores = playerScores,
            winners = playerScores.winners()
        )
    } else {
        GameInProgressViewState(
            playerScores = playerScores,
            nextPlayerToBowl = nextPlayerToBowl()
        )
    }
}

internal fun GameInProgress.gameIsOver() =
    playerGames.all { it is GameOver }

private fun List<PlayerScores>.winners(): List<Int> {
    val maxScore = maxOf { it.total }
    return indices.filter { get(it).total == maxScore }
}


private fun GameInProgress.scores() = playerGames.map { it.score() }


data class Step(
    val newState: LaneState,
    val command: PinsetterCommand? = null
)


internal fun LaneState.eval(inputMessage: ControllerInput): Step = when (inputMessage) {
    is StartGame ->
        Step(newState = ResettingPinsetter(playerCount = inputMessage.playerCount), command = Reset)
    
    PinsetterReady -> when (this) {
        is ResettingPinsetter ->
            Step(newState = GameInProgress(playerGames = (1..playerCount).map { StartOfGame }))
        
        else -> ignoreInput()
    }
    
    is Pinfall ->
        when (this) {
            Initialising -> ignoreInput()
            is ResettingPinsetter -> ignoreInput()
            is GameInProgress -> {
                val player = nextPlayerToBowl()
                val newState = roll(inputMessage.pinfall)
                Step(
                    newState = newState,
                    command = when {
                        newState.gameIsOver() -> null
                        newState.playerGames[player].needsAllPinsSet() -> SetFull
                        else -> SetPartial
                    }
                )
            }
        }
}

fun Frame.needsAllPinsSet() = when (this) {
    is CompleteFrame, is GameOver -> true
    is IncompleteFinalSpare -> true
    is IncompleteFinalStrike -> true
    is PenultimateBonusRollForFinalStrike -> bonusRoll1 == 10
    else -> false
}


fun LaneState.ignoreInput() =
    Step(this)


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

