class Rectangle<T>(val width: Int, val height: Int, init: (Int, Int) -> T) : Collection<T> {

    private val content = List(width * height) { index -> init(index % width, index / width) }

    operator fun get(x: Int, y: Int): T = content[x + y * width]
    override val size: Int
        get() = width * height

    override fun isEmpty(): Boolean = width == 0 && height == 0

    override fun iterator(): Iterator<T> = content.iterator()

    override fun containsAll(elements: Collection<T>): Boolean = content.containsAll(elements)

    override fun contains(element: T): Boolean = content.contains(element)

    fun rows(): List<List<T>> = if (content.isNotEmpty()) content.chunked(width) else emptyList()

    fun columns(): List<List<T>> = if (content.isNotEmpty()) content.byNth(width) else emptyList()

    fun transpose(): Rectangle<T> = Rectangle(height, width) { x, y -> this[y, x] }

    fun toIndexedList(): List<Pair<Coordinate, T>> =
        content.mapIndexed { index, it -> Coordinate(index % width, index / width) to it }

    data class Coordinate(val x: Int, val y: Int)

}

fun <T> List<List<T>>.transpose(): List<List<T>> = List(this[0].size) { y -> List(this.size) { x -> this[x][y] } }


fun <T> List<List<T>>.toRectangle(): Rectangle<T> {
    val width = firstOrNull()?.size ?: 0
    require(all { it.size == width }) { "All elements should have the same size" }

    return Rectangle(width, this.size) { x, y -> this[y][x] }
}

enum class Side {
    North, East, South, West
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