import day.*

fun main(args: Array<String>) {
    val days = listOf(
        Day01, Day02, Day03, Day04, Day05,
        Day06, Day07, Day08, Day09, Day10,
        Day11, Day12, Day13, Day14, Day15,
        Day16, Day17, Day18, Day19, Day20,
        Day21, Day22, Day23, Day24, Day25,
    )

    days.forEach { testAndRunOnSuccess(it) }
}

fun testAndRunOnSuccess(day: Day) {
    if (test(day)) {
        run(day)
    }
}

fun test(day: Day): Boolean {
    val testData = day.testData()
    when (val result1 = day.part1(testData.data)) {
        NotImplemented -> {}
        is Success -> if (testData.expected1 != result1.result) {
            println("${day.name}: 1: Expected: ${testData.expected1}, result: ${result1.result}")
            return false
        }

    }
    when (val result2 = day.part2(testData.data2)) {
        NotImplemented -> {}
        is Success -> if (testData.expected2 != result2.result) {
            println("${day.name}: 2: Expected: ${testData.expected2}, result: ${result2.result}")
            return false
        }
    }
    return true
}

fun run(day: Day) {
    val input = day.input()
    val part1Result = day.part1(input)
    if (part1Result is Success) {
        println("${day.name} Part 1: ${part1Result.result}")
    }
    val part2Result = day.part2(input)
    if (part2Result is Success) {
        println("${day.name} Part 2: ${part2Result.result}")
    }
}