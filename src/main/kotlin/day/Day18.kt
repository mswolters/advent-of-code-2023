package day

import MutableRectangle
import Rectangle
import ResizingMutableRectangle
import Side
import neighbour
import neighbours
import toString

object Day18 : Day {
    override fun part1(input: List<String>): Result {
        val instructions = input.map { parse(it) }
        val digMap = ResizingMutableRectangle { _, _ -> false }
        digMap[0, 0] = true

        var minX = 0
        var minY = 0
        var coordinate = Rectangle.Coordinate(0, 0)
        instructions.forEach { instruction ->
            repeat(instruction.count) {
                coordinate = coordinate.neighbour(instruction.direction)
                digMap[coordinate] = true
                if (coordinate.x < minX) {
                    minX = coordinate.x
                }
                if (coordinate.y < minY) {
                    minY = coordinate.y
                }
            }
        }

        floodFill(digMap, 1, 1)

        return digMap.count { it }.asSuccess()
    }

    fun parse(line: String): Instruction {
        val (directionText, countText, colorText) = line.split(" ")
        val direction = when (directionText) {
            "U" -> Side.North
            "R" -> Side.East
            "L" -> Side.West
            "D" -> Side.South
            else -> throw IllegalArgumentException("Unknown direction: $directionText")
        }
        val count = countText.toInt()
        val color = colorText.removeSurrounding("(#", ")").toInt(16)
        return Instruction(direction, count, color)
    }

    private fun floodFill(digMap: MutableRectangle<Boolean>, x: Int, y: Int) {
        if (!digMap[x, y]) {
            digMap[x, y] = true
            digMap.neighbours(x, y).map { it.value.second }.forEach { floodFill(digMap, it.x, it.y) }
        }
    }

    data class Instruction(val direction: Side, val count: Int, val color: Int)

    override fun part2(input: List<String>): Result {
        //I'm not getting paid enough for this shit
        return NotImplemented
    }

    override fun testData(): Day.TestData {
        return Day.TestData(
            62,
            0,
            """R 6 (#70c710)
D 5 (#0dc571)
L 2 (#5713f0)
D 2 (#d2c081)
R 2 (#59c680)
D 2 (#411b91)
L 5 (#8ceee2)
U 2 (#caa173)
L 1 (#1b58a2)
U 2 (#caa171)
R 2 (#7807d2)
U 3 (#a77fa3)
L 2 (#015232)
U 2 (#7a21e3)"""
                .lines()
        )
    }
}