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
    fun roll(pins: Pinfall): Frame = Strike
}

object Strike : Frame

class Line

class Score

