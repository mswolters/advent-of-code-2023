import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src/main/resources/", "$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)

fun Iterable<String>.asInts(): List<Int> = map { it.toInt() }
fun Sequence<String>.asInts(): Sequence<Int> = map { it.toInt() }

fun Iterable<String>.asLongs(): List<Long> = map { it.toLong() }
fun Sequence<String>.asLongs(): Sequence<Long> = map { it.toLong() }

fun <T> Iterable<T>.asPair(): Pair<T, T> {
    require(this.count() == 2)
    return Pair(this.first(), this.last())
}

/**
 * Like Iterable<T>.chunked, but by columns. Every list will contain every nth element of the starting element.
 */
fun <T> Iterable<T>.byNth(n: Int): List<List<T>> {
    val result = List(n) { mutableListOf<T>() }
    forEachIndexed { i: Int, t: T ->
        result[i.mod(n)].add(t)
    }
    return result
}

fun Int.pow(exponent: Int): Int = toBigInteger().pow(exponent).toInt()

fun <A, B> Iterable<A>.cartesianProduct(other: Iterable<B>): Sequence<Pair<A, B>> = sequence {
    forEach { a ->
        other.forEach { b ->
            yield(a to b)
        }
    }
}
