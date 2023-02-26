package example.bowling

data class PlayerScores(
    val frames: List<FrameScore>
)

val PlayerScores.total: Int get() =
    frames.lastOrNull()?.runningTotal ?: 0

data class FrameScore(
    val roll1: Int?,
    val roll2: Int?,
    val runningTotal: Int
)

fun Frame.score(): PlayerScores =
    PlayerScores(frames = emptyList())
