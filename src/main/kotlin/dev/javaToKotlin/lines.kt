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

class PlayableLine(
    player: Player,
    frames: List<Frame>
) : Line(player, frames) {
    fun roll(pinCount: PinCount): Line {
        val currentFrame = frames.firstOrNull { it is PlayableFrame }
        if (currentFrame !is PlayableFrame)
            error("Unexpectedly not a playable frame")
        val newFrame: Frame = currentFrame.roll(pinCount)
        val newFrames: List<Frame> = frames.replace(currentFrame, newFrame)
        return if (newFrames.any { it is PlayableFrame })
            PlayableLine(player, newFrames)
        else
            CompletedLine(player, newFrames)
    }
}

fun <E> List<E>.replace(item: E, newItem: E): List<E> =
    this.map { if (it == item) newItem else it }

class CompletedLine(
    player: Player,
    frames: List<Frame>
) : Line(player, frames)
