package day

import Rectangle
import split
import toRectangle
import kotlin.math.min

object Day13 : Day {
    override fun part1(input: List<String>): Result {
        val mirrorResults = input
            .split("")
            .map { map ->
                map.map { line -> line.map { contentFor(it) } }
                    .toRectangle()
            }.map { checkMirror(it) }
        return mirrorResults
            .map { it.score }
            .sum()
            .asSuccess()
    }

    override fun part2(input: List<String>): Result {
        val maps = input
            .split("")
            .map { map ->
                map.map { line -> line.map { contentFor(it) } }
                    .toRectangle()
            }
        val smudgeResults = maps.map { checkSingleSmudge(it) }
        return smudgeResults
            .map { it.score }
            .sum()
            .asSuccess()
    }

    fun checkMirror(map: Rectangle<FloorContent>): MirrorResult {
        return MirrorResult(map, checkMirror(map.columns()), checkMirror(map.rows()))
    }

    fun checkMirror(map: List<List<FloorContent>>): MirrorLine {
        for (index in map.indices) {
            if (mirrorsAlong(index, map)) {
                return Reflection(index)
            }
        }
        return NoLine
    }

    fun mirrorsAlong(index: Int, map: List<List<FloorContent>>): Boolean {
        if (index == map.size - 1) {
            return false
        }
        for (i in 0..min(index, map.size - index - 2)) {
            if (map[index - i] != map[index + i + 1]) {
                return false
            }
        }
        return true
    }

    fun checkSingleSmudge(map: Rectangle<FloorContent>): MirrorResult {
        return MirrorResult(map, checkSingleSmudge(map.columns()), checkSingleSmudge(map.rows()))
    }
    fun checkSingleSmudge(map: List<List<FloorContent>>): MirrorLine {
        for (index in map.indices) {
            if (diffCountForMirrorAlong(index, map) == 1) {
                return Reflection(index)
            }
        }
        return NoLine
    }

    fun diffCountForMirrorAlong(index: Int, map: List<List<FloorContent>>): Int {
        if (index == map.size - 1) {
            return Int.MAX_VALUE
        }
        var diffCount = 0
        for (i in 0..min(index, map.size - index - 2)) {
            val leftSide = map[index - i]
            val rightSide = map[index + i + 1]
            diffCount += leftSide.zip(rightSide)
                .count { (left, right) -> left != right }
        }
        return diffCount
    }

    data class MirrorResult(val map: Rectangle<FloorContent>, val vertical: MirrorLine, val horizontal: MirrorLine) {
        val score: Int
            get() = vertical.score + horizontal.score * 100
    }

    sealed interface MirrorLine {
        val score: Int
            get() = when (this) {
                NoLine -> 0
                is Reflection -> this.position + 1
            }

    }

    data object NoLine : MirrorLine
    data class Reflection(val position: Int) : MirrorLine

    enum class FloorContent {
        Ash, Rock
    }

    fun contentFor(char: Char): FloorContent {
        return when (char) {
            '.' -> FloorContent.Ash
            '#' -> FloorContent.Rock
            else -> throw IllegalArgumentException("Unknown floor content: $char")
        }
    }

    override fun testData(): Day.TestData {
        return Day.TestData(
            405,
            400,
            """#.##..##.
..#.##.#.
##......#
##......#
..#.##.#.
..##..##.
#.#.##.#.

#...##..#
#....#..#
..##..###
#####.##.
#####.##.
..##..###
#....#..#"""
                .lines()
        )
    }
}