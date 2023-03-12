@file:Suppress("unused", "UNUSED_PARAMETER", "TODO")

package dev.javaToKotlin

typealias Player = String

open class Game(
    val lines: List<Line>
) {
    constructor() : this(emptyList())
}

class PlayableGame: Game() {
    fun roll(pinCount: PinCount): Game = TODO()
}

open class Line protected constructor(
    val player: Player,
    val frames: List<Frame>
) {
    companion object {
        operator fun invoke(
            player: Player,
            frameCount: NonNegativeInt
        ): Line = when {
            frameCount.value == 0 -> Line(player, emptyList())
            else -> PlayableLine(player, List(frameCount.value) { UnplayedFrame() })
        }
    }
}

class CompletedLine(
    player: Player,
    frames: List<Frame>
) : Line(player, frames)

class PlayableLine(
    player: Player,
    frames: List<Frame>
) : Line(player, frames) {
    fun roll(pinCount: PinCount): Line {
        val currentFrame = frames.first()
        if (currentFrame !is PlayableFrame)
            error("Unexpectedly not a playable frame")
        val newFrame = currentFrame.roll(pinCount)
        if (newFrame is PlayableFrame)
            return PlayableLine(player, listOf(newFrame))
        else
            return CompletedLine(player, listOf(newFrame))
    }
}

open class Frame

class UnplayedFrame : Frame(), PlayableFrame {
    override fun roll(pinCount: PinCount): Frame = InProgressFrame(pinCount)
}

class InProgressFrame(
    val roll1: PinCount
) : Frame(), PlayableFrame {
    override fun roll(pinCount: PinCount): Frame = CompletedFrame(roll1, pinCount)
}

class CompletedFrame(
    val roll1: PinCount,
    val roll: PinCount
) : Frame() {

}


interface PlayableFrame {
    fun roll(pinCount: PinCount): Frame
}

@JvmInline
value class NonNegativeInt(val value: Int) {
    init {
        require(value >= 0)
    }
}

operator fun NonNegativeInt.plus(score: NonNegativeInt): NonNegativeInt = TODO()
operator fun NonNegativeInt.plus(pinCount: PinCount): NonNegativeInt = TODO()

