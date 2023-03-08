package dev.javaToKotlin

import dev.javaToKotlin.PinCount.Companion.zero

@JvmInline
value class Score(val value: Int) {
    operator fun plus(other: Score) = Score(this.value + other.value)
    operator fun plus(other: PinCount) = Score(this.value + other.value)
}

class Game(val lines: List<Line>)

typealias Player = String

sealed class Frame {
    abstract fun pinsString(): String
    open fun score(frames: List<Frame>): Score? = null
}

interface PlayableFrame {
    fun roll(pins: PinCount): Frame
}

interface HasRoll1 {
    val roll1: PinCount
}

interface HasRoll2 {
    val roll2: PinCount
}

class UnplayedFrame : Frame(), PlayableFrame {
    override fun roll(pins: PinCount): Frame =
        when (pins.value) {
            10 -> Strike()
            else -> InProgressFrame(pins)
        }

    override fun pinsString() = " , "
    override fun score(frames: List<Frame>): Score? = null
}

class InProgressFrame(
    override val roll1: PinCount
) : Frame(), PlayableFrame, HasRoll1 {
    override fun roll(pins: PinCount) = CompletedFrame(this.roll1, pins)
    override fun pinsString() = "${roll1.scorecard()}, "
    override fun score(frames: List<Frame>) = roll1.toScore()
}

class CompletedFrame(
    override val roll1: PinCount,
    override val roll2: PinCount
) : Frame() , HasRoll1, HasRoll2 {
    override fun pinsString() = render(roll1, roll2)
    override fun score(frames: List<Frame>): Score {
        val baseScore = roll1 + roll2
        return if (roll1 + roll2 == Score(10)) {
            val nextFrame: Frame? = frames.nextItemFrom(this)
            if (nextFrame is HasRoll1)
                baseScore + nextFrame.roll1
            else
                baseScore
        } else
            baseScore
    }
}

class UnplayedFinalFrame : Frame(), PlayableFrame {
    override fun roll(pins: PinCount) = InProgressFinalFrame(pins)
    override fun pinsString() = " , , "
    override fun score(frames: List<Frame>): Score? = null
}

class InProgressFinalFrame(
    override val roll1: PinCount
) : Frame(), PlayableFrame, HasRoll1 {
    override fun roll(pins: PinCount): Frame =
        when {
            this.roll1.value + pins.value >= 10 -> FinalBonusFrame(this.roll1, pins)
            else -> CompletedFinalFrame(this.roll1, pins)
        }
    override fun pinsString() = "${roll1.scorecard()}, , "
    override fun score(frames: List<Frame>) = roll1.toScore()
}

class FinalBonusFrame(
    override val roll1: PinCount,
    override val roll2: PinCount
) : Frame(), PlayableFrame, HasRoll1, HasRoll2 {
    override fun roll(pins: PinCount) = CompletedFinalFrame(roll1, roll2, pins)
    override fun pinsString() = render(roll1, roll2) + ", "
    override fun score(frames: List<Frame>) = roll1 + roll2
}

fun render(roll1: PinCount, roll2: PinCount) =
    when {
        roll1.value == 10 -> "${roll1.scorecard()},${roll2.scorecard()}"
        roll1.value + roll2.value == 10 -> "${roll1.scorecard()},/"
        else -> "${roll1.scorecard()},${roll2.scorecard()}"
    }

class CompletedFinalFrame(
    override val roll1: PinCount,
    override val roll2: PinCount,
    val roll3: PinCount? = null
) : Frame(), HasRoll1, HasRoll2 {
    override fun pinsString() = render(roll1, roll2) + "," +
            (roll3?.scorecard() ?: " ")
    override fun score(frames: List<Frame>) = roll1 + roll2 + (roll3 ?: zero)
}


class Strike : Frame(), HasRoll1 {
    override val roll1 = PinCount(10)!!
    override fun pinsString() = " ,X"
    override fun score(frames: List<Frame>): Score {
        val nextFrame: Frame? = frames.nextItemFrom(this)
        val nextNextFrame: Frame? = nextFrame?.let { frames.nextItemFrom(it) }
        val nextRoll = when {
            nextFrame is HasRoll1 -> nextFrame.roll1
            else -> zero
        }
        val nextNextRoll = when {
            nextFrame is HasRoll2 -> nextFrame.roll2
            nextNextFrame is HasRoll1 -> nextNextFrame.roll1
            else -> zero
        }
        return roll1 + nextRoll + nextNextRoll
    }
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
        (listOf(player) + framesStrings()).joinToString("|")

    private fun framesStrings(): ArrayList<String> {
        val destination = ArrayList<String>(frames.size)
        var score = Score(0)
        for (item in frames) {
            val frameScore = item.score(frames)
            score = score + (item.score(frames)?: Score(0))
            val scoreString = if (frameScore == null) " " else score.value
            destination.add(
                item.pinsString() + "," + scoreString
            )
        }
        return destination
    }
}

class PlayableLine(
    player: Player,
    frames: List<Frame>
) : Line(player, frames) {
    fun roll(pinCount: PinCount): Line {
        val currentFrame = frames.first { it is PlayableFrame }
        if (currentFrame !is PlayableFrame)
            error("Unexpectedly unplayable frame")
        val newFrames = frames.replace(
            currentFrame,
            currentFrame.roll(pinCount)
        )
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


private fun PinCount.scorecard() = when (this.value) {
    10 -> "X"
    else -> this.value.toString()
}

private fun <T> List<T>.replace(replace: T, with: T): List<T> =
    toMutableList().apply {
        this[indexOf(replace)] = with
    }.toList()

private fun <T> List<T>.nextItemFrom(item: T): T? {
    val index = indexOf(item)
    return if (index == -1 || index == size - 1) null else this[index + 1]
}
