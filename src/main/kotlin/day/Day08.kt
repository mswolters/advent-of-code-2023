package day

import lcm
import java.math.BigInteger

object Day08 : Day {
    override fun part1(input: List<String>): Result {
        val actions = input[0].map {
            when (it) {
                'L' -> Action.Left
                'R' -> Action.Right
                else -> throw IllegalArgumentException("Unknown char $it")
            }
        }

        val nodes = parse(input.subList(2, input.size))

        val startCode = "AAA"
        val endCode = "ZZZ"

        var currentNode = nodes[startCode]!!
        var count = 0
        while (currentNode.code != endCode) {
            currentNode = when (actions[count.mod(actions.size)]) {
                Action.Left -> nodes[currentNode.left]!!
                Action.Right -> nodes[currentNode.right]!!
            }
            count++
        }
        return count.asSuccess()
    }

    override fun part2(input: List<String>): Result {
        val actions = input[0].map {
            when (it) {
                'L' -> Action.Left
                'R' -> Action.Right
                else -> throw IllegalArgumentException("Unknown char $it")
            }
        }

        val nodes = parse(input.subList(2, input.size))

        var currentNodes = nodes.filterKeys { it.endsWith('A') }.map { (_, node) -> node }
        val hitZ = MutableList(currentNodes.size) { -1L }
        var count = 0L
        while (hitZ.any { it == -1L }) {
            val action = actions[count.mod(actions.size)]
            count++
            currentNodes = currentNodes.mapIndexed { index, it ->
                val nextNode = when (action) {
                    Action.Left -> nodes[it.left]!!
                    Action.Right -> nodes[it.right]!!
                }
                if (nextNode.code.endsWith("Z") && hitZ[index] == -1L) {
                    hitZ[index] = count
                }
                nextNode
            }
        }

        return lcm(actions.size.toLong(), lcm(hitZ)).asSuccess()
    }

    fun parse(input: List<String>): Map<String, Node> {
        val regex = """(?<code>\w{3}) = \((?<left>\w{3}), (?<right>\w{3})\)""".toRegex()
        return input.map {
            val result = regex.matchEntire(it)!!
            Node(result.groups["code"]!!.value, result.groups["left"]!!.value, result.groups["right"]!!.value)
        }.associateBy { it.code }
    }

    data class Node(val code: String, val left: String, val right: String)
    enum class Action(val char: Char) {
        Left('L'),
        Right('R')
    }

    override fun testData(): Day.TestData {
        return Day.TestData(
            2,
            6,
            """RL

AAA = (BBB, CCC)
BBB = (DDD, EEE)
CCC = (ZZZ, GGG)
DDD = (DDD, DDD)
EEE = (EEE, EEE)
GGG = (GGG, GGG)
ZZZ = (ZZZ, ZZZ)"""
                .lines(),
            """LR

11A = (11B, XXX)
11B = (XXX, 11Z)
11Z = (11B, XXX)
22A = (22B, XXX)
22B = (22C, 22C)
22C = (22Z, 22Z)
22Z = (22B, 22B)
XXX = (XXX, XXX)"""
                .lines()
        )
    }
}