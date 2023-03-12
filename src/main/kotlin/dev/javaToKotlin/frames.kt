package dev.javaToKotlin

open class Frame

class UnplayedFrame : Frame(), PlayableFrame {
    override fun roll(pinCount: PinCount): Frame =
        when (pinCount.value) {
            10 -> Strike()
            else -> InProgressFrame(pinCount)
        }
}

class UnplayedFinalFrame : Frame(), PlayableFrame {
    override fun roll(pinCount: PinCount): Frame =
        when (pinCount.value) {
            10 -> FinalFrameStrike()
            else -> InProgressFrame(pinCount)
        }
}

class Strike : Frame()

class FinalFrameStrike : Frame(), PlayableFrame {
    override fun roll(pinCount: PinCount): Frame =
        FinalBonusFrame(PinCount(10)!!, pinCount)
}

class FinalBonusFrame(
    val roll1: PinCount,
    val roll2: PinCount
) : Frame(), PlayableFrame {
    override fun roll(pinCount: PinCount): Frame =
        CompletedFinalFrame(roll1, roll2, pinCount)
}

class CompletedFinalFrame(
    val roll1: PinCount,
    val roll2: PinCount,
    val roll3: PinCount
) : Frame() {

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