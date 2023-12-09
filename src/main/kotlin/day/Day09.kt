package day

import asInts

object Day09 : Day {
    override fun part1(input: List<String>): Result {
        return input.map { it.split(' ').asInts() }
            .sumOf { findNext(it) }
            .asSuccess()
    }

    override fun part2(input: List<String>): Result {
        return input.map { it.split(' ').asInts() }
            .sumOf { findBefore(it) }
            .asSuccess()
    }

    private fun findNext(list: List<Int>): Int {
        return findNext(list, differences(list)).last()
    }

    private fun findBefore(list: List<Int>): Int {
        return findNext(list, differences(list)).first()
    }

    private fun findNext(list: List<Int>, differences: List<Int>): List<Int> {
        if (differences.all { it == 0 }) {
            return listOf(list.first()) + list + list.last()
        }
        val nextDifferences = findNext(differences, differences(differences))
        return listOf(list.first() - nextDifferences.first()) + list + (list.last() + nextDifferences.last())
    }

    private fun differences(list: List<Int>): List<Int> {
        return list.windowed(2) { (left, right) -> right - left }
    }

    override fun testData(): Day.TestData {
        return Day.TestData(
            114,
            2,
            """0 3 6 9 12 15
1 3 6 10 15 21
10 13 16 21 30 45"""
                .lines()
        )
    }
}