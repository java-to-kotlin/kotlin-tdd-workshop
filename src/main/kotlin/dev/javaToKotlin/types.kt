package dev.javaToKotlin


class Frame
@JvmInline
value class PinCount(val value: Int){
    init{
        require(value in 0..10)
    }
}
class Score
typealias Player = String
open class Line protected constructor (
    val player: Player,
    val frames: List<Frame>
) {
    companion object {
        operator fun invoke(
            player: Player,
            frames: List<Frame>
        ) : Line = when {
            frames.isEmpty() -> Line(player, frames)
            else -> PlayableLine(player, frames)
        }
    }
}
typealias Game = List<Line>

class PlayableLine(player: Player, frames: List<Frame>)
    : Line(player, frames) {
    
}


class Strike
class Spare
class Turn
class MatchState
class Result
class Roll
class PerfectGame
class Winner
class PlayerQueue
class Draw
class Bonus
class CurrentFrame
