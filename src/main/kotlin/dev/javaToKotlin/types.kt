package dev.javaToKotlin

@JvmInline
value class PinCount private constructor(val value: Int) {
    init {
        require(value in 0..10)
    }

    companion object {
        operator fun invoke(value: Int): PinCount? =
            when (value) {
                in 0..10 -> PinCount(value)
                else -> null
            }
    }

    override fun toString() = value.toString()

    fun scorecard() = when (value) {
        10 -> "X"
        else -> value.toString()
    }
}

class Score

class Game(val lines: List<Line>)

typealias Player = String

sealed class Frame {
    abstract fun scorecard(): String
}

interface PlayableFrame {
    fun roll(pins: PinCount): Frame
}

class UnplayedFrame : Frame(), PlayableFrame {
    override fun roll(pins: PinCount): Frame =
        when (pins.value) {
            10 -> Strike()
            else -> InProgressFrame(pins)
        }

    override fun scorecard() = " , "
}

class UnplayedFinalFrame : Frame(), PlayableFrame {
    override fun roll(pins: PinCount) = InProgressFinalFrame(pins)
    override fun scorecard() = " , , "
}

class InProgressFinalFrame(val pins: PinCount) : Frame(), PlayableFrame {
    override fun roll(pins: PinCount): Frame =
        when {
            this.pins.value + pins.value >= 10 -> FinalBonusFrame(this.pins, pins)
            else -> CompletedFinalFrame(this.pins, pins)
        }
    override fun scorecard() = "${pins.scorecard()}, , "
}


class FinalBonusFrame(
    val roll1: PinCount,
    val roll2: PinCount
) : Frame(), PlayableFrame {
    override fun roll(pins: PinCount) = CompletedFinalFrame(roll1, roll2, pins)
    override fun scorecard() = maybeSpare(roll1, roll2) + ", "
}

fun maybeSpare(roll1: PinCount, roll2: PinCount) = "${roll1.scorecard()}," +
        (if (roll1.value != 10 && roll1.value + roll2.value == 10) "/" else roll2.scorecard())

class InProgressFrame(val pins: PinCount) : Frame(), PlayableFrame {
    override fun roll(pins: PinCount) = CompletedFrame(this.pins, pins)
    override fun scorecard() = "${pins.scorecard()}, "
}

class CompletedFinalFrame(
    val roll1: PinCount,
    val roll2: PinCount,
    val roll3: PinCount? = null
) : Frame() {
    override fun scorecard() = maybeSpare(roll1, roll2) + "," +
            (roll3?.scorecard() ?: " ")
}

class CompletedFrame(
    val roll1: PinCount,
    val roll2: PinCount
) : Frame() {
    override fun scorecard() = maybeSpare(roll1, roll2)
}

class Strike : Frame() {
    override fun scorecard() = " ,X"
}

fun lineFor(
    player: Player,
    frameCount: Int
): Line = when {
    frameCount > 0 -> PlayableLine(
        player,
        List(frameCount - 1) { UnplayedFrame() } + UnplayedFinalFrame()
    )

    else -> CompletedLine(player, emptyList())
}

sealed class Line(
    val player: Player,
    val frames: List<Frame>
) {
    fun scorecard(): String =
        (listOf("$player") + frames.map { it.scorecard() }).joinToString("|")
}

class PlayableLine(
    player: Player,
    frames: List<Frame>
) : Line(player, frames) {
    fun roll(pinCount: PinCount): Line {
        val currentFrame = frames.first { it is PlayableFrame }
        if (currentFrame !is PlayableFrame)
            error("Unexpectedly unplayable frame")
        val newFrame = currentFrame.roll(pinCount)
        val newFrames = frames.toMutableList().apply {
            this[this.indexOf(currentFrame)] = newFrame
        }.toList()
        return if (newFrames.last() is PlayableFrame)
            PlayableLine(player, newFrames)
        else
            CompletedLine(player, newFrames)
    }
}

class CompletedLine(
    player: Player,
    frames: List<Frame>
) : Line(player, frames)