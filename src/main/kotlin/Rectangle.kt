import kotlin.math.max
import kotlin.math.min

interface Rectangle<out T> : Collection<T> {
    val width: Int
    val height: Int
    operator fun get(x: Int, y: Int): T
    operator fun get(coordinate: Coordinate): T = get(coordinate.x, coordinate.y)
    fun indexedIterator(): Iterator<Pair<Coordinate, T>>
    fun rows(): List<List<T>>
    fun columns(): List<List<T>>
    fun transpose(): Rectangle<T>
    fun toIndexedList(): List<Pair<Coordinate, T>>

    fun isInBounds(x: Int, y: Int): Boolean = x in 0..<width && y in 0..<height
    fun isInBounds(coordinate: Rectangle.Coordinate): Boolean = isInBounds(coordinate.x, coordinate.y)

    data class Coordinate(val x: Int, val y: Int)
}

fun <T> Rectangle(width: Int, height: Int, init: (x: Int, y: Int) -> T): Rectangle<T> {
    return RectangleImpl(width, height, init)
}

private class RectangleImpl<T>(override val width: Int, override val height: Int, init: (Int, Int) -> T) : Rectangle<T> {

    private val content = List(width * height) { index -> init(index % width, index / width) }

    override operator fun get(x: Int, y: Int): T = content[x + y * width]
    override val size: Int
        get() = width * height

    override fun isEmpty(): Boolean = width == 0 && height == 0

    override fun iterator(): Iterator<T> = content.iterator()
    override fun indexedIterator(): Iterator<Pair<Rectangle.Coordinate, T>> {
        return iterator {
            for (x in 0..<width) {
                for (y in 0..<height) {
                    yield(Rectangle.Coordinate(x, y) to get(x, y))
                }
            }
        }
    }

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

private class MutableRectangleImpl<T>(override val width: Int, override val height: Int, init: (Int, Int) -> T) :
    MutableRectangle<T> {

    private var content = MutableList(width * height) { index -> init(index % width, index / width) }

    override fun set(x: Int, y: Int, value: T) {
        content[x + y * width] = value
    }

    override operator fun get(x: Int, y: Int): T = content[x + y * width]

    override fun rows(): List<List<T>> = if (content.isNotEmpty()) content.chunked(width) else emptyList()

    override fun columns(): List<List<T>> = if (content.isNotEmpty()) content.byNth(width) else emptyList()

    override fun transpose(): Rectangle<T> = RectangleImpl(height, width) { x, y -> this[y, x] }

    override fun toIndexedList(): List<Pair<Rectangle.Coordinate, T>> =
        content.mapIndexed { index, it -> Rectangle.Coordinate(index % width, index / width) to it }


    override val size: Int
        get() = width * height

    override fun isEmpty(): Boolean {
        return width * height == 0
    }

    override fun iterator(): MutableIterator<T> {
        return content.iterator()
    }

    override fun indexedIterator(): Iterator<Pair<Rectangle.Coordinate, T>> {
        return iterator {
            for (x in 0..<width) {
                for (y in 0..<height) {
                    yield(Rectangle.Coordinate(x, y) to get(x, y))
                }
            }
        }
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

private class AutoResizingMutableRectangleImpl<T>(
    var content: MutableRectangle<T>,
    var xStart: Int = 0,
    var yStart: Int = 0,
    val init: (Int, Int) -> T
) :
    MutableRectangle<T> {

    private fun resizeContent(x: Int, y: Int) {
        val maxX = max(x, content.width + xStart - 1)
        val maxY = max(y, content.height + yStart - 1)
        val minX = min(x, xStart)
        val minY = min(y, yStart)
        val newWidth = max(maxX - minX + 1, content.width)
        val newHeight = max(maxY - minY + 1, content.height)

        content = MutableRectangleImpl(newWidth, newHeight) { x, y ->
            val xBonus = xStart - minX
            val yBonus = yStart - minY
            if (content.isInBounds(x - xBonus, y - yBonus)) {
                content[x - xBonus, y - yBonus]
            } else {
                init(x + xStart, y + yStart)
            }
        }
        xStart = minX
        yStart = minY
    }

    override fun set(x: Int, y: Int, value: T) {
        if (!isInBounds(x, y)) {
            resizeContent(x, y)
        }
        content.set(x - xStart, y - yStart, value)
    }

    override val width: Int
        get() = content.width
    override val height: Int
        get() = content.height

    override fun get(x: Int, y: Int): T = content.get(x - xStart, y - yStart)

    override fun rows(): List<List<T>> = content.rows()

    override fun columns(): List<List<T>> = content.columns()

    override fun transpose(): Rectangle<T> = TODO() //content.transpose()

    override fun toIndexedList(): List<Pair<Rectangle.Coordinate, T>> = content.toIndexedList()

    override val size: Int
        get() = content.size

    override fun isEmpty(): Boolean = content.isEmpty()

    override fun iterator(): MutableIterator<T> = content.iterator()

    override fun indexedIterator(): Iterator<Pair<Rectangle.Coordinate, T>> = content.indexedIterator()

    override fun clear() {
        content = MutableRectangleImpl(0, 0, init)
        yStart = 0
        xStart = 0
    }

    override fun retainAll(elements: Collection<T>): Boolean = content.retainAll(elements)

    override fun removeAll(elements: Collection<T>): Boolean = content.removeAll(elements)

    override fun remove(element: T): Boolean = content.remove(element)

    override fun addAll(elements: Collection<T>): Boolean = content.addAll(elements)

    override fun add(element: T): Boolean = content.add(element)

    override fun containsAll(elements: Collection<T>): Boolean = content.containsAll(elements)

    override fun contains(element: T): Boolean = content.contains(element)

    override fun isInBounds(x: Int, y: Int): Boolean {
        return x - xStart in 0..<content.width && y - yStart in 0..<content.height
    }

}

fun <T> Rectangle<T>.toString(transform: ((T) -> CharSequence)? = null): String {
    return rows().joinToString("\n") { line -> line.joinToString("", transform = transform) }
}

fun <T> MutableRectangle(width: Int, height: Int, init: (x: Int, y: Int) -> T): MutableRectangle<T> {
    return MutableRectangleImpl(width, height, init)
}

fun <T> ResizingMutableRectangle(width: Int = 0, height: Int = 0, init: (x: Int, y: Int) -> T): MutableRectangle<T> {
    return AutoResizingMutableRectangleImpl(MutableRectangleImpl(width, height, init), init = init)
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

fun Rectangle<*>.edges(): Map<Rectangle.Coordinate, Side> {
    val ret = mutableMapOf<Rectangle.Coordinate, Side>()
    for (x in 0..<width) {
        ret[Rectangle.Coordinate(x, 0)] = Side.North
        ret[Rectangle.Coordinate(x, height - 1)] = Side.South
    }
    for (y in 0..<height) {
        ret[Rectangle.Coordinate(0, y)] = Side.West
        ret[Rectangle.Coordinate(width - 1, y)] = Side.East
    }
    return ret
}

enum class Side {
    North, East, South, West;

    fun opposite(): Side {
        return when (this) {
            North -> South
            East -> West
            South -> North
            West -> East
        }
    }

    fun perpendicular(): Set<Side> {
        return when (this) {
            North, South -> setOf(East, West)
            East, West -> setOf(North, South)
        }
    }
}

fun Rectangle<*>.topLeftCoordinate() = Rectangle.Coordinate(0, 0)
fun Rectangle<*>.topRightCoordinate() = Rectangle.Coordinate(width - 1, 0)
fun Rectangle<*>.bottomLeftCoordinate() = Rectangle.Coordinate(0, height - 1)
fun Rectangle<*>.bottomRightCoordinate() = Rectangle.Coordinate(width - 1, height - 1)

fun Side.coordinateNextTo(x: Int, y: Int): Rectangle.Coordinate {
    return when (this) {
        Side.North -> Rectangle.Coordinate(x, y - 1)
        Side.East -> Rectangle.Coordinate(x + 1, y)
        Side.South -> Rectangle.Coordinate(x, y + 1)
        Side.West -> Rectangle.Coordinate(x - 1, y)
    }
}

fun Side.coordinateNextTo(coordinate: Rectangle.Coordinate): Rectangle.Coordinate =
    coordinateNextTo(coordinate.x, coordinate.y)

fun <T> Rectangle<T>.toMutableRectangle(): MutableRectangle<T> {
    return MutableRectangleImpl(width, height) { x, y -> get(x, y) }
}

fun <T> Rectangle<T>.neighbours(x: Int, y: Int): Map<Side, Pair<T, Rectangle.Coordinate>> {
    val result = mutableMapOf<Side, Pair<T, Rectangle.Coordinate>>()
    if (isInBounds(x, y - 1)) {
        result[Side.North] = this[x, y - 1] to Rectangle.Coordinate(x, y - 1)
    }
    if (isInBounds(x + 1, y)) {
        result[Side.East] = this[x + 1, y] to Rectangle.Coordinate(x + 1, y)
    }
    if (isInBounds(x, y + 1)) {
        result[Side.South] = this[x, y + 1] to Rectangle.Coordinate(x, y + 1)
    }
    if (isInBounds(x - 1, y)) {
        result[Side.West] = this[x - 1, y] to Rectangle.Coordinate(x - 1, y)
    }
    return result
}

fun <T> Rectangle<T>.neighbours(coordinate: Rectangle.Coordinate): Map<Side, Pair<T, Rectangle.Coordinate>> =
    neighbours(coordinate.x, coordinate.y)

fun Rectangle.Coordinate.direction(to: Rectangle.Coordinate): Side {
    return if (to.x < x) {
        Side.West
    } else if (to.x > x) {
        Side.East
    } else if (to.y < y) {
        Side.North
    } else {
        Side.South
    }
}

fun Rectangle.Coordinate.neighbours(): Map<Side, Rectangle.Coordinate> {
    val result = mutableMapOf<Side, Rectangle.Coordinate>()
    result[Side.North] = Rectangle.Coordinate(x, y - 1)
    result[Side.East] = Rectangle.Coordinate(x + 1, y)
    result[Side.South] = Rectangle.Coordinate(x, y + 1)
    result[Side.West] = Rectangle.Coordinate(x - 1, y)
    return result
}

fun Rectangle.Coordinate.neighbour(inDirection: Side): Rectangle.Coordinate {
    return when (inDirection) {
        Side.North -> Rectangle.Coordinate(x, y - 1)
        Side.East -> Rectangle.Coordinate(x + 1, y)
        Side.South -> Rectangle.Coordinate(x, y + 1)
        Side.West -> Rectangle.Coordinate(x - 1, y)
    }
}