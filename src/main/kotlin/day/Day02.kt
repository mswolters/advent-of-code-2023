package day

import kotlin.math.max

object Day02 : Day {
    override fun part1(input: List<String>): Result {
        return input.asSequence()
            .map { parseGame(it) }
            .mapIndexed { i, it -> (i + 1) to isGamePossible(12, 13, 14, it) }
            .filter { (_, possible) -> possible }
            .sumOf { (index, _) -> index }
            .asSuccess()
    }

    override fun part2(input: List<String>): Result {
        return input.map { parseGame(it) }
            .map { minimumInBag(it) }
            .sumOf { it.red * it.green * it.blue }
            .asSuccess()
    }

    private fun parseGame(line: String): List<Grab> {
        return line.substringAfter(':')
            .split(';')
            .map { parseGrab(it) }
    }

    private fun parseGrab(grabLine: String): Grab {
        val colors = grabLine.split(",")
            .map { grab -> grab.trim().split(' ') }
            .associate {
                (when (it[1]) {
                    "red" -> Color.Red
                    "green" -> Color.Green
                    "blue" -> Color.Blue
                    else -> throw Exception("Unexpected color: ${it[1]}")
                } to it[0].toInt())
            }
        return Grab(
            red = colors.getOrDefault(Color.Red, 0),
            green = colors.getOrDefault(Color.Green, 0),
            blue = colors.getOrDefault(Color.Blue, 0)
        )
    }

    private fun isGamePossible(maxRed: Int, maxGreen: Int, maxBlue: Int, game: List<Grab>): Boolean {
        return game.all { it.red <= maxRed && it.green <= maxGreen && it.blue <= maxBlue }
    }

    private fun minimumInBag(game: List<Grab>): Grab {
        return game.reduce { acc, grab ->
            Grab(
                max(acc.red, grab.red),
                max(acc.green, grab.green),
                max(acc.blue, grab.blue)
            )
        }
    }

    data class Grab(val red: Int, val green: Int, val blue: Int)
    enum class Color {
        Red, Green, Blue
    }

    override fun testData(): Day.TestData {
        return Day.TestData(
            8,
            2286,
            """Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green"""
                .lines()
        )
    }
}