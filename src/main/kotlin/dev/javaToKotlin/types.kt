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
data class Line(
    val player: Player,
    val frames: List<Frame>
)
typealias Game = List<Line>



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
