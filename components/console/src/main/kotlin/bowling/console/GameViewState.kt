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
                            .map { it.toFrameScore() ?: return null },
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

internal fun String.toFrameScore(): FrameScore? {
    val parts = split(",")
    if (parts.size != 3) return null
    
    val runningTotal = parts[2].toIntOrNull()
    
    when {
        parts[1] == "/" -> {
            val firstRoll = parts[0].toIntOrNull()
            val secondRoll = if (firstRoll == null) null else pinCount - firstRoll
            
            return FrameScore(
                firstRoll = firstRoll,
                secondRoll = secondRoll,
                runningTotal = runningTotal
            )
        }
        
        parts[0] == "X" && parts[1] == "" || parts[0] == "" && parts[1] == "X" -> {
            return FrameScore(
                firstRoll = 10,
                secondRoll = null,
                runningTotal = runningTotal
            )
        }
        
        else -> {
            return FrameScore(
                firstRoll = parts[0].toIntOrNull(),
                secondRoll = parts[1].toIntOrNull(),
                runningTotal = runningTotal
            )
        }
    }
}
