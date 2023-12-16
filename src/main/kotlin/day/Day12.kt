package day

import Memo2
import asInts
import memoize
import repeat
import split

object Day12 : Day {
    override fun part1(input: List<String>): Result {
        // Brute force works 😇
        val validArrangementCounts = input.map { parse(it) }
            .map { (springs, record) ->
                generatePossibleConditions(springs)
                    .map { isValidRecord(it, record) }
                    .count { it }
            }
        return validArrangementCounts
            .sum()
            .asSuccess()
    }

    fun parse(line: String): Pair<List<SpringCondition>, List<Int>> {
        val (rawConditions, rawNumbers) = line.split(' ')
        return rawConditions.map { it.toSpringCondition() } to rawNumbers.split(',').asInts()
    }

    override fun part2(input: List<String>): Result {
        val validArrangementCounts = input.map { parse(it) }
            .map { (springs, record) -> repeatSpringWithUnknownInBetween(springs) to record.repeat(5) }
            .map { (springs, record) -> part2SolveMemoized(springs, record) }
        return validArrangementCounts.sum().asSuccess()
    }

    fun repeatSpringWithUnknownInBetween(springs: List<SpringCondition>): List<SpringCondition> {
        val ret = mutableListOf<SpringCondition>()
        ret.addAll(springs)
        ret.add(SpringCondition.Unknown)
        ret.addAll(springs)
        ret.add(SpringCondition.Unknown)
        ret.addAll(springs)
        ret.add(SpringCondition.Unknown)
        ret.addAll(springs)
        ret.add(SpringCondition.Unknown)
        ret.addAll(springs)
        return ret
    }

    fun generatePossibleConditions(from: List<SpringCondition>): Sequence<List<SpringCondition>> {
        val unknownSpringCount = from.count { it == SpringCondition.Unknown }
        return sequence {
            for (gen in 0..<1L.shl(unknownSpringCount)) {
                val possibleCondition = mutableListOf<SpringCondition>()
                var bit = 0
                for (condition in from) {
                    if (condition == SpringCondition.Unknown) {
                        val pickedCondition =
                            if ((gen and 1L.shl(bit)) == 0L) SpringCondition.Broken else SpringCondition.Working
                        bit++
                        possibleCondition.add(pickedCondition)
                    } else {
                        possibleCondition.add(condition)
                    }
                }
                yield(possibleCondition)
            }
        }
    }

    fun isValidRecord(springs: List<SpringCondition>, record: List<Int>): Boolean {
        val workingSpringSizes = springs.split(SpringCondition.Broken).map { it.size }.filter { it != 0 }
        return workingSpringSizes == record
    }

    val part2SolveMemoized = Memo2<List<SpringCondition>, List<Int>, Long>::calculateValidConditionsCount.memoize()

    enum class SpringCondition {
        Unknown,
        Working,
        Broken
    }

    fun Char.toSpringCondition(): SpringCondition {
        return when (this) {
            '#' -> SpringCondition.Working
            '.' -> SpringCondition.Broken
            '?' -> SpringCondition.Unknown
            else -> throw IllegalArgumentException("Unknown char: $this")
        }
    }

    override fun testData(): Day.TestData {
        return Day.TestData(
            21,
            525152,
            """???.### 1,1,3
.??..??...?##. 1,1,3
?#?#?#?#?#?#?#? 1,3,1,6
????.#...#... 4,1,1
????.######..#####. 1,6,5
?###???????? 3,2,1"""
                .lines()
        )
    }
}

private fun Memo2<List<Day12.SpringCondition>, List<Int>, Long>.calculateValidConditionsCount(springs: List<Day12.SpringCondition>, record: List<Int>): Long {
    if (springs.isEmpty() && record.isNotEmpty()) {
        return 0L
    }
    if (springs.isEmpty() && record.isEmpty()) {
        return 1L
    }

    return when (springs.first()) {
        Day12.SpringCondition.Unknown -> calculateValidWorkingConditionsCount(springs, record) +
                recurse(springs.subList(1, springs.size), record)

        Day12.SpringCondition.Working -> calculateValidWorkingConditionsCount(springs, record)
        Day12.SpringCondition.Broken -> recurse(springs.subList(1, springs.size), record)
    }
}

private fun calculateValidWorkingConditionsCount(springs: List<Day12.SpringCondition>, record: List<Int>): Long {
    if (record.isEmpty()) {
        return 0L
    }
    val expectedWorkingCount = record.first()
    if (springs.size < expectedWorkingCount) {
        return 0L
    }
    if (springs.subList(0, expectedWorkingCount).all { it != Day12.SpringCondition.Broken }) {
        // if end of springs and end of record return 1
        return if (springs.size == expectedWorkingCount) {
            if (record.size == 1) 1L else 0L
        } else if (springs[expectedWorkingCount] != Day12.SpringCondition.Working) {
            Day12.part2SolveMemoized(
                springs.subList(expectedWorkingCount + 1, springs.size),
                record.subList(1, record.size)
            )
        } else {
            0L
        }
    }
    return 0L
}