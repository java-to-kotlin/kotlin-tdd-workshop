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
    }
}