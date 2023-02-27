package example.bowling

data class ViewState(
    val playerScores: List<PlayerScores>,
    val nextPlayerToBowl: Int
)


internal fun ViewState.toLines(): List<String> =
    playerScores.map {
        "PLAYER ${(it.map { f -> f.toScoreLineElement() } + "${it.total}").joinToString(" ")}"
    } + "NEXT $nextPlayerToBowl"

private fun FrameScore.toScoreLineElement(): String =
    "${roll1 ?: ""},${roll2 ?: ""},$runningTotal"
