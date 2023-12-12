package day

import asInts
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
        return NotImplemented
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