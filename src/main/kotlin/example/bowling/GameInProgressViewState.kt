package example.bowling


sealed interface ViewState {
    val playerScores: List<PlayerScores>
}

data class GameInProgressViewState(
    override val playerScores: List<PlayerScores>,
    val nextPlayerToBowl: Int
) : ViewState

data class GameOverViewState(
    override val playerScores: List<PlayerScores>,
    val winners: List<Int>
) : ViewState


fun ViewState.toLines(): List<String> = scoreLines() + when(this) {
    is GameInProgressViewState -> "NEXT $nextPlayerToBowl"
    is GameOverViewState -> "WINNER ${winners.joinToString(" ")}"
}

private fun ViewState.scoreLines() = playerScores.map {
    "PLAYER ${(it.map { f -> f.toScoreLineElement() } + "${it.total}").joinToString(" ")}"
}

private fun FrameScore.toScoreLineElement(): String =
    "${roll1 ?: ""},${roll2 ?: ""},$runningTotal"
