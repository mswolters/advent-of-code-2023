// https://iliyangermanov.medium.com/kotlin-function-memoization-for-practioners-c1950b7881f0

interface Memo1<A, R> { // 1
    fun recurse(a: A): R
}

fun <A, R> (Memo1<A, R>.(A) -> R).memoize(): (A) -> R {
    val memoized = object : Memoized1<A, R>() { // 3
        override fun Memo1<A, R>.function(a: A): R = this@memoize(a)
    }
    return { a -> // 4
        memoized.execute(a)
    }
}

abstract class Memoized1<A, R> { // 5
    private val cache = mutableMapOf<A, R>()
    private val memo = object : Memo1<A, R> {
        override fun recurse(a: A): R = cache.getOrPut(a) { function(a) }
    }

    protected abstract fun Memo1<A, R>.function(a: A): R

    fun execute(a: A): R = memo.recurse(a)
}

interface Memo2<A, B, R> {
    fun recurse(a: A, b: B): R
}

abstract class Memoized2<A, B, R> {
    private data class Input<A, B>(
        val a: A,
        val b: B
    )

    private val cache = mutableMapOf<Input<A, B>, R>()
    private val memo = object : Memo2<A, B, R> {
        override fun recurse(a: A, b: B): R =
            cache.getOrPut(Input(a, b)) { function(a, b) }
    }

    protected abstract fun Memo2<A, B, R>.function(a: A, b: B): R

    fun execute(a: A, b: B): R = memo.recurse(a, b)
}

fun <A, B, R> (Memo2<A, B, R>.(A, B) -> R).memoize(): (A, B) -> R {
    val memoized = object : Memoized2<A, B, R>() {
        override fun Memo2<A, B, R>.function(a: A, b: B): R = this@memoize(a, b)
    }
    return { a, b ->
        memoized.execute(a, b)
    }
}