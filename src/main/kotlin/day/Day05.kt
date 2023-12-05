package day

import asLongs
import split
import kotlin.streams.asStream

object Day05 : Day {
    override fun part1(input: List<String>): Result {
        val (seeds, maps) = parse(input)

        return seeds.minOf { maps.location(it) }.asSuccess()
    }

    override fun part2(input: List<String>): Result {
        val (seedsRanges, maps) = parse(input)
        return seedsRanges.asSequence()
            .chunked(2)
            .flatMap { (start, length) -> var num = start; generateSequence { num++.takeIf { it < start + length } } }
            .asStream().parallel()
            .map { maps.location(it) }
            .min(Comparator.naturalOrder())
            .get()
            .asSuccess()
    }

    fun parse(input: List<String>): Pair<List<Long>, Maps> {
        val seeds = input[0].substringAfter("seeds: ").split(' ').asLongs()

        val rest = input.subList(2, input.size).filter { it.isNotEmpty() }
        val maps = rest.split { it.endsWith(':') }.filter { it.any() }.map { Map(it.map(::parseEntry)) }
        return Pair(seeds, Maps(maps))
    }

    fun parseEntry(input: String): MapEntry {
        val longs = input.split(' ').asLongs()
        return MapEntry(longs[0], longs[1], longs[2])
    }

    data class MapEntry(val destinationStart: Long, val sourceStart: Long, val length: Long) {
        companion object {
            val default = MapEntry(0, 0, Long.MAX_VALUE)
        }
    }

    class Map(val entries: List<MapEntry>) {
        fun destination(source: Long): Long {
            val entry =
                entries.firstOrNull { source in it.sourceStart..<it.sourceStart + it.length } ?: MapEntry.default
            val depthInEntry = source - entry.sourceStart
            return entry.destinationStart + depthInEntry
        }
    }

    class Maps(
        val seedToSoil: Map,
        val soilToFertilizer: Map,
        val fertilizerToWater: Map,
        val waterToLight: Map,
        val lightToTemperature: Map,
        val temperatureToHumidity: Map,
        val humidityToLocation: Map
    ) {
        constructor(allMaps: List<Map>) : this(
            allMaps[0],
            allMaps[1],
            allMaps[2],
            allMaps[3],
            allMaps[4],
            allMaps[5],
            allMaps[6]
        )

        val allMaps = listOf(
            seedToSoil,
            soilToFertilizer,
            fertilizerToWater,
            waterToLight,
            lightToTemperature,
            temperatureToHumidity,
            humidityToLocation
        )

        fun location(seed: Long): Long {
            return allMaps.fold(seed) { acc, map -> map.destination(acc) }
        }
    }

    override fun testData(): Day.TestData {
        return Day.TestData(
            35,
            46,
            """seeds: 79 14 55 13

seed-to-soil map:
50 98 2
52 50 48

soil-to-fertilizer map:
0 15 37
37 52 2
39 0 15

fertilizer-to-water map:
49 53 8
0 11 42
42 0 7
57 7 4

water-to-light map:
88 18 7
18 25 70

light-to-temperature map:
45 77 23
81 45 19
68 64 13

temperature-to-humidity map:
0 69 1
1 0 69

humidity-to-location map:
60 56 37
56 93 4"""
                .lines()
        )
    }
}