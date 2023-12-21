package day

object Day20 : Day {
    override fun part1(input: List<String>): Result {
        val allModules = mutableMapOf<String, Module>()
        val pulseQueue = ArrayDeque<PulseData>()
        val state = State(allModules, pulseQueue)
        val button = Button(state)
        allModules["button"] = button
        allModules["output"] = BroadcastModule("output", listOf(), state)
        allModules["rx"] = BroadcastModule("rx", listOf(), state) //grrr
        val modules = input.map { parse(it, state) }
        modules.forEach { allModules[it.name] = it }
        modules.forEach { module -> module.outputs.forEach { allModules[it]!!.inputs.add(module.name) } }
        modules.forEach { it.setup() }

        repeat(1000) {
            button.sendPulse(Pulse.Low)
            while (pulseQueue.isNotEmpty()) {
                val (from, to, pulse) = pulseQueue.removeFirst()
                allModules[to]!!.processPulse(from, pulse)
            }
        }

        return (state.lows * state.highs).asSuccess()
    }

    override fun part2(input: List<String>): Result {
        // Apparently, go back from rx until you find no more conjunctions, find the cycle for those conjunctions, lcm
        return NotImplemented
    }

    fun parse(line: String, state: State): Module {
        val (nameAndType, outputString) = line.split(" -> ")
        val outputs = outputString.split(", ")
        return when {
            nameAndType == "broadcaster" -> BroadcastModule("broadcaster", outputs, state)
            nameAndType.startsWith("&") -> ConjunctionModule(nameAndType.substringAfter("&"), outputs, state)
            nameAndType.startsWith("%") -> FlipFlopModule(nameAndType.substringAfter("%"), outputs, state)
            else -> throw IllegalArgumentException("Unknown module type: $nameAndType")
        }
    }

    class State(val allModules: Map<String, Module>, val pulseQueue: ArrayDeque<PulseData>, var lows: Int = 0, var highs: Int = 0)

    sealed class Module(val name: String, val outputs: List<String>, val state: State, val inputs: MutableList<String> = mutableListOf()) {

        fun sendPulse(pulse: Pulse) {
            when (pulse) {
                Pulse.Low -> state.lows += outputs.size
                Pulse.High -> state.highs += outputs.size
            }
            outputs.forEach {
                //println("$name -$pulse> $it")
                state.pulseQueue.add(PulseData(name, it, pulse))
            }
        }

        abstract fun processPulse(from: String, pulse: Pulse)

        open fun setup() {}

    }

    class Button(state: State): Module("button", listOf("broadcaster"), state) {
        override fun processPulse(from: String, pulse: Pulse) {}

    }

    class BroadcastModule(name: String, outputs: List<String>, state: State) : Module(name, outputs, state) {
        override fun processPulse(from: String, pulse: Pulse) {
            sendPulse(pulse)
        }

    }

    class FlipFlopModule(name: String, outputs: List<String>, state: State): Module(name, outputs, state) {

        var lastOutput = Pulse.Low

        override fun processPulse(from: String, pulse: Pulse) {
            if (pulse == Pulse.High) return

            lastOutput = when (lastOutput) {
                Pulse.Low -> Pulse.High
                Pulse.High -> Pulse.Low
            }
            sendPulse(lastOutput)
        }

    }

    class ConjunctionModule(name: String, outputs: List<String>, state: State) : Module(name, outputs, state) {

        private val lastPulses = mutableMapOf<String, Pulse>()

        override fun processPulse(from: String, pulse: Pulse) {
            lastPulses[from] = pulse
            sendPulse(if (lastPulses.values.all { it == Pulse.High }) Pulse.Low else Pulse.High)
        }

        override fun setup() {
            inputs.forEach { lastPulses[it] = Pulse.Low }
        }

    }

    data class PulseData(val from: String, val to: String, val pulse: Pulse)

    enum class Pulse {
        Low, High
    }

    override fun testData(): Day.TestData {
        return Day.TestData(
            11687500,
            0,
            """broadcaster -> a
%a -> inv, con
&inv -> b
%b -> con
&con -> output"""
                .lines()
        )
    }
}