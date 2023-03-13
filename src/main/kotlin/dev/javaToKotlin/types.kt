package dev.javaToKotlin


sealed interface Frame

interface PlayableFrame: Frame {
    fun roll(pinCount: PinCount): Frame
}

class UnplayedFrame: PlayableFrame {
    override fun roll(pinCount: PinCount): Frame {
        return InProgressFrame()
    }
}

class InProgressFrame: PlayableFrame {
    override fun roll(pinCount: PinCount): Frame {
        return OpenFrame()
    }
}

class OpenFrame: Frame

@JvmInline
value class PinCount(val value: Int){
    init{
        require(value in 0..10)
    }
}
class Score
typealias Player = String
open class Line protected constructor (
    val player: Player,
    val frames: List<Frame>
) {
    companion object {
        operator fun invoke(
            player: Player,
            frames: List<Frame>
        ) : Line = when {
            frames.isEmpty() -> Line(player, frames)
            else -> PlayableLine(player, frames)
        }
    }
}

class CompletedLine(player: Player, frames: List<Frame>): Line(player, frames)

typealias Game = List<Line>

class PlayableLine(player: Player, frames: List<Frame>)
    : Line(player, frames) {
    fun roll(pinCount: PinCount): Line {
        val currentFrame = frames.firstOrNull{it is PlayableFrame}
        if(currentFrame !is PlayableFrame)  error("Unexpected non playable frame")
        val newFrame = currentFrame.roll(pinCount)
        return when (newFrame) {
            is InProgressFrame -> PlayableLine(player, listOf(newFrame))
            else -> CompletedLine(player, listOf(newFrame))
        }
    }
}


class Strike
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
