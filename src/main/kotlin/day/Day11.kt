package day

import cartesianProduct
import toRectangle
import kotlin.math.absoluteValue

object Day11 : Day {
    override fun part1(input: List<String>): Result {
        val galaxyMap = input.map { line -> line.map { if (it == '#') Entry.Galaxy else Entry.NoGalaxy } }.toRectangle()
        val expandedRows =
            galaxyMap.rows().map { row -> if (row.none { it == Entry.Galaxy }) listOf(row, row) else listOf(row) }
                .flatten()
                .toRectangle()
        val expandedCols =
            expandedRows.columns().map { col -> if (col.none { it == Entry.Galaxy }) listOf(col, col) else listOf(col) }
                .flatten()
                .reversed()
                .toRectangle()
                .transpose()
        val galaxyCoordinates = expandedCols.toIndexedList().filter { (_, it) -> it == Entry.Galaxy }.map { (coordinate, _) -> coordinate }
        return (galaxyCoordinates.cartesianProduct(galaxyCoordinates).map { (a, b) -> distance(a, b) }.sum() / 2).asSuccess()
    }

    override fun part2(input: List<String>): Result {
        val bonusSize = 1_000_000L - 1L
        val galaxyMap = input.mapIndexed { y, line -> line.mapIndexed { x, it -> if (it == '#') Galaxy(x.toLong(), y.toLong()) else null } }.toRectangle()
        var bonusY = 0L
        galaxyMap.rows().forEach { row ->
            if (row.none { it is Galaxy }) {
                bonusY += bonusSize
            }
            row.filterNotNull().forEach { it.y += bonusY }
        }
        var bonusX = 0L
        galaxyMap.columns().forEach { column ->
            if (column.none { it is Galaxy }) {
                bonusX += bonusSize
            }
            column.filterNotNull().forEach { it.x += bonusX }
        }
        val galaxies = galaxyMap.filterNotNull()
        return (galaxies.cartesianProduct(galaxies).map { (a, b) -> distance(a, b) }.sum() / 2).asSuccess()
    }

    fun distance(a: Rectangle.Coordinate, b: Rectangle.Coordinate): Int {
        return (a.x - b.x).absoluteValue + (a.y - b.y).absoluteValue
    }

    fun distance(a: Galaxy, b: Galaxy): Long {
        return (a.x - b.x).absoluteValue + (a.y - b.y).absoluteValue
    }

    enum class Entry {
        Galaxy,
        NoGalaxy
    }

    data class Galaxy(var x: Long, var y: Long)

    override fun testData(): Day.TestData {
        return Day.TestData(
            374,
            82000210,
            """...#......
.......#..
#.........
..........
......#...
.#........
.........#
..........
.......#..
#...#....."""
                .lines()
        )
    }
}