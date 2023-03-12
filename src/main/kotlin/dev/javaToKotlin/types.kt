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


@JvmInline
value class NonNegativeInt(val value: Int) {
    init {
        require(value >= 0)
    }
}

operator fun NonNegativeInt.plus(score: NonNegativeInt): NonNegativeInt = TODO()
operator fun NonNegativeInt.plus(pinCount: PinCount): NonNegativeInt = TODO()

