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

class PlayableLine(
    val rolled: Int
) : Line {

}

class CompleteLine : Line {

}

fun PlayableLine.roll(pinfall: Pinfall): Line {
    return when (rolled) {
        0 -> PlayableLine(1)
        else -> CompleteLine()
    }
}

fun newLine(frameCount: Int): PlayableLine {
    return PlayableLine(0)
}