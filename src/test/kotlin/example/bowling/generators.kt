package example.bowling

import io.kotest.property.Arb
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map

fun Arb.Companion.roll(max: PinCount = PinCount.MAX) =
    int(PinCount.ZERO.score()..max.score()).map { it.pins }

fun Arb.Companion.openFrame(): Arb<Pair<PinCount, PinCount>> =
    frame(maxScore = 9.pins)

fun Arb.Companion.spare(): Arb<Pair<PinCount, PinCount>> =
    roll(max = 9.pins).map { i -> Pair(first = i, second = PinCount.MAX - i) }

fun Arb.Companion.frame(
    maxScore: PinCount = PinCount.MAX,
    maxFirstRoll: PinCount = maxScore
): Arb<Pair<PinCount, PinCount>> =
    roll(max = maxFirstRoll)
        .flatMap { i -> roll(max = maxScore - i).map { j -> Pair(first = i, second = j) } }
