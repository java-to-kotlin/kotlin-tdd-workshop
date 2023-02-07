package example.bowling

import example.bowling.PinCount.Companion
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.checkAll
import kotlin.test.assertTrue

fun Arb.Companion.pinCount() = Arb.int(0, 10).map { it.pins }

class PinCountTest : AnnotationSpec() {
    @Test
    suspend fun `pin count is clamped to range 0 to 10 inclusive`() {
        checkAll(Arb.int()) { n ->
            assertTrue(n.pins in PinCount.ZERO..PinCount.MAX)
        }
    }
    
    @Test
    suspend fun `result of addition is clamped to range 0 to 10 inclusive`() {
        checkAll(Arb.pinCount(), Arb.pinCount()) { x, y ->
            assertTrue(x + y >= x)
            assertTrue(x + y >= y)
            assertTrue(x + y in PinCount.ZERO..Companion.MAX)
        }
    }
    
    @Test
    suspend fun `result of subtraction is clamped to range 0 to 10 inclusive`() {
        checkAll(Arb.pinCount(), Arb.pinCount()) { x, y ->
            assertTrue(x - y <= x)
            assertTrue(x - y in PinCount.ZERO..Companion.MAX)
        }
    }
    
    @Test
    suspend fun `associativity of addition`() {
        checkAll(Arb.pinCount(), Arb.pinCount(), Arb.pinCount()) { x, y, z ->
            assertTrue(((x + y) + z) == (x + (y + z)))
        }
    }
    
    @Test
    suspend fun `commutativity of addition`() {
        checkAll(Arb.pinCount(), Arb.pinCount()) { x, y ->
            assertTrue(x + y == y + x)
        }
    }
    
    @Test
    suspend fun `addition, subtraction and zero`() {
        checkAll(Arb.pinCount()) { x ->
            assertTrue(x + PinCount.ZERO == x)
            assertTrue(x - PinCount.ZERO == x)
            assertTrue(x - x == PinCount.ZERO)
        }
    }
}
