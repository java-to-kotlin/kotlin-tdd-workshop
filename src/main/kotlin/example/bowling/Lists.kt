package example.bowling

// The same operations as PersistentList

fun <T> List<T>.set(i: Int, newValue: T): List<T> =
    subList(0,i) + newValue + subList(i+1,size)

fun <T> List<T>.removeAt(i: Int): List<T> =
    subList(0,i) + subList(i+1,size)
