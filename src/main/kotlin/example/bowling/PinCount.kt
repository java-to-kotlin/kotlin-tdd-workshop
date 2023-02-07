package example.bowling

class PinCount private constructor(private val n: Int) : Comparable<PinCount> {
    override fun toString(): String = n.toString()
    override fun equals(other: Any?): Boolean = (other is PinCount) && other.n == n
    override fun hashCode(): Int = n.hashCode()
    
    operator fun plus(that: PinCount): PinCount = fromInt(this.n + that.n)
    operator fun minus(that: PinCount): PinCount = fromInt(this.n - that.n)
    
    override fun compareTo(other: PinCount): Int = n.compareTo(other.n)
    
    fun score() = n
    
    companion object {
        fun fromInt(n: Int) = PinCount(n.coerceIn(0, 10))
        
        val ZERO = PinCount(0)
        val MAX = PinCount(10)
    }
}

val Int.pins get() = PinCount.fromInt(this)
