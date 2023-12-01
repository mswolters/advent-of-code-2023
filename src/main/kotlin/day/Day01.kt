package day

object Day01 : Day {

    override fun part1(input: List<String>): Result {
        return input.map { it.toCharArray().filter { c -> c.isDigit() } }
            .sumOf { chars -> "${chars.first()}${chars.last()}".toInt() }
            .asSuccess()
    }

    override fun part2(input: List<String>): Result {
        return input.map { replaceDigits(it) }
            .sumOf { chars -> "${chars.first()}${chars.last()}".toInt() }
            .asSuccess()
    }

    private val digits = mapOf(
        "zero" to "0",
        "one" to "1",
        "two" to "2",
        "three" to "3",
        "four" to "4",
        "five" to "5",
        "six" to "6",
        "seven" to "7",
        "eight" to "8",
        "nine" to "9",
    )
    private val replacementRegex = digits.keys.joinToString("|").let { "(?=($it|[0-9]))." }.toRegex()
    private fun replaceDigits(input: String): String {
        return replacementRegex.findAll(input).toList()
            .map { it.groups[1]!! }
            .joinToString(separator = "") { digits.getOrDefault(it.value, it.value) }
    }

    override fun testData(): Day.TestData {
        return Day.TestData(
            142,
            281,
            """1abc2
pqr3stu8vwx
a1b2c3d4e5f
treb7uchet"""
                .lines(),
            """two1nine
eightwothree
abcone2threexyz
xtwone3four
4nineeightseven2
zoneight234
7pqrstsixteen"""
                .lines()
        )
    }
}