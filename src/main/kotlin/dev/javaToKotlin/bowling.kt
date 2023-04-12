package dev.javaToKotlin

@JvmInline
value class Pinfall(val fallenPins: Int) {
    init {
        require(fallenPins in 0..10)
    }

    override fun toString(): String = fallenPins.toString()
}

sealed interface Frame

sealed interface IncompleteFrame : Frame {
    fun roll(pinfall: Pinfall): Frame
}

class UnplayedFrame() : IncompleteFrame {
    override fun roll(pinfall: Pinfall) =
        when (pinfall.fallenPins) {
            10 -> Strike()
            else -> PartialFrame(pinfall)
        }
}

class PartialFrame(val roll1: Pinfall) : IncompleteFrame {
    override fun roll(pinfall: Pinfall) =
        OpenFrame(roll1, pinfall)
}

sealed interface CompleteFrame : Frame

class OpenFrame(val roll1: Pinfall, val roll2: Pinfall) : CompleteFrame {

}

class Strike : CompleteFrame

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
    val newFrame = (frames[currentFrameIndex] as IncompleteFrame).roll(pinfall)
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
    return frames.indexOfFirst { it is IncompleteFrame }
}

fun Line.render(): String =
    frames.joinToString("|") { it.render() }

private fun Frame.render() =
    when (this) {
        is PartialFrame -> "${this.roll1.render()}, , "
        is OpenFrame -> "${this.roll1},${this.roll2}, "
        is Strike -> " ,X, "
        is UnplayedFrame -> " , , "
    }

private fun Pinfall.render() = when (fallenPins) {
    0 -> "-"
    else -> fallenPins.toString()
}
