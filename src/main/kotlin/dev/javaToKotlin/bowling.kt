package dev.javaToKotlin

@JvmInline
value class Pinfall(val fallenPins: Int) {
    init {
        require(fallenPins in 0..10)
    }
}

fun UnplayedFrame.roll(pinfall: Pinfall): Frame {

    return PartialFrame()

}

interface Frame

class UnplayedFrame() : Frame {

}

class PartialFrame : Frame {

}
