package example.bowling

import io.kotest.property.Arb
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map

fun Arb.Companion.roll(max: Int = 10) = int(0..max)

fun Arb.Companion.openFrame(): Arb<Pair<Int, Int>> =
    frame(maxScore = 9)

fun Arb.Companion.spare(): Arb<Pair<Int, Int>> =
    roll(max = 9).map { i -> Pair(first = i, second = 10 - i) }

fun Arb.Companion.frame(maxScore: Int = 10, maxFirstRoll: Int = maxScore): Arb<Pair<Int, Int>> =
    roll(max = maxFirstRoll).flatMap { i -> roll(max = maxScore - i).map { j -> Pair(first = i, second = j) } }
