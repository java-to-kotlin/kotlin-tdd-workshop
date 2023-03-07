package dev.javaToKotlin

@JvmInline
value class PinCount(val value: Int) {
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
}

class Score

class Frame

class Game(val lines: List<Line>)

typealias Player = String

class Line(
    val player: Player,
    val frames: List<Frame>
)