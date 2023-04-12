package dev.javaToKotlin

@JvmInline
value class Pinfall(val fallenPins: Int) {
    init {
        require(fallenPins in 0..10)
    }
}

interface Frame

interface PlayableFrame {
    fun roll(pinfall: Pinfall): Frame
}

class UnplayedFrame() : Frame, PlayableFrame {
    override fun roll(pinfall: Pinfall) =
        when (pinfall.fallenPins) {
            10 -> CompleteFrame()
            else -> PartialFrame()
        }
}

class PartialFrame : Frame, PlayableFrame {
    override fun roll(pinfall: Pinfall) =
        CompleteFrame()
}

class CompleteFrame : Frame {

}

interface Line {
}

data class PlayableLine(
    val frameCount: Int,
    val rolled: Int = 0,
    val frames: List<Frame>
) : Line

class CompleteLine : Line {

}

fun PlayableLine.roll(pinfall: Pinfall): Line {
    val currentFrame = frames.get(currentFrameIndex) as PlayableFrame
    val newFrame = currentFrame.roll(pinfall)
    val newFrames = frames.toMutableList().apply {
        set(currentFrameIndex, newFrame)
    }

    return when {
        rolled >= frameCount * 2 - 1 -> CompleteLine()
        else -> copy(rolled = rolled + 1, frames = newFrames)
    }
}

fun newLine(frameCount: Int): PlayableLine {
    return PlayableLine(frameCount = frameCount,
        frames = List(frameCount) { UnplayedFrame() })
}

val PlayableLine.currentFrameIndex: Int
    get() {
        return frames.indexOfFirst { it is PlayableFrame }
    }
