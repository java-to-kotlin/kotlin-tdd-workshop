package dev.javaToKotlin

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