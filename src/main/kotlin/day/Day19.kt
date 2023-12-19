package day

import split

object Day19 : Day {
    override fun part1(input: List<String>): Result {
        val (workflowStrings, partStrings) = input.split("")
        val workflows = workflowStrings.map { parseWorkflow(it) }
            .plus(arrayOf(AcceptedWorkflow("A"), RejectedWorkflow("R")))
            .associateBy { it.name }
        val parts = partStrings.map { parsePart(it) }

        return parts.filter { it.isAccepted(workflows) }.sumOf { with(it) { x + m + a + s } }.asSuccess()
    }

    fun Part.isAccepted(workflows: Map<String, Workflow>): Boolean {
        var workflow = workflows["in"]!!
        while (true) {
            when (workflow) {
                is AcceptedWorkflow -> return true
                is ListWorkflow -> workflow = workflows[workflow.next(this)]!!
                is RejectedWorkflow -> return false
            }
        }
    }

    fun ListWorkflow.next(part: Part): String {
        operations.forEach {
            when (it) {
                is CompareOperation -> {
                    if (it.biggerThan && part[it.category] > it.value) {
                        return it.whenTrue
                    } else if (!it.biggerThan && part[it.category] < it.value) {
                        return it.whenTrue
                    }
                }

                is ReferenceOperation -> return it.to
            }
        }
        throw IllegalStateException("Reached end of operations: $operations")
    }

    fun parseWorkflow(line: String): ListWorkflow {
        val (name, stepsString) = line.split("{", "}")
        val operations = stepsString.split(",").map { parseStep(it) }
        return ListWorkflow(name, operations)
    }

    fun parseStep(step: String): Operation {
        if (!step.contains(":")) {
            return ReferenceOperation(step)
        }

        val biggerThan = step.contains(">")
        val (categoryName, value, whenTrue) = step.split("<", ">", ":")
        val category = when (categoryName) {
            "x" -> Category.X
            "m" -> Category.M
            "a" -> Category.A
            "s" -> Category.S
            else -> throw IllegalArgumentException("Unknown category $categoryName")
        }

        return CompareOperation(category, biggerThan, value.toInt(), whenTrue)
    }

    fun parsePart(line: String): Part {
        val (x, m, a, s) = line.removeSurrounding("{", "}").split(",").map { it.substringAfter("=") }.map { it.toInt() }
        return Part(x, m, a, s)
    }

    override fun part2(input: List<String>): Result {
        val workflowStrings = input.takeWhile { it.isNotEmpty() }
        val workflows = workflowStrings.map { parseWorkflow(it) }
            .plus(arrayOf(AcceptedWorkflow("A"), RejectedWorkflow("R")))
            .associateBy { it.name }

        val acceptedRange = AcceptedRange(1..4000, 1..4000, 1..4000, 1..4000)
        val acceptedRanges = calculateAcceptedRanges(acceptedRange, workflows["in"] as ListWorkflow, workflows)

        return acceptedRanges.sumOf {
            it.allRanges.map { range -> (range.last - range.first + 1).toLong() }.reduce { acc, i -> acc * i }
        }.asSuccess()
    }

    fun calculateAcceptedRanges(
        within: AcceptedRange,
        workflow: Workflow,
        workflows: Map<String, Workflow>
    ): List<AcceptedRange> {
        if (within.allRanges.any { it.first > it.last }) {
            return emptyList()
        }
        return when (workflow) {
            is AcceptedWorkflow -> listOf(within)
            is RejectedWorkflow -> listOf()
            is ListWorkflow -> calculateAcceptedRanges(within, workflow.operations, workflows)
        }
    }

    fun calculateAcceptedRanges(
        within: AcceptedRange,
        nextOperations: List<Operation>,
        workflows: Map<String, Workflow>
    ): List<AcceptedRange> {
        when (val operation = nextOperations.first()) {
            is CompareOperation -> {
                val (lower, higher) = within.split(
                    operation.category,
                    if (operation.biggerThan) operation.value + 1 else operation.value
                )
                return if (operation.biggerThan) {
                    listOf(calculateAcceptedRanges(higher, workflows[operation.whenTrue]!!, workflows), calculateAcceptedRanges(lower, nextOperations.subList(1, nextOperations.size), workflows)).flatten()
                } else {
                    listOf(calculateAcceptedRanges(lower, workflows[operation.whenTrue]!!, workflows), calculateAcceptedRanges(higher, nextOperations.subList(1, nextOperations.size), workflows)).flatten()
                }
            }

            is ReferenceOperation -> return calculateAcceptedRanges(
                within,
                workflows[operation.to]!!,
                workflows
            )


        }
    }

    sealed interface Operation
    data class CompareOperation(val category: Category, val biggerThan: Boolean, val value: Int, val whenTrue: String) :
        Operation

    data class ReferenceOperation(val to: String) : Operation

    sealed interface Workflow {
        val name: String
    }

    data class ListWorkflow(override val name: String, val operations: List<Operation>) : Workflow
    data class AcceptedWorkflow(override val name: String) : Workflow
    data class RejectedWorkflow(override val name: String) : Workflow

    data class Part(val x: Int, val m: Int, val a: Int, val s: Int) {
        operator fun get(category: Category): Int {
            return when (category) {
                Category.X -> x
                Category.M -> m
                Category.A -> a
                Category.S -> s
            }
        }
    }

    data class AcceptedRange(
        val x: IntRange,
        val m: IntRange,
        val a: IntRange,
        val s: IntRange
    ) {
        val allRanges = listOf(x, m, a, s)

        operator fun get(category: Category): IntRange {
            return when (category) {
                Category.X -> x
                Category.M -> m
                Category.A -> a
                Category.S -> s
            }
        }

        fun copy(category: Category, value: IntRange): AcceptedRange {
            return when (category) {
                Category.X -> copy(x = value)
                Category.M -> copy(m = value)
                Category.A -> copy(a = value)
                Category.S -> copy(s = value)
            }
        }

        // creates 2 AcceptedRanges by splitting a category
        // The at value will be contained in the second range
        // If the split occurs outside the range, only 1 AcceptedRange will be returned
        fun split(category: Category, at: Int): List<AcceptedRange> {
            val rangeToSplit = this[category]
            val newRanges = listOf(
                IntRange(rangeToSplit.first, at - 1),
                IntRange(at, rangeToSplit.last)
            )
            return when (category) {
                Category.X -> newRanges.map { this.copy(x = it) }
                Category.M -> newRanges.map { this.copy(m = it) }
                Category.A -> newRanges.map { this.copy(a = it) }
                Category.S -> newRanges.map { this.copy(s = it) }
            }
        }
    }

    enum class Category {
        X, M, A, S
    }

    override fun testData(): Day.TestData {
        return Day.TestData(
            "19114",
            "167409079868000",
            """px{a<2006:qkq,m>2090:A,rfg}
pv{a>1716:R,A}
lnx{m>1548:A,A}
rfg{s<537:gd,x>2440:R,A}
qs{s>3448:A,lnx}
qkq{x<1416:A,crn}
crn{x>2662:A,R}
in{s<1351:px,qqz}
qqz{s>2770:qs,m<1801:hdj,R}
gd{a>3333:R,R}
hdj{m>838:A,pv}

{x=787,m=2655,a=1222,s=2876}
{x=1679,m=44,a=2067,s=496}
{x=2036,m=264,a=79,s=2244}
{x=2461,m=1339,a=466,s=291}
{x=2127,m=1623,a=2188,s=1013}"""
                .lines()
        )
    }
}