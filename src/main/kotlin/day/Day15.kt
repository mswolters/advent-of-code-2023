package day

object Day15 : Day {
    override fun part1(input: List<String>): Result {
        return input.first()
            .split(",")
            .sumOf { hash(it) }
            .asSuccess()
    }

    fun hash(input: String): Int = input.map { it.code }
        .fold(0) { acc, it -> ((acc + it) * 17) % 256 }


    override fun part2(input: List<String>): Result {
        val boxes = List(256) { mutableListOf<Add>() }
        input.first()
            .split(",")
            .map { parseAction(it) }
            .forEach {
                when (it) {
                    is Add -> {
                        val indexOfMatching = boxes[it.label].indexOfFirst { lens -> lens.code == it.code }
                        if (indexOfMatching == -1) {
                            boxes[it.label].add(it)
                        } else {
                            boxes[it.label][indexOfMatching] = it
                        }
                    }
                    is Remove -> boxes[it.label].removeIf { lens -> lens.code == it.code }
                }
            }
        return boxes.flatMapIndexed { boxIndex: Int, lenses: MutableList<Add> -> lenses.mapIndexed { lensIndex, lens -> Triple(boxIndex, lensIndex, lens.strength) } }
            .sumOf { (boxIndex, lensIndex, focalPoint) -> (boxIndex + 1) * (lensIndex + 1) * focalPoint }
            .asSuccess()
    }

    fun parseAction(input: String): Action {
        if (input.contains('-')) {
            return Remove(input.substringBefore('-'))
        }
        val (code, strength) = input.split('=')
        return Add(code, strength.toInt())
    }

    sealed interface Action {
        val code: String
        val label: Int
            get() = hash(code)
    }

    data class Remove(override val code: String) : Action
    data class Add(override val code: String, val strength: Int) : Action

    override fun testData(): Day.TestData {
        return Day.TestData(
            1320,
            145,
            """rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7"""
                .lines()
        )
    }
}