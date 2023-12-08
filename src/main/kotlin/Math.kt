fun gcd(a: Int, b: Int): Int {
    var mutA = a
    var mutB = b
    while (mutB != 0) {
        val temp = mutA % mutB
        mutA = mutB
        mutB = temp
    }
    return mutA
}

fun lcm(a: Int, b: Int): Int {
    return (a * b) / gcd(a, b)
}

fun lcm(numbers: List<Int>): Int {
    return numbers.reduce { acc, i -> lcm(acc, i) }
}

fun gcd(a: Long, b: Long): Long {
    var mutA = a
    var mutB = b
    while (mutB != 0L) {
        val temp = mutA % mutB
        mutA = mutB
        mutB = temp
    }
    return mutA
}

fun lcm(a: Long, b: Long): Long {
    return (a * b) / gcd(a, b)
}

fun lcm(numbers: List<Long>): Long {
    return numbers.reduce { acc, i -> lcm(acc, i) }
}