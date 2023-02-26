package example.bowling

sealed interface Frame
object UnplayedFrame : Frame
data class PartialFrame(val roll1: Int) : Frame


fun Frame.roll(pinfall: Int): Frame {
    return when (this) {
        UnplayedFrame -> PartialFrame(pinfall)
        is PartialFrame -> this
    }
}

