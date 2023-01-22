package com.natpryce.kotlinconf2023.bowling

fun <T> T.repeated(times: Int, f: (T) -> T): T {
    var result = this
    repeat(times) {
        result = f(result)
    }
    return result
}

fun <T> T.repeatedIndexed(times: Int, f: (Int, T) -> T): T {
    var result = this
    repeat(times) { i ->
        result = f(i, result)
    }
    return result
}
