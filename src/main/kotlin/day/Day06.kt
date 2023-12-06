package day

import asLongs

object Day06 : Day {
    override fun part1(input: List<String>): Result {
        return parse(input)
            .map { race -> allDistancesInRace(race).filter { it > race.distance } }
            .map { it.count() }
            .fold(1) { acc, it -> acc * it }
            .asSuccess()
    }

    override fun part2(input: List<String>): Result {
        val nonWhitespace = "\\s+".toRegex()
        return parse(input.map { nonWhitespace.replace(it, "") })
            .map { race -> allDistancesInRace(race).filter { it > race.distance } }
            .map { it.count() }
            .first()
            .asSuccess()
    }

    fun parse(input: List<String>): List<Race> {
        val times = input[0].substringAfter("Time:").trim().split("\\s+".toRegex()).asLongs()
        val distances = input[1].substringAfter("Distance:").trim().split("\\s+".toRegex()).asLongs()
        return times.zip(distances) { time, distance -> Race(time, distance) }
    }

    fun distanceTravelledInTime(totalTime: Long, buttonPressTime: Long): Long =
        (totalTime - buttonPressTime) * buttonPressTime

    fun allDistancesInRace(race: Race): List<Long> = (1..<race.time).map { distanceTravelledInTime(race.time, it) }

    data class Race(val time: Long, val distance: Long)

    override fun testData(): Day.TestData {
        return Day.TestData(
            288,
            71503,
            """Time:      7  15   30
Distance:  9  40  200"""
                .lines()
        )
    }
}