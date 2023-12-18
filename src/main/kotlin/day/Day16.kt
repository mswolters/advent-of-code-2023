package day

import Rectangle
import Side
import coordinateNextTo
import edges
import toRectangle
import java.util.*

object Day16 : Day {
    override fun part1(input: List<String>): Result {
        val layout = input.map { line -> line.toCharArray().toList() }.toRectangle()
        val beamPath: Rectangle<MutableSet<Side>> =
            Rectangle(layout.width, layout.height) { _, _ -> EnumSet.noneOf(Side::class.java) }

        walk(layout, beamPath, Rectangle.Coordinate(0, 0), Side.East)

        return beamPath.count { it.isNotEmpty() }.asSuccess()
    }

    override fun part2(input: List<String>): Result {
        val layout = input.map { line -> line.toCharArray().toList() }.toRectangle()

        val numberPerSide = layout.edges().mapValues { (coordinate, direction) ->
            val beamPath: Rectangle<MutableSet<Side>> =
                Rectangle(layout.width, layout.height) { _, _ -> EnumSet.noneOf(Side::class.java) }
            walk(layout, beamPath, coordinate, direction.opposite())
            beamPath.count { it.isNotEmpty()}
        }

        return numberPerSide.values.max().asSuccess()
    }

    fun walk(
        layout: Rectangle<Char>,
        beamPath: Rectangle<MutableSet<Side>>,
        position: Rectangle.Coordinate,
        direction: Side
    ) {
        if (!layout.isInBounds(position)) return

        when (val char = layout[position.x, position.y]) {
            '.' -> {
                continueBeam(layout, beamPath, position, direction)
            }

            '/', '\\' -> {
                val newDirection = when (direction) {
                    Side.North -> Side.East
                    Side.East -> Side.North
                    Side.South -> Side.West
                    Side.West -> Side.South
                }.let { if (char == '/') it else it.opposite() }
                continueBeam(layout, beamPath, position, newDirection)
            }

            '|' -> {
                if (direction == Side.North || direction == Side.South) {
                    continueBeam(layout, beamPath, position, direction)
                } else {
                    val perpendicular = direction.perpendicular()
                    continueBeam(layout, beamPath, position, perpendicular.first())
                    continueBeam(layout, beamPath, position, perpendicular.last())
                }
            }

            '-' -> {
                if (direction == Side.East || direction == Side.West) {
                    continueBeam(layout, beamPath, position, direction)
                } else {
                    val perpendicular = direction.perpendicular()
                    continueBeam(layout, beamPath, position, perpendicular.first())
                    continueBeam(layout, beamPath, position, perpendicular.last())
                }
            }
        }
    }

    private inline fun continueBeam(
        layout: Rectangle<Char>,
        beamPath: Rectangle<MutableSet<Side>>,
        position: Rectangle.Coordinate,
        direction: Side
    ) {
        if (beamPath[position].contains(direction)) return
        beamPath[position].add(direction)
        walk(layout, beamPath, direction.coordinateNextTo(position), direction)
    }

    override fun testData(): Day.TestData {
        return Day.TestData(
            46,
            51,
            """.|...\....
|.-.\.....
.....|-...
........|.
..........
.........\
..../.\\..
.-.-/..|..
.|....-|.\
..//.|...."""
                .lines()
        )
    }
}