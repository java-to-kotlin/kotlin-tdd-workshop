package dev.javaToKotlin

import java.util.concurrent.atomic.AtomicReference

fun <T> holdMy(thing: T): AtomicReference<T> = AtomicReference(thing)