@file:Suppress("unused", "UNUSED_PARAMETER", "TODO")

package dev.javaToKotlin

typealias Player = String

open class Game(
    val lines: List<Line>
) {
    constructor() : this(emptyList())
}

class PlayableGame: Game() {
    fun roll(pinCount: PinCount): Game = TODO()
}

open class Line protected constructor(
    val player: Player,
    val frames: List<Frame>
) {
    companion object {
        operator fun invoke(
            player: Player,
            frameCount: NonNegativeInt
        ): Line = when {
            frameCount.value == 0 -> Line(player, emptyList())
            else -> PlayableLine(player, emptyList())
        }
    }
}

class PlayableLine(
    player: Player,
    frames: List<Frame>
) : Line(player, frames) {
    fun roll(pinCount: PinCount): Line = this
}

open class Frame

class PlayableFrame : Frame() {
    fun roll(pinCount: PinCount): Frame = TODO()
}

@JvmInline
value class NonNegativeInt(val value: Int) {
    init {
        require(value >= 0)
    }
}

operator fun NonNegativeInt.plus(score: NonNegativeInt): NonNegativeInt = TODO()
operator fun NonNegativeInt.plus(pinCount: PinCount): NonNegativeInt = TODO()

