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
        return NotImplemented
    }

    override fun testData(): Day.TestData {
        return Day.TestData(
            1320,
            0,
            """rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7"""
                .lines()
        )
    }
}