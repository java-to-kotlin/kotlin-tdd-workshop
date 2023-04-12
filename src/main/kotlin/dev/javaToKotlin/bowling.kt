package dev.javaToKotlin

@JvmInline
value class Pinfall(val fallenPins: Int) {
    init {
        require(fallenPins in 0..10)
    }
}

fun UnplayedFrame.roll(pinfall: Pinfall) =
    when (pinfall.fallenPins) {
        10 -> CompleteFrame()
        else -> PartialFrame()
    }

fun PartialFrame.roll(pinfall: Pinfall) =
    CompleteFrame()

interface Frame

class UnplayedFrame() : Frame {

}

class PartialFrame : Frame {

}

class CompleteFrame : Frame {

}

interface Line

data class PlayableLine(
    val frameCount: Int,
    val rolled: Int = 0
) : Line

class CompleteLine : Line {

}

fun PlayableLine.roll(pinfall: Pinfall): Line =
    when {
        rolled >= frameCount * 2 - 1 -> CompleteLine()
        else -> copy(rolled = rolled + 1)
    }

fun newLine(frameCount: Int): PlayableLine {
    return PlayableLine(frameCount = frameCount)
}