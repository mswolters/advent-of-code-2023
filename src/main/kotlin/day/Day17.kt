package day

import Path
import Rectangle
import Side
import bottomRightCoordinate
import direction
import findPath
import neighbours
import toMutableRectangle
import toRectangle
import topLeftCoordinate

object Day17 : Day {
    override fun part1(input: List<String>): Result {
        val city = input.map { line -> line.map { it.digitToInt() } }.toRectangle()
        val startCoordinate = city.topLeftCoordinate()
        val endCoordinate = city.bottomRightCoordinate()
        val path = findPath(
            PathNode(startCoordinate, Side.North, 0),
            { node -> node.coordinate == endCoordinate },
            { node, pathToNode ->
                /*println(pathToString(city, pathToNode))
                println()*/

                val directions = toDirections(pathToNode.nodes)
                val removeDirections = if (node.numDirection == 3) setOf(directions.last()) else emptySet()
                val lastDirection = if (directions.any()) setOf(directions.last().opposite()) else emptySet()
                val neighbourCoordinates = city.neighbours(node.coordinate).minus(lastDirection).minus(removeDirections)

                neighbourCoordinates.values.map { (heatLoss, coordinate) ->
                    val neighbourDirection = node.coordinate.direction(coordinate)
                    val numDirection =
                        if (lastDirection.isNotEmpty() && lastDirection.last().opposite() == neighbourDirection) node.numDirection + 1 else 1
                    PathNode(coordinate, neighbourDirection, numDirection) to heatLoss.toDouble()
                }
            })/* {
            sqrt(((endCoordinate.x - it.coordinate.x) shl 1).toDouble() + ((endCoordinate.y - it.coordinate.y) shl 1).toDouble()) / 20
        }*/

        return path.nodes.drop(1).sumOf { city[it.coordinate] }.asSuccess()
    }

    fun toDirections(path: List<PathNode>): List<Side> =
        path.zipWithNext { first, second -> first.coordinate.direction(second.coordinate) }

    fun pathToString(city: Rectangle<Int>, path: Path<PathNode>): String {
        val pathMap = city.rows().map { row -> row.map { it.toString() } }.toMutableRectangle()
        path.nodes.zipWithNext { first, second -> second.coordinate to first.coordinate.direction(second.coordinate) }
            .forEach { (coordinate, side) ->
                pathMap[coordinate] = when (side) {
                    Side.North -> "^"
                    Side.East -> ">"
                    Side.South -> "v"
                    Side.West -> "<"
                }
            }
        return pathMap.rows().joinToString("\n") { row -> row.joinToString("") }
    }

    data class PathNode(val coordinate: Rectangle.Coordinate, val direction: Side, val numDirection: Int)

    override fun part2(input: List<String>): Result {
        val city = input.map { line -> line.map { it.digitToInt() } }.toRectangle()
        val startCoordinate = city.topLeftCoordinate()
        val endCoordinate = city.bottomRightCoordinate()
        val path = findPath(
            PathNode(startCoordinate, Side.North, 0),
            { node -> node.coordinate == endCoordinate && node.numDirection >= 4 },
            { node, pathToNode ->

                val directions = toDirections(pathToNode.nodes)
                val removeDirections = if (node.numDirection >= 10) setOf(directions.last()) else if (node.numDirection < 4 && directions.isNotEmpty()) directions.last().perpendicular().toSet() else emptySet()
                val cameFromDirection = if (directions.any()) setOf(directions.last().opposite()) else emptySet()
                val neighbourCoordinates = city.neighbours(node.coordinate).minus(cameFromDirection).minus(removeDirections)

                neighbourCoordinates.values.map { (heatLoss, coordinate) ->
                    val neighbourDirection = node.coordinate.direction(coordinate)
                    val numDirection =
                        if (cameFromDirection.isNotEmpty() && cameFromDirection.last().opposite() == neighbourDirection) node.numDirection + 1 else 1
                    PathNode(coordinate, neighbourDirection, numDirection) to heatLoss.toDouble()
                }
            })

        return path.nodes.drop(1).sumOf { city[it.coordinate] }.asSuccess()
    }

    override fun testData(): Day.TestData {
        return Day.TestData(
            102,
            71,
            """2413432311323
3215453535623
3255245654254
3446585845452
4546657867536
1438598798454
4457876987766
3637877979653
4654967986887
4564679986453
1224686865563
2546548887735
4322674655533""".lines(),
            """111111111111
999999999991
999999999991
999999999991
999999999991"""
                .lines()
        )
    }
}