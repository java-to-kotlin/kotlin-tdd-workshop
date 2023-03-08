package dev.javaToKotlin

@JvmInline
value class PinCount private constructor(val value: Int) {

    init {
        require(value in 0..10)
    }

    companion object {
        operator fun invoke(value: Int): PinCount? =
            when (value) {
                in 0..10 -> PinCount(value)
                else -> null
            }
        val zero = PinCount(0)
    }

    fun toScore() = Score(this.value)
    operator fun plus(other: PinCount) = Score(this.value + other.value)
}