@file:Suppress("unused", "UNUSED_PARAMETER")

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

open class Line(
    val player: Player,
    val frames: List<Frame>
)

class PlayableLine(
    player: Player,
    frames: List<Frame>
) : Line(player, frames) {
    fun roll(pinCount: PinCount): Line = TODO()
}

open class Frame

class PlayableFrame : Frame() {
    fun roll(pinCount: PinCount): Frame = TODO()
}

@JvmInline
value class Score(val value: Int) {
    init {
        require(value in 0..300)
    }
    operator fun plus(pinCount: PinCount): Score = TODO()
    operator fun plus(score: Score): Score = TODO()
}

@JvmInline
value class PinCount(val value: Int) {
    init {
        require(value in 0..10)
    }

    operator fun plus(that: PinCount): PinCount = TODO()
}

