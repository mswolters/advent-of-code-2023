package day

import PathData
import findPath
import neighbours
import toRectangle

object Day21 : Day {
    override fun part1(input: List<String>): Result {
        val map = input.map { line -> line.map { when (it){
            'S' -> FloorContent.Start
            '.' -> FloorContent.Plot
            '#' -> FloorContent.Rock
            else -> throw Exception("Unknown character $it")
        } } }.toRectangle()
        val (startCoordinate, _) = map.indexedIterator().asSequence().first { (_, content)-> content == FloorContent.Start }

        // Any node that can be reached in N steps can also be reached in N+2 steps, since we can just go back
        // Because we can't move diagonally, a node that can be reached in N steps can _never_ be reached in kN+1 steps
        // Build up a set of all nodes that can be reached in 64 steps, then filter out any node that took an even number of steps
        val pathData = PathData<Rectangle.Coordinate>()
        findPath(startCoordinate, {_, pathToNode -> pathToNode.nodes.size == 65 }, pathData) { node, _ ->
            map.neighbours(node).values.filter { (floorContent, _) -> floorContent != FloorContent.Rock }.map { it.second }.map { it to 1.0 }
        }
        return pathData.visitedNodes.filterValues { path -> path.nodes.size % 2 == 1 }.count().asSuccess()
    }

    enum class FloorContent{
        Start, Plot, Rock
    }

    override fun part2(input: List<String>): Result {
        return NotImplemented
    }

    override fun testData(): Day.TestData {
        return Day.TestData(
            42,
            0,
            """...........
.....###.#.
.###.##..#.
..#.#...#..
....#.#....
.##..S####.
.##..#...#.
.......##..
.##.#.####.
.##..##.##.
..........."""
                .lines()
        )
    }
}