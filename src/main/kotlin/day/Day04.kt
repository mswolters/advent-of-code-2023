package day

import kotlin.math.pow

object Day04 : Day {
    override fun part1(input: List<String>): Result {
        return input.asSequence().map { parseCard(it) }
            .map { (cardNumbers, myNumbers) -> cardNumbers.intersect(myNumbers.toSet()) }
            .map { it.count() }
            .map { if (it == 0) 0 else 2.0.pow(it - 1).toLong() }
            .sum()
            .asSuccess()
    }

    override fun part2(input: List<String>): Result {
        val cards = input.map { parseCard(it) }
        val counts = List(cards.count()) { 1L }.toMutableList()
        cards.map { (cardNumbers, myNumbers) -> cardNumbers.intersect(myNumbers.toSet()).count() }
            .forEachIndexed { index, count ->
                for (windex in index + 1..index + count) {
                    counts[windex] += counts[index]
                }
            }
        return counts.sum().asSuccess()
    }

    fun parseCard(line: String): Pair<List<Int>, List<Int>> {
        val split = line.split(':', '|').map { it.trim() }
        return parseInts(split[1]) to parseInts(split[2])
    }

    fun parseInts(ints: String): List<Int> {
        return ints.split(' ').filter { it.isNotEmpty() }.map { it.toInt() }
    }

    override fun testData(): Day.TestData {
        return Day.TestData(
            13,
            30,
            """Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11"""
                .lines()
        )
    }
}