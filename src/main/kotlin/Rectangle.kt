interface Rectangle<out T> : Collection<T> {
    val width: Int
    val height: Int
    operator fun get(x: Int, y: Int): T
    operator fun get(coordinate: Coordinate): T = get(coordinate.x, coordinate.y)
    fun rows(): List<List<T>>
    fun columns(): List<List<T>>
    fun transpose(): Rectangle<T>
    fun toIndexedList(): List<Pair<Coordinate, T>>

    data class Coordinate(val x: Int, val y: Int)
}

private class RectangleImpl<T>(override val width: Int, override val height: Int, init: (Int, Int) -> T) : Rectangle<T> {

    private val content = List(width * height) { index -> init(index % width, index / width) }

    override operator fun get(x: Int, y: Int): T = content[x + y * width]
    override val size: Int
        get() = width * height

    override fun isEmpty(): Boolean = width == 0 && height == 0

    override fun iterator(): Iterator<T> = content.iterator()

    override fun containsAll(elements: Collection<T>): Boolean = content.containsAll(elements)

    override fun contains(element: T): Boolean = content.contains(element)

    override fun rows(): List<List<T>> = if (content.isNotEmpty()) content.chunked(width) else emptyList()

    override fun columns(): List<List<T>> = if (content.isNotEmpty()) content.byNth(width) else emptyList()

    override fun transpose(): Rectangle<T> = RectangleImpl(height, width) { x, y -> this[y, x] }

    override fun toIndexedList(): List<Pair<Rectangle.Coordinate, T>> =
        content.mapIndexed { index, it -> Rectangle.Coordinate(index % width, index / width) to it }

    override fun equals(other: Any?): Boolean {
        if (other !is Rectangle<*>) {
            return false
        }
        if (width != other.width || height != other.height) {
            return false
        }
        if (other is RectangleImpl<*>) {
            return content == other.content
        }
        return asSequence().zip(other.asSequence()).all { (left, right) -> left == right }
    }

    override fun hashCode(): Int {
        return content.hashCode()
    }

}

interface MutableRectangle<T> : Rectangle<T>, MutableCollection<T> {
    operator fun set(x: Int, y: Int, value: T)
    operator fun set(coordinate: Rectangle.Coordinate, value: T) {
        set(coordinate.x, coordinate.y, value)
    }

}
class MutableRectangleImpl<T>(override val width: Int, override val height: Int, init: (Int, Int) -> T) : MutableRectangle<T> {

    private val content = MutableList(width * height) { index -> init(index % width, index / width) }

    override fun set(x: Int, y: Int, value: T) {
        content[x + y*width] = value
    }

    override operator fun get(x: Int, y: Int): T = content[x + y * width]

    override fun rows(): List<List<T>> = if (content.isNotEmpty()) content.chunked(width) else emptyList()

    override fun columns(): List<List<T>> = if (content.isNotEmpty()) content.byNth(width) else emptyList()


    override fun transpose(): Rectangle<T> = RectangleImpl(height, width) { x, y -> this[y, x] }

    override fun toIndexedList(): List<Pair<Rectangle.Coordinate, T>> {
        TODO("Not yet implemented")
    }

    override val size: Int
        get() = width * height

    override fun isEmpty(): Boolean {
        return width * height == 0
    }

    override fun iterator(): MutableIterator<T> {
        return content.iterator()
    }

    override fun clear() {
        throw UnsupportedOperationException()
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun remove(element: T): Boolean {
        throw UnsupportedOperationException()
    }

    override fun addAll(elements: Collection<T>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun add(element: T): Boolean {
        throw UnsupportedOperationException()
    }

    override fun containsAll(elements: Collection<T>): Boolean = content.containsAll(elements)

    override fun contains(element: T): Boolean = content.contains(element)

    override fun equals(other: Any?): Boolean {
        if (other !is Rectangle<*>) {
            return false
        }
        if (width != other.width || height != other.height) {
            return false
        }
        if (other is MutableRectangleImpl<*>) {
            return content == other.content
        }
        return asSequence().zip(other.asSequence()).all { (left, right) -> left == right }
    }

    override fun hashCode(): Int {
        return content.hashCode()
    }

}

fun <T> List<List<T>>.transpose(): List<List<T>> = List(this[0].size) { y -> List(this.size) { x -> this[x][y] } }


fun <T> List<List<T>>.toRectangle(): Rectangle<T> {
    val width = firstOrNull()?.size ?: 0
    require(all { it.size == width }) { "All elements should have the same size" }

    return RectangleImpl(width, this.size) { x, y -> this[y][x] }
}
fun <T> List<List<T>>.toMutableRectangle(): MutableRectangle<T> {
    val width = firstOrNull()?.size ?: 0
    require(all { it.size == width }) { "All elements should have the same size" }

    return MutableRectangleImpl(width, this.size) { x, y -> this[y][x] }
}

enum class Side {
    North, East, South, West
}

fun <T> Rectangle<T>.toMutableRectangle(): MutableRectangle<T> {
    return MutableRectangleImpl(width, height) { x, y -> get(x, y) }
}

fun <T> Rectangle<T>.neighbours(x: Int, y: Int): Map<Side, T> {
    val result = mutableMapOf<Side, T>()
    if (y > 0) {
        result[Side.North] = this[x, y - 1]
    }
    if (x < width - 1) {
        result[Side.East] = this[x + 1, y]
    }
    if (y < height - 1) {
        result[Side.South] = this[x, y + 1]
    }
    if (x > 0) {
        result[Side.West] = this[x - 1, y]
    }
    return result
}