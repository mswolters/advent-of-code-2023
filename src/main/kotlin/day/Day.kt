package day

import readInput

interface Day {

    fun part1(input: List<String>): Result
    fun part2(input: List<String>): Result

    fun testData(): TestData

    val name: String get() = this.javaClass.kotlin.simpleName!!
    fun input(): List<String> {
        return readInput(name)
    }

    data class TestData(val expected1: String, val expected2: String, val data: List<String>) {
        constructor(expected1: Int, expected2: Int, data: List<String>) : this(expected1.toString(), expected2.toString(), data)

    }

}

sealed interface Result

@JvmInline
value class Success(val result: String) : Result
fun <T> T.asSuccess() = Success("$this")
fun String.asSuccess() = Success(this)

data object NotImplemented : Result