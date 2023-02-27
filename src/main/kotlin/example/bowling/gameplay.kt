package example.bowling

sealed interface Frame
sealed interface PrecedingFrame : Frame
sealed interface CompleteFrame : PrecedingFrame
object StartOfGame : PrecedingFrame
data class PartialFrame(val roll1: Int) : Frame
data class OpenFrame(val roll1: Int, val roll2: Int) : CompleteFrame
data class Spare(val roll1: Int) : CompleteFrame
object Strike : CompleteFrame


fun Frame.roll(pinfall: Int): Frame {
    return when (this) {
        is PrecedingFrame -> when (pinfall) {
            10 -> Strike
            else -> PartialFrame(pinfall)
        }
        
        is PartialFrame -> when (pinfall) {
            10 - roll1 -> Spare(roll1)
            else -> OpenFrame(roll1, pinfall)
        }
    }
}

fun GameInProgress.nextPlayerToBowl(): Int =
    playerGames.indexOfFirst { it !is CompleteFrame }.takeUnless { it < 0 } ?: 0

fun GameInProgress.roll(pinfall: Int) =
    copy(playerGames = playerGames.mapAtIndex(nextPlayerToBowl()) { it.roll(pinfall) })

inline fun <E> List<E>.mapAtIndex(i: Int, f: (E)->E): List<E> =
    set(i, f(get(i)))

fun <E> List<E>.set(i: Int, e: E): List<E> =
    toMutableList().apply { set(i, e) }
