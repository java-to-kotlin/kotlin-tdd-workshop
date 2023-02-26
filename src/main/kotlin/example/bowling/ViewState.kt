package example.bowling

data class ViewState(
    val playerScores: List<PlayerScores>,
    val nextPlayerToBowl: Int
)


internal fun ViewState.toLines(): List<String> =
    playerScores.map { "PLAYER ${it.total}" } + "NEXT $nextPlayerToBowl"
