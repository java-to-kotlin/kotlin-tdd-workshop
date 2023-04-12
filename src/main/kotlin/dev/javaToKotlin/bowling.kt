package dev.javaToKotlin

@JvmInline
value class Pinfall(val fallenPins: Int) {
    init {
        require(fallenPins in 0..10)
    }

    override fun toString(): String = fallenPins.toString()
}

interface Frame

interface PlayableFrame {
    fun roll(pinfall: Pinfall): Frame
}

class UnplayedFrame() : Frame, PlayableFrame {
    override fun roll(pinfall: Pinfall) =
        when (pinfall.fallenPins) {
            10 -> CompleteFrame(Pinfall(10), Pinfall(0))
            else -> PartialFrame(pinfall)
        }
}

class PartialFrame(val roll1: Pinfall) : Frame, PlayableFrame {
    override fun roll(pinfall: Pinfall) =
        CompleteFrame(roll1, pinfall)
}

class CompleteFrame(val roll1: Pinfall, val roll2: Pinfall) : Frame {

}

interface Line {
    val frames: List<Frame>
}

data class PlayableLine(
    override val frames: List<Frame>
) : Line

class CompleteLine(
    override val frames: List<Frame>
) : Line

fun PlayableLine.roll(pinfall: Pinfall): Line {
    val currentFrameIndex = getCurrentFrameIndex()
    val newFrame = (frames[currentFrameIndex] as PlayableFrame).roll(pinfall)
    val newFrames = frames.toMutableList().apply {
        set(currentFrameIndex, newFrame)
    }

    return when {
        newFrames.last() is CompleteFrame -> CompleteLine(newFrames)
        else -> copy(frames = newFrames)
    }
}

fun newLine(frameCount: Int): PlayableLine {
    return PlayableLine(frames = List(frameCount) { UnplayedFrame() })
}

private fun PlayableLine.getCurrentFrameIndex(): Int {
    return frames.indexOfFirst { it is PlayableFrame }
}

fun Line.render(): String =
    frames.joinToString("|") { it.render() }

private fun Frame.render() =
    when (this) {
        is PartialFrame -> "${this.roll1}, , "
        is CompleteFrame -> "${this.roll1},${this.roll2}, "
        else -> " , , "
    }
