package dev.javaToKotlin


sealed interface Frame

interface PlayableFrame : Frame {
    fun roll(pinCount: PinCount): Frame
}

class UnplayedFrame : PlayableFrame {
    override fun roll(pinCount: PinCount): Frame {
        return when(pinCount){
            PinCount(10) -> Strike()
            else -> InProgressFrame()
        }
    }
}

class InProgressFrame : PlayableFrame {
    override fun roll(pinCount: PinCount): Frame {
        return OpenFrame()
    }
}

class OpenFrame : Frame

class Strike : Frame

@JvmInline
value class PinCount(val value: Int) {
    init {
        require(value in 0..10)
    }
}
class Score

typealias Player = String

open class Line protected constructor(
    val player: Player,
    val frames: List<Frame>
) {

    companion object {
        operator fun invoke(
            player: Player,
            noOfFrames: Int = 10
        ): Line = when (noOfFrames) {
            in Int.MIN_VALUE..0 -> CompletedLine(player, emptyList())
            else -> PlayableLine(player, List(noOfFrames) {
                UnplayedFrame()
            })
        }
    }
}

class CompletedLine(player: Player, frames: List<Frame>) : Line(player, frames)

interface Game {
    val lines : List<Line>
}

class InProgressGame(override val lines: List<Line>) : Game {
    fun roll(pinCount: PinCount) : Game {
        return this
    }
    
}

fun newGame(players: List<String>, numberOfFrames: Int = 10): InProgressGame {
    return InProgressGame(players.map {player -> Line(player, numberOfFrames)})
}

class PlayableLine(player: Player, frames: List<Frame>) : Line(player, frames) {
    fun roll(pinCount: PinCount): Line {
        val currentFrame = frames.firstOrNull { it is PlayableFrame }
        require(currentFrame is PlayableFrame){
            "Unexpected non playable frame"
        }
        val newFrameState = currentFrame.roll(pinCount)
        val newFrames = frames.replacing(currentFrame, newFrameState)
        return when (newFrames.last()) {
            is PlayableFrame -> PlayableLine(player, newFrames)
            else -> CompletedLine(player, newFrames)
        }
    }
}


fun <T> List<T>.replacing(item: T, newItem: T): List<T> = this.map {
    if (it === item) newItem
    else it
}
class Spare
class Turn
class MatchState
class Result
class Roll
class PerfectGame
class Winner
class PlayerQueue
class Draw
class Bonus
class CurrentFrame
