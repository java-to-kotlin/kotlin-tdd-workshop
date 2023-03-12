package dev.javaToKotlin

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
    val roll2: PinCount
) : Frame() {

}

interface PlayableFrame {
    fun roll(pinCount: PinCount): Frame
}