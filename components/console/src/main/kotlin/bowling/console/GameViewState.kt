package bowling.console

import java.io.BufferedReader
import java.io.IOException


sealed interface GameViewState {
    val scoresPerPlayer: List<PlayerScores>
}

data class GameInProgressState(
    override val scoresPerPlayer: List<PlayerScores>,
    val nextPlayerToBowl: Int
) : GameViewState

data class GameOverState(
    override val scoresPerPlayer: List<PlayerScores>,
    val winningPlayers: List<Int>
) : GameViewState

data class PlayerScores(
    val frames: List<FrameScore>,
    val total: Int
)

val noScore = PlayerScores(frames = emptyList(), total = 0)

data class FrameScore(
    val firstRoll: Int?,
    val secondRoll: Int?,
    val runningTotal: Int?
)

val unplayedFrame = FrameScore(null, null, null)


fun BufferedReader.readLineParts(): List<String> =
    (readLine() ?: throw IOException("unexpected end of stream"))
        .split(" ")

fun BufferedReader.readViewState(): GameViewState? {
    val playerScores = mutableListOf<PlayerScores>()
    while (true) {
        val parts = readLineParts()
        if (parts.isEmpty()) return null
        
        when (parts.first()) {
            "PLAYER" -> {
                playerScores.add(
                    PlayerScores(
                        frames = parts
                            .drop(1)
                            .dropLast(1)
                            .map { it.toFrameScore() },
                        total = parts.lastOrNull()?.toIntOrNull() ?: return null
                    )
                )
            }
            
            "NEXT" -> return GameInProgressState(
                scoresPerPlayer = playerScores,
                nextPlayerToBowl = parts
                    .getOrNull(1)
                    ?.toIntOrNull()
                    ?: return null
            )
            
            "WINNER" -> return GameOverState(
                scoresPerPlayer = playerScores,
                winningPlayers = parts
                    .drop(1)
                    .map { it.toIntOrNull() ?: return null }
                    .takeIf { it.isNotEmpty() }
                    ?: return null
            )
        }
    }
}

private fun String.toFrameScore(): FrameScore {
    val scoreParts = split(",").map { it.toIntOrNull() }
    return FrameScore(
        firstRoll = scoreParts.getOrNull(0),
        secondRoll = scoreParts.getOrNull(1),
        runningTotal = scoreParts.getOrNull(2)
    )
}
