package dev.javaToKotlin

@JvmInline
value class Pinfall(val fallenPins: Int) {
    init {
        require(fallenPins in 0..10)
    }
}

fun UnplayedFrame.roll(pinfall: Pinfall) =
    PartialFrame()

fun PartialFrame.roll(pinfall: Pinfall) =
    CompleteFrame()

interface Frame

class UnplayedFrame() : Frame {

}

class PartialFrame : Frame {

}

class CompleteFrame : Frame {

}
