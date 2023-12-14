package day

import MutableRectangle
import Rectangle
import toMutableRectangle
import toRectangle

object Day14 : Day {
    override fun part1(input: List<String>): Result {
        val map = input.map { line -> line.map { charToFloorContent(it) } }.toRectangle()
        return shiftRocks(map.columns()).map { column ->
            column.mapIndexed { index, floorContent ->
                if (floorContent == FloorContent.RoundRock) column.size - index else 0
            }
        }
            .sumOf { it.sum() }
            .asSuccess()
    }

    override fun part2(input: List<String>): Result {
        val map = input.map { line -> line.map { charToFloorContent(it) } }.toRectangle()

        var cycledMap = map
        val seenConfigurations = mutableMapOf<Rectangle<FloorContent>, Int>()

        val requestedCycles = 1_000_000_000

        var numberOfCyclesBeforeLoop = Int.MAX_VALUE
        var loopStartIndex = 0
        for (i in (0..<requestedCycles)) {
            if (seenConfigurations.containsKey(cycledMap)) {
                println("Loop detected $i")
                numberOfCyclesBeforeLoop = i
                loopStartIndex = seenConfigurations[cycledMap]!!
                break
            } else {
                seenConfigurations[cycledMap] = i
            }
            cycledMap = cycleRocks(cycledMap)
        }

        val loopLength = numberOfCyclesBeforeLoop - loopStartIndex
        val extraInLoop = (requestedCycles - loopStartIndex) % loopLength
        val foundMap = seenConfigurations.filter { (_, value) -> value == loopStartIndex + extraInLoop }.keys.first()

        return foundMap.columns().map { column ->
            column.mapIndexed { index, floorContent ->
                if (floorContent == FloorContent.RoundRock) column.size - index else 0
            }
        }
            .sumOf { it.sum() }
            .asSuccess()
    }

    fun shiftRocks(columns: List<List<FloorContent>>): List<List<FloorContent>> {
        val mutableColumns = columns.map { it.toMutableList() }
        for (column in mutableColumns) {
            var emptySquaresCount = 0
            for (index in column.indices) {
                val content = column[index]
                when (content) {
                    FloorContent.RoundRock -> {
                        column.swap(index, index - emptySquaresCount)
                    }
                    FloorContent.CubeRock -> emptySquaresCount = 0
                    FloorContent.Empty -> emptySquaresCount++
                }
            }
        }
        return mutableColumns
    }

    private fun <T> MutableList<T>.swap(indexA: Int, indexB: Int) {
        val temp = this[indexA]
        this[indexA] = this[indexB]
        this[indexB] = temp
    }

    fun cycleRocks(map: Rectangle<FloorContent>): Rectangle<FloorContent> {
        // North, West, South, East
        val mutableMap = map.toMutableRectangle()
        for (x in 0..<mutableMap.width) {
            var emptySquaresCount = 0
            for (y in 0..<mutableMap.height) {
                val content = mutableMap[x, y]
                when (content) {
                    FloorContent.RoundRock -> {
                        mutableMap.swap(Rectangle.Coordinate(x, y), Rectangle.Coordinate(x, y - emptySquaresCount))
                    }
                    FloorContent.CubeRock -> emptySquaresCount = 0
                    FloorContent.Empty -> emptySquaresCount++
                }
            }
        }
        for (y in 0..<mutableMap.height) {
            var emptySquaresCount = 0
            for (x in 0..<mutableMap.width) {
                val content = mutableMap[x, y]
                when (content) {
                    FloorContent.RoundRock -> {
                        mutableMap.swap(Rectangle.Coordinate(x, y), Rectangle.Coordinate(x - emptySquaresCount, y))
                    }
                    FloorContent.CubeRock -> emptySquaresCount = 0
                    FloorContent.Empty -> emptySquaresCount++
                }
            }
        }
        for (x in 0..<mutableMap.width) {
            var emptySquaresCount = 0
            for (y in (0..<mutableMap.height).reversed()) {
                val content = mutableMap[x, y]
                when (content) {
                    FloorContent.RoundRock -> {
                        mutableMap.swap(Rectangle.Coordinate(x, y), Rectangle.Coordinate(x, y + emptySquaresCount))
                    }
                    FloorContent.CubeRock -> emptySquaresCount = 0
                    FloorContent.Empty -> emptySquaresCount++
                }
            }
        }
        for (y in 0..<mutableMap.height) {
            var emptySquaresCount = 0
            for (x in (0..<mutableMap.width).reversed()) {
                val content = mutableMap[x, y]
                when (content) {
                    FloorContent.RoundRock -> {
                        mutableMap.swap(Rectangle.Coordinate(x, y), Rectangle.Coordinate(x + emptySquaresCount, y))
                    }
                    FloorContent.CubeRock -> emptySquaresCount = 0
                    FloorContent.Empty -> emptySquaresCount++
                }
            }
        }
        return mutableMap
    }

    private fun <T> MutableRectangle<T>.swap(indexA: Rectangle.Coordinate, indexB: Rectangle.Coordinate) {
        val temp = this[indexA]
        this[indexA] = this[indexB]
        this[indexB] = temp
    }
    enum class FloorContent {
        RoundRock,
        CubeRock,
        Empty
    }

    fun Rectangle<FloorContent>.stringify(): String {
        return rows().joinToString("\n") { row ->
            row.joinToString("") {
                when (it) {
                    FloorContent.RoundRock -> "O"
                    FloorContent.CubeRock -> "#"
                    FloorContent.Empty -> "."
                }
            }
        }
    }


    fun charToFloorContent(char: Char): FloorContent {
        return when (char) {
            '.' -> FloorContent.Empty
            'O' -> FloorContent.RoundRock
            '#' -> FloorContent.CubeRock
            else -> throw IllegalArgumentException("Unknown char: $char")
        }
    }

    override fun testData(): Day.TestData {
        return Day.TestData(
            136,
            64,
            """O....#....
O.OO#....#
.....##...
OO.#O....O
.O.....O#.
O.#..O.#.#
..O..#O..O
.......O..
#....###..
#OO..#...."""
                .lines()
        )
    }
}