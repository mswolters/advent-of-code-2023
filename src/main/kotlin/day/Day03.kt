package day

object Day03 : Day {
    override fun part1(input: List<String>): Result {
        val schematic = input.mapIndexed(::parseLine)

        return schematic.asSequence()
            .flatten()
            .filterIsInstance<Number>()
            .filter { touchesSymbol(schematic, it) }
            .map { it.value }
            .sum()
            .asSuccess()
    }

    override fun part2(input: List<String>): Result {
        val schematic = input.mapIndexed(::parseLine)

        return schematic.asSequence()
            .flatten()
            .filterIsInstance<Symbol>()
            .filter { it.char == '*' }
            .map { neighbours(schematic, it) }
            .map { dereferencePartialNumbers(it) }
            .map { neighbours -> neighbours.filterIsInstance<Number>() }
            .filter { it.size == 2 }
            .map { it[0].value * it[1].value }
            .sum()
            .asSuccess()
    }

    private fun dereferencePartialNumbers(neighbours: List<Data>):List<Data> {
        return neighbours.map { if (it is PartialNumber) it.start else it }.distinct()
    }

    fun touchesSymbol(schematic: List<List<Data>>, item: Data): Boolean {
        return neighbours(schematic, item).any { it is Symbol }
    }

    fun neighbours(schematic: List<List<Data>>, item: Data): List<Data> {
        val result = mutableListOf<Data>()
        for (neighbourY in item.y - 1..item.y + 1) {
            val length = when (item) {
                is Number -> item.length
                is PartialNumber -> TODO()
                is Period -> 1
                is Symbol -> 1
            }
            for (neighbourX in item.x - 1..item.x + length) {
                schematic.get(neighbourX, neighbourY)?.let { result.add(it) }
            }
        }
        return result
    }

    private fun List<List<Data>>.get(x: Int, y: Int): Data? {
        if (y >= 0 && y < this.size) {
            val line = get(y)
            if (x >= 0 && x < line.size) {
                return line[x]
            }
        }
        return null
    }

    fun parseLine(y: Int, line: String): List<Data> {
        val result = mutableListOf<Data>()
        var partialNumber = ""
        for ((x, c) in line.withIndex()) {
            if (c.isDigit()) {
                partialNumber += c
            } else {
                if (partialNumber.isNotEmpty()) {
                    val fullNumber = Number(x - partialNumber.length, y, partialNumber.toInt(), partialNumber.length)
                    result.add(fullNumber)
                    for (index in 1..<partialNumber.length) {
                        result.add(PartialNumber(x - partialNumber.length + index, y, fullNumber, index))
                    }
                }
                partialNumber = ""
            }
            if (c == '.') {
                result.add(Period(x, y))
            } else if (!c.isDigit()) {
                result.add(Symbol(x, y, c))
            }
        }
        if (partialNumber.isNotEmpty()) {
            val fullNumber = Number(line.length - partialNumber.length, y, partialNumber.toInt(), partialNumber.length)
            result.add(fullNumber)
            for (index in 1..<partialNumber.length) {
                result.add(PartialNumber(line.length - partialNumber.length + index, y, fullNumber, index))
            }
        }
        return result
    }

    sealed interface Data {
        val x: Int
        val y: Int
    }

    data class Number(override val x: Int, override val y: Int, val value: Int, val length: Int) : Data
    data class PartialNumber(override val x: Int, override val y: Int, val start: Number, val index: Int) : Data
    data class Period(override val x: Int, override val y: Int) : Data
    data class Symbol(override val x: Int, override val y: Int, val char: Char) : Data

    override fun testData(): Day.TestData {
        return Day.TestData(
            4361,
            467835,
            """467..114..
...*......
..35..633.
......#...
617*......
.....+.58.
..592.....
......755.
...${'$'}.*....
.664.598.."""
                .lines()
        )
    }
}