package example.bowling

import kotlin.random.Random
import kotlin.test.Test

class GenerateExampleTest {
    @Test
    fun `generate example`() {
        val game = Random.nextGame(2, 7)
        val viewState = game.toViewState()
        
        viewState.toLines().forEach(::println)
        
        println()
        
        viewState.toTextScorecard().forEach(::println)
    }
}

fun ViewState.toTextScorecard(): List<String> =
    listOf(dividerLine) +
        this.playerScores.flatMapIndexed { i, s: List<FrameScore> ->
            s.toTextScoreboardLines(playerNames[i]) + dividerLine
        }

private val playerNames = listOf("Alice", "Bob", "Carol", "Dave", "Eve")
private val maxPlayerNameLen = playerNames.maxOf { it.length }

private fun List<FrameScore>.toTextScoreboardLines(name: String): List<String> =
    listOf(this.frameScoreLine(name), this.runningTotalLine())

private fun List<FrameScore>.frameScoreLine(name: String): String =
    "| " + name.padEnd(maxPlayerNameLen + 1, ' ') +
        joinToString(
            separator = " | ", prefix = "| ", postfix = " |",
            transform = FrameScore::toScoreDisplay
        ) +
        emptyCells(10-size)
        

private fun emptyCells(n: Int) =
    "     |".repeat(n)

private val dividerLine =
    "+" + "-".repeat(maxPlayerNameLen + 2) + "+" + "-----+".repeat(10)

private fun FrameScore.toScoreDisplay(): String {
    val r1 = roll1
    val r2 = roll2
    
    return if (r1 == 10) {
        "  X"
    } else if (r1 != null && r2 != null && r1 + r2 == 10) {
        "${r1.toRollDisplay()} /"
    } else {
        "${roll1.toRollDisplay()} ${roll2.toRollDisplay()}"
    }
}

private fun List<FrameScore>.runningTotalLine(): String =
    "| " + total.toString().padStart(maxPlayerNameLen, ' ') + " " +
        joinToString(separator = " | ", prefix = "| ", postfix = " |") {
            it.runningTotal.toString().padCentered(3)
        } +
        emptyCells(10-size)

private fun String.padCentered(toLen: Int): String {
    val space = (toLen - length) / 2
    return " ".repeat(space + (length + 1) % 2) + this + " ".repeat(space)
}

private fun Int?.toRollDisplay(): String =
    when (this) {
        null -> " "
        0 -> "-"
        else -> this.toString()
    }


fun Random.nextGame(playerCount: Int, maxRolls: Int = Int.MAX_VALUE): GameInProgress {
    tailrec fun generateGame(game: GameInProgress, currentPlayer: Int, rollsLeft: Int): GameInProgress {
        if (game.gameIsOver() || rollsLeft == 0) {
            return game
        } else {
            val roll1 = nextInt(11)
            val afterRoll = game.roll(roll1)
            val nextTurn =
                if (afterRoll.gameIsOver() || roll1 == 10) afterRoll else afterRoll.roll(nextInt(11 - roll1))
            
            val nextPlayer = (currentPlayer + 1) % playerCount
            
            return generateGame(
                game = nextTurn,
                currentPlayer = nextPlayer,
                rollsLeft = if (nextPlayer == 0) rollsLeft - 1 else rollsLeft
            )
        }
    }
    
    return generateGame(newGame(playerCount), 0, maxRolls)
}
