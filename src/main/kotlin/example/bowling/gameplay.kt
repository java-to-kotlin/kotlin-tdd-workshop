package example.bowling

sealed interface Frame
sealed interface PrecedingFrame : Frame {
    val framesRemaining: UInt
}

sealed interface FollowingFrame : Frame {
    val prev: PrecedingFrame
}

sealed interface CompleteFrame : PrecedingFrame, FollowingFrame
sealed interface GameOver : FollowingFrame

object StartOfGame : PrecedingFrame {
    override val framesRemaining: UInt = 10u
    
    override fun toString(): String = this::class.simpleName ?: "<UNKNOWN>"
}

data class PartialFrame(override val prev: PrecedingFrame, val roll1: Int) : FollowingFrame
data class OpenFrame(override val prev: PrecedingFrame, val roll1: Int, val roll2: Int) : CompleteFrame {
    override val framesRemaining: UInt = prev.framesRemaining - 1u
}

data class Spare(override val prev: PrecedingFrame, val roll1: Int) : CompleteFrame {
    val roll2 get() = 10 - roll1
    
    override val framesRemaining: UInt = prev.framesRemaining - 1u
}

data class Strike(override val prev: PrecedingFrame) : CompleteFrame {
    override val framesRemaining: UInt = prev.framesRemaining - 1u
}

data class FinalOpenFrame(override val prev: PrecedingFrame, val roll1: Int, val roll2: Int) : GameOver

data class IncompleteFinalSpare(override val prev: PrecedingFrame, val roll1: Int) : FollowingFrame {
    val roll2 get() = 10 - roll1
}

data class FinalBonusRoll(override val prev: PrecedingFrame, val bonusRoll: Int) : GameOver

fun Frame.roll(pinfall: Int): Frame {
    return when (this) {
        is GameOver -> this
        
        is PrecedingFrame -> when (pinfall) {
            10 -> Strike(this)
            else -> PartialFrame(this, pinfall)
        }
        
        is PartialFrame -> when {
            isLastFrame -> when (pinfall) {
                10 - roll1 -> IncompleteFinalSpare(prev, roll1)
                else -> FinalOpenFrame(prev, roll1, pinfall)
            }
            else -> when (pinfall) {
                10 - roll1 -> Spare(prev, roll1)
                else -> OpenFrame(prev, roll1, pinfall)
            }
        }
        
        is IncompleteFinalSpare ->
            FinalBonusRoll(prev, pinfall)
            
    }
}

val PartialFrame.isLastFrame: Boolean
    get() =
        prev.framesRemaining == 1u

private tailrec fun Frame.count(acc: Int = 0): Int =
    when (this) {
        is FollowingFrame -> prev.count(acc + 1)
        else -> acc
    }

fun GameInProgress.nextPlayerToBowl(): Int {
    val frameCount = playerGames.first().count()
    return playerGames
        .indexOfFirst { it !is CompleteFrame || it.count() < frameCount }
        .takeUnless { it < 0 }
        ?: 0
}

fun GameInProgress.roll(pinfall: Int) =
    copy(playerGames = playerGames.mapAtIndex(nextPlayerToBowl()) { it.roll(pinfall) })

inline fun <E> List<E>.mapAtIndex(i: Int, f: (E) -> E): List<E> =
    set(i, f(get(i)))

fun <E> List<E>.set(i: Int, e: E): List<E> =
    toMutableList().apply { set(i, e) }