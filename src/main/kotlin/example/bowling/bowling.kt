package example.bowling

typealias Player = Int

class Game

@JvmInline
value class Pinfall private constructor(val value: Int) {
    companion object : (Int) -> Pinfall? {
        override fun invoke(i: Int): Pinfall? = when {
            i < 0 || i > 10 -> null
            else -> Pinfall(i)
        }
    }
}

val Int.pins get(): Pinfall = TODO()

interface Frame

object UnplayedFrame : Frame {
    fun roll(pins: Pinfall): Frame =
        when {
            pins.value == 10 -> Strike
            else -> IncompleteFrame()
        }
}

object Strike : Frame

class IncompleteFrame() : Frame {
    fun roll(pins: Pinfall): Frame =
        OpenFrame()
}

class OpenFrame : Frame

class Line

class Score

