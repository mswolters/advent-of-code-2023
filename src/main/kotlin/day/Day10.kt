package day

object Day10 : Day {
    override fun part1(input: List<String>): Result {
        val map =
            fixStart(input.mapIndexed { y, line ->
                line.mapIndexed { x, it ->
                    PipeSection(
                        x,
                        y,
                        charToConnectedSides(it),
                        it
                    )
                }
            })
        val distances = calculateDistanceFromStart(map)
        return distances.flatten().filter { it != Int.MAX_VALUE }.max().asSuccess()
    }

    fun fixStart(map: List<List<PipeSection>>): List<List<PipeSection>> {
        val mutableMap = map.map { it.toMutableList() }.toMutableList()
        val start = map.flatten().first { it.char == 'S' }

        val startSections = start.connectedSections(map).filter { it.connectedSections(map).contains(start) }
        val startConnectedSides = mutableSetOf<Side>()
        startSections.forEach {
            when {
                it.x < start.x -> startConnectedSides.add(Side.West)
                it.y < start.y -> startConnectedSides.add(Side.North)
                it.x > start.x -> startConnectedSides.add(Side.East)
                it.y > start.y -> startConnectedSides.add(Side.South)
            }
        }
        mutableMap[start.y][start.x] = start.copy(connectedSides = startConnectedSides)
        return mutableMap
    }

    override fun part2(input: List<String>): Result {
        val map =
            fixStart(input.mapIndexed { y, line ->
                line.mapIndexed { x, it ->
                    PipeSection(
                        x,
                        y,
                        charToConnectedSides(it),
                        it
                    )
                }
            })
        val distances = calculateDistanceFromStart(map)
        //Keep only the original loop
        val onlyLoopMap = map.map { line ->
            line.map {
                if (distances[it.y][it.x] == Int.MAX_VALUE) PipeSection(
                    it.x,
                    it.y,
                    emptySet(),
                    '.'
                ) else it
            }
        }
        /*val insideMap = onlyLoopMap.map { it.toMutableList() }.toMutableList()
        var insideCount = 0
        onlyLoopMap.forEachIndexed { y, line ->
            var isInside = false
            line.forEachIndexed { x, it -> if (it.connectedSides.contains(Side.North)) isInside = !isInside
            if (isInside && it.char == '.') {
                insideCount++
            } else if (!isInside && it.char == '.') {
                insideMap[y][x] = insideMap[y][x].copy(char = 'O')
            }}
        }*/
        // Blow it up by 2x so gaps between pipes appear
        val enlargedLoopMap = enlargeLoopMap(onlyLoopMap)
        val fillMap = enlargedLoopMap.map { it.toMutableList() }.toMutableList()
        val xSize = fillMap[0].size
        val ySize = fillMap.size
        for (x in 0..<xSize) {
            for (y in 0..<ySize) {
                if (x == 0 || y == 0) {
                    // Flood fill from the outside edges
                    floodFill(fillMap, x, y)
                }
            }
        }

        val shrunkenMap =
            fillMap.filterIndexed { y, _ -> y % 2 == 0 }.map { line -> line.filterIndexed { x, _ -> x % 2 == 0 } }

        /*println(mapToString(fillMap))
        println()
        println(mapToString(shrunkenMap))
        println()
        println(mapToString(insideMap))

        //return insideCount.asSuccess()*/
        return shrunkenMap.flatten().count { it.char == '.' }.asSuccess()
    }

    fun mapToString(map: List<List<PipeSection>>): String {
        return map.map { line -> line.map { it.char }.joinToString(separator = "") }
            .mapIndexed { y, line -> "$y: $line" }.joinToString("\n")
    }

    private fun floodFill(fillMap: MutableList<MutableList<PipeSection>>, x: Int, y: Int) {
        val section = fillMap[y][x]
        if (section.char == '.') {
            fillMap[y][x] = section.copy(char = 'O')
            section.neighbours(fillMap).forEach { floodFill(fillMap, it.x, it.y) }
        }
    }

    fun calculateDistanceFromStart(map: List<List<PipeSection>>): List<List<Int>> {
        val distances = MutableList(map.size) { MutableList(map[0].size) { Int.MAX_VALUE } }
        val start = map.flatten().first { it.char == 'S' }
        val startSections = start.connectedSections(map)
        distances[start.y][start.x] = 0
        var currentSections = startSections
        var currentDistance = 1
        while (currentSections.isNotEmpty()) {
            currentSections.forEach { distances[it.y][it.x] = currentDistance }
            currentDistance++;
            currentSections = currentSections.map { curr ->
                curr.connectedSections(map).filter { distances[it.y][it.x] == Int.MAX_VALUE }
            }.flatten()
        }
        return distances
    }

    fun enlargeLoopMap(loopMap: List<List<PipeSection>>): List<List<PipeSection>> {
        return List(loopMap.size * 2) { y ->
            List(loopMap[0].size * 2) { x ->
                enlargePipeSection(x, y, loopMap[y / 2][x / 2])
            }
        }
    }

    fun enlargePipeSection(newX: Int, newY: Int, pipeSection: PipeSection): PipeSection {
        val newConnectedSides: Set<Side> =
            if (newX % 2 == 0 && newY % 2 == 0) {
                return pipeSection.copy(x = newX, y = newY)
            } else if (newX % 2 == 0 && pipeSection.connectedSides.contains(Side.South)) {
                setOf(Side.North, Side.South)
            } else if (newX % 2 != 0 && newY % 2 == 0 && pipeSection.connectedSides.contains(Side.East)) {
                setOf(Side.East, Side.West)
            } else {
                emptySet()
            }
        return PipeSection(newX, newY, newConnectedSides, if (newConnectedSides.isEmpty()) '.' else 'X')
    }

    data class PipeSection(val x: Int, val y: Int, val connectedSides: Set<Side>, val char: Char) {
        fun connectedSections(map: List<List<PipeSection>>): List<PipeSection> {
            val northSide = if (!connectedSides.contains(Side.North) || y == 0) null else map[y - 1][x]
            val eastSide = if (!connectedSides.contains(Side.East) || x == map[0].size - 1) null else map[y][x + 1]
            val southSide = if (!connectedSides.contains(Side.South) || y == map.size - 1) null else map[y + 1][x]
            val westSide = if (!connectedSides.contains(Side.West) || x == 0) null else map[y][x - 1]

            return listOfNotNull(northSide, eastSide, southSide, westSide)
        }

        fun neighbours(map: List<List<PipeSection>>): List<PipeSection> {
            val northSide = if (y == 0) null else map[y - 1][x]
            val eastSide = if (x == map[0].size - 1) null else map[y][x + 1]
            val southSide = if (y == map.size - 1) null else map[y + 1][x]
            val westSide = if (x == 0) null else map[y][x - 1]
            return listOfNotNull(northSide, eastSide, southSide, westSide)
        }
    }

    enum class Side {
        North, East, South, West
    }

    fun charToConnectedSides(char: Char): Set<Side> {
        return when (char) {
            '|' -> setOf(Side.North, Side.South)
            '-' -> setOf(Side.East, Side.West)
            'L' -> setOf(Side.North, Side.East)
            'J' -> setOf(Side.North, Side.West)
            '7' -> setOf(Side.South, Side.West)
            'F' -> setOf(Side.South, Side.East)
            '.' -> emptySet()
            'S' -> setOf(Side.North, Side.East, Side.South, Side.West)
            else -> throw IllegalArgumentException("Unknown char: $char")
        }
    }

    override fun testData(): Day.TestData {
        return Day.TestData(
            8,
            10,
            """7-F7-
.FJ|7
SJLL7
|F--J
LJ.LJ"""
                .lines(),
            """FF7FSF7F7F7F7F7F---7
L|LJ||||||||||||F--J
FL-7LJLJ||||||LJL-77
F--JF--7||LJLJ7F7FJ-
L---JF-JLJ.||-FJLJJ7
|F|F-JF---7F7-L7L|7|
|FFJF7L7F-JF7|JL---7
7-L-JL7||F7|L7F-7F7|
L.L7LFJ|||||FJL7||LJ
L7JLJL-JLJLJL--JLJ.L""".lines()
        )
    }
}