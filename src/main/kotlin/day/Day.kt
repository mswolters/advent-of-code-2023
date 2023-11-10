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

    data class TestData(val expected1: Int, val expected2: Int, val data: List<String>)

}

sealed interface Result

@JvmInline
value class Success(val result: Int) : Result
fun Int.asSuccess() = Success(this)

data object NotImplemented : Result